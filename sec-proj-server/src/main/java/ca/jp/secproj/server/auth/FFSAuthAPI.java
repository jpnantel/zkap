package ca.jp.secproj.server.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jp.secproj.crypto.zka.ffs.FFSAuthArbitrator;
import ca.jp.secproj.crypto.zka.ffs.FFSValidator;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound1DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound2DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound3DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSSetupDTO;
import ca.jp.secproj.server.persistence.IUserDb;

/**
 * Feige Fiat Shamir authentication service bean. Exposes methods to register
 * setup, generate challenge from witness and finally validates the response
 * built from the commitment, witness and challenge.
 * 
 * @author Jean-Philippe Nantel
 *
 */
@Path("/auth/ffs")
public class FFSAuthAPI {

    private static Logger logger = LoggerFactory.getLogger(FFSAuthAPI.class);

    private final static Long FFS_VALIDATOR_TTL = 600000l;

    private Map<String, FFSValidator> ffsValidators;

    private Map<String, Long> ffsValidatorsTTL;

    /**
     * Injected reference to the user persistence manager
     */
    private IUserDb userDb;

    private String serverId;

    public FFSAuthAPI() {
	this.ffsValidators = new HashMap<>();
	this.ffsValidatorsTTL = new HashMap<>();
    }

    @Path("/setup")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    /**
     * Registers the FFS Setup in the arbitrator
     * @param setup
     * @return
     */
    public Response setup(FFSSetupDTO setup) {
	if (setup == null || !FFSAuthArbitrator.validateSetupParameters(setup)) {
	    return Response.status(Status.BAD_REQUEST).entity("Invalid FFS setup parameters. ").build();
	}
	userDb.setFFSSetup(setup);
	return Response.status(Status.OK).entity("Successfully registered ffs setup").build();
    }

    @Path("/setup/{proverid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Registers the FFS Setup in the arbitrator
     * @param setup
     * @return
     */
    public Response setup(@PathParam("proverid") String proverId) {
	if (StringUtils.isBlank(proverId)) {
	    return Response.status(Status.BAD_REQUEST).entity("Invalid prover id. ").build();
	}
	try {
	    FFSSetupDTO setup = userDb.getFFSSetup(proverId);
	    return Response.status(Status.OK).entity(setup).build();
	} catch (IOException e) {
	    logger.warn("userdb error: ", e);
	    return Response
		    .status(Status.INTERNAL_SERVER_ERROR)
		    .entity("Exception when retrieving setup. Have you registered before? "
			    + "Consider calling ~/auth/ffs/setup").build();
	}
    }

    @Path("/challenge")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response challenge(FFSRound1DTO r1) {
	if (r1 == null || StringUtils.isBlank(r1.getProverId())) {
	    return Response.status(Status.BAD_REQUEST).entity("Invalid FFS witness (round 1) ").build();
	}
	FFSSetupDTO setup;
	try {
	    setup = userDb.getFFSSetup(r1.getProverId());
	} catch (IOException e) {
	    logger.warn("userdb error: ", e);
	    return Response
		    .status(Status.INTERNAL_SERVER_ERROR)
		    .entity("Exception when retrieving setup. Have you registered before? "
			    + "Consider calling ~/auth/ffs/setup").build();
	}
	if (setup == null) {
	    logger.info("Could not find setup for user: " + r1.getProverId());
	    return Response
		    .status(Status.INTERNAL_SERVER_ERROR)
		    .entity("Unable to retrieve setup. Have you registered before? "
			    + "Consider calling ~/auth/ffs/setup").build();
	}
	FFSValidator validator = new FFSValidator(r1.getProverId(), this.serverId, setup.getN(), setup.getNbRounds(),
		setup.getPublicKey());

	ffsValidators.put(r1.getProverId(), validator);
	ffsValidatorsTTL.put(r1.getProverId(), System.currentTimeMillis() + FFS_VALIDATOR_TTL);

	FFSRound2DTO r2 = validator.generateChallenge(r1);
	if (r2 == null) {
	    logger.info("Error while generating challenge. ");
	    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error while generating challenge. ").build();
	}
	return Response.status(Status.OK).entity(r2).build();
    }

    @Path("/validate")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validate(FFSRound3DTO r3) {
	if (r3 == null || StringUtils.isBlank(r3.getProverId())) {
	    return Response.status(Status.BAD_REQUEST).entity("Invalid FFS witness (round 3) ").build();
	}
	FFSValidator validator = ffsValidators.get(r3.getProverId());
	if (validator == null) {
	    logger.info("FFSValidator object not found. ");
	    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("FFSValidator object not found. ").build();
	}
	Long ttl = ffsValidatorsTTL.get(r3.getProverId());
	if (ttl == null || System.currentTimeMillis() > ttl) {
	    logger.info("FFSValidator object expired. ");
	    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("FFSValidator object expired. ").build();
	}

	if (validator.authenticate(r3)) {
	    return Response.status(Status.OK).entity("You successfully identified yourself buddy! ").build();
	} else {
	    logger.info("FFSValidator object expired. ");
	    return Response.status(Status.UNAUTHORIZED).entity("Authentication failed. ").build();
	}
    }

    public IUserDb getUserDb() {
	return userDb;
    }

    public void setUserDb(IUserDb userDb) {
	this.userDb = userDb;
    }

    public String getServerId() {
	return serverId;
    }

    public void setServerId(String serverId) {
	this.serverId = serverId;
    }

}

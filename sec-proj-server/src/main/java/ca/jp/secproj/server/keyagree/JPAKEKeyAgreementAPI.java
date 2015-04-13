package ca.jp.secproj.server.keyagree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound2Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound3Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jp.secproj.server.persistence.IUserDb;
import ca.jp.secproj.utils.crypto.zka.JPAKEKeyAgreementHelper;
import ca.jp.secproj.utils.crypto.zka.JPAKERound1PayloadDTO;
import ca.jp.secproj.utils.crypto.zka.JPAKERound2PayloadDTO;
import ca.jp.secproj.utils.crypto.zka.JPAKERound3PayloadDTO;

@Path("/keyagree/jpake")
public class JPAKEKeyAgreementAPI {

    private static Logger logger = LoggerFactory.getLogger(JPAKEKeyAgreementAPI.class);

    private final static Long JPAKE_PARTICIPANT_TTL = 600000l;

    private Map<String, JPAKEKeyAgreementHelper> jPakeKAHelper;

    private Map<String, Long> jPakeKAHelperTTL;

    private IUserDb userDb;

    public JPAKEKeyAgreementAPI() {

	this.jPakeKAHelper = new HashMap<>();
	this.jPakeKAHelperTTL = new HashMap<>();

    }

    @Path("/savesecret")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveSecret(String userSecret, @HeaderParam("user") String user) {
	try {
	    if (StringUtils.isBlank(user)) {
		logger.info("User not valid");
		return Response.status(Status.BAD_REQUEST).entity("Please supply a valid user in the user header. ")
			.build();
	    }
	    try {
		userDb.setJPAKESecret(user, userSecret);
	    } catch (IOException e) {
		logger.error("Error retrieving user secret.", e);
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error retrieving user secret.").build();
	    }

	    return Response.status(Status.OK).build();
	} catch (Exception e) {
	    logger.warn("Unknown error in round 1 post. ", e);
	    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown error in round 1 post. ").build();
	}
    }

    @Path("/firstround")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response firstRound(JPAKERound1PayloadDTO clientRound1Payload, @HeaderParam("user") String user) {
	try {
	    if (StringUtils.isBlank(user)) {
		logger.info("User not valid");
		return Response.status(Status.BAD_REQUEST).entity("Please supply a valid user in the user header. ")
			.build();
	    }
	    String userJPAKESecret = null;
	    try {
		userJPAKESecret = userDb.getJPAKESecret(user);
	    } catch (IOException e) {
		logger.error("Error retrieving user secret.", e);
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error retrieving user secret.").build();
	    }

	    JPAKEKeyAgreementHelper kaHelper = new JPAKEKeyAgreementHelper(user + "_server", userJPAKESecret);

	    JPAKERound1Payload serverRound1Payload = kaHelper.getRound1Payload();
	    kaHelper.validateRound1(clientRound1Payload.createPayload());

	    jPakeKAHelper.put(user, kaHelper);
	    jPakeKAHelperTTL.put(user, System.currentTimeMillis() + JPAKE_PARTICIPANT_TTL);

	    return Response.status(Status.OK).entity(new JPAKERound1PayloadDTO(serverRound1Payload)).build();
	} catch (Exception e) {
	    logger.warn("Unknown error in round 1 post. ", e);
	    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown error in round 1 post. ").build();
	}
    }

    @Path("/secondround")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response secondRound(JPAKERound2PayloadDTO clientRound2Payload, @HeaderParam("user") String user) {
	try {
	    if (StringUtils.isBlank(user)) {
		logger.info("User not valid");
		return Response.status(Status.BAD_REQUEST).entity("Please supply a valid user in the user header. ")
			.build();
	    }

	    JPAKEKeyAgreementHelper kaHelper = jPakeKAHelper.get(user);
	    if (kaHelper == null) {
		logger.info("JPAKEParticipant object not found. ");
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity("JPAKEParticipant object not found. ")
			.build();
	    }

	    Long participantTTl = jPakeKAHelperTTL.get(user);
	    if (participantTTl == null || System.currentTimeMillis() > participantTTl) {
		logger.info("JPakeParticipant expired! Authentication process aborted. ");
		return Response.status(Status.INTERNAL_SERVER_ERROR)
			.entity("JPakeParticipant expired! Authentication process aborted. ").build();
	    }

	    JPAKERound2Payload serverRound2Payload = kaHelper.getRound2Payload();
	    kaHelper.validateRound2(clientRound2Payload.createPayload());

	    return Response.status(Status.OK).entity(new JPAKERound2PayloadDTO(serverRound2Payload)).build();
	} catch (Exception e) {
	    logger.warn("Unknown error in round 2 post. ", e);
	    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown error in round 2 post. ").build();
	}
    }

    @Path("/thirdround")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response thirdRound(JPAKERound3PayloadDTO clientRound3Payload, @HeaderParam("user") String user) {
	try {
	    if (StringUtils.isBlank(user)) {
		logger.info("User not valid");
		return Response.status(Status.BAD_REQUEST).entity("Please supply a valid user in the user header. ")
			.build();
	    }

	    JPAKEKeyAgreementHelper kaHelper = jPakeKAHelper.get(user);
	    if (kaHelper == null) {
		logger.info("JPAKEParticipant object not found. ");
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity("JPAKEParticipant object not found. ")
			.build();
	    }

	    Long participantTTl = jPakeKAHelperTTL.get(user);
	    if (participantTTl == null || System.currentTimeMillis() > participantTTl) {
		logger.info("JPakeParticipant expired! Authentication process aborted. ");
		return Response.status(Status.INTERNAL_SERVER_ERROR)
			.entity("JPakeParticipant expired! Authentication process aborted. ").build();
	    }

	    JPAKERound3Payload serverRound3Payload = kaHelper.getRound3Payload();
	    kaHelper.validateRound3(clientRound3Payload.createPayload());

	    jPakeKAHelper.remove(user);
	    jPakeKAHelperTTL.remove(user);

	    userDb.setSecretKey(user, kaHelper.getRawAES128Key());

	    return Response.status(Status.OK).entity(new JPAKERound3PayloadDTO(serverRound3Payload)).build();
	} catch (Exception e) {
	    logger.warn("Unknown error in round 2 post. ", e);
	    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown error in round 2 post. ").build();
	}
    }

    public IUserDb getUserDb() {
	return userDb;
    }

    public void setUserDb(IUserDb userDb) {
	this.userDb = userDb;
    }

}

package ca.jp.secproj.server.comm;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jp.secproj.crypto.symkey.AESUtils;
import ca.jp.secproj.server.keyagree.JPAKEKeyAgreementAPI;
import ca.jp.secproj.server.persistence.IUserDb;

/**
 * Toy service API used to demonstrate a successful key exchange. Exposes the
 * "query oracle" service.
 * 
 * @author Jean-Philippe Nantel
 *
 */
@Path("/comm/oracle")
public class Oracle {

    private static Logger logger = LoggerFactory.getLogger(JPAKEKeyAgreementAPI.class);

    /**
     * Injected reference to the user persistence manager
     */
    private IUserDb userDb;

    /**
     * Query oracle service that, given a common AES-128 key, deciphers the
     * senders message, wraps it into its own message, reciphers it and then
     * sends it.
     * 
     * @param message
     * @param user
     * @return
     */
    @Path("/query")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response query(String message, @HeaderParam("user") String user) {
	try {
	    if (StringUtils.isBlank(user)) {
		logger.info("User not valid");
		return Response.status(Status.BAD_REQUEST).entity("Please supply a valid user in the user header. ")
			.build();
	    }
	    if (message == null) {
		logger.info("No message supplied. ");
		return Response.status(Status.NO_CONTENT).entity("No message supplied. ").build();
	    }
	    byte[] userKey = userDb.getJPAKENegotiatedSecretKey(user);
	    if (userKey == null) {
		logger.info("Unable to retrieve shared secret key from datastore. ");
		return Response.status(Status.INTERNAL_SERVER_ERROR)
			.entity("Unable to retrieve shared secret key from datastore. ").build();
	    }

	    AESUtils aesHelper = new AESUtils(userKey);

	    String decipheredMsg = aesHelper.deCipherString(message);

	    String serverResponse = "You said " + decipheredMsg + " , but I say why? ";
	    return Response.status(Status.OK).entity(aesHelper.cipherString(serverResponse)).build();

	} catch (Exception e) {
	    logger.warn("Unknown error oracle query post. ", e);
	    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown error in round 1 post. ").build();
	}
    }

    public IUserDb getUserDb() {
	return userDb;
    }

    public void setUserDb(IUserDb userDb) {
	this.userDb = userDb;
    }
}
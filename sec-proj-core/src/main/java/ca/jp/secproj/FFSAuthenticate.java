package ca.jp.secproj;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.jp.secproj.crypto.zka.ffs.FFSProver;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound1DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound2DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound3DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSSetupDTO;
import ca.jp.secproj.utils.log.Loggable;
import ca.jp.secproj.utils.net.ConnectionClientHelper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
@Loggable
@Component("FFSAuthenticate")
public class FFSAuthenticate implements SecProjExecutable {

    private static Logger logger;

    @Autowired
    private String serverURL;

    @Autowired
    private String serverId;

    @Autowired
    private String ffsPath;

    @Autowired
    private Client jerseyClient = null;

    public void execute(String username, String password) {
	MessageDigest md = null;
	try {
	    md = MessageDigest.getInstance("SHA-256");
	} catch (NoSuchAlgorithmException e1) {
	    logger.error("Can't instantiate SHA-256: ", e1);
	    return;
	}

	try {
	    md.update(password.getBytes("UTF-8"));
	} catch (UnsupportedEncodingException e1) {
	    logger.error("Cannot get bytes as UTF-8 string from supplied password", e1);
	    return;
	}
	byte[] privateKey = md.digest();

	WebResource wr = jerseyClient.resource(serverURL + ffsPath + "/setup/" + username);
	FFSSetupDTO setup = null;
	try {
	    setup = wr.get(FFSSetupDTO.class);
	} catch (UniformInterfaceException e) {
	    logger.warn("Error doing get (executeGet)", e);
	    return;
	}

	FFSProver prover = new FFSProver(username, serverId, setup.getN(), setup.getNbRounds(), privateKey);

	FFSRound1DTO r1 = prover.getWitness();

	FFSRound2DTO r2 = null;
	try {
	    r2 = ConnectionClientHelper.postToServer(jerseyClient, r1, FFSRound2DTO.class, serverURL + ffsPath
		    + "/challenge");
	} catch (IOException e) {
	    logger.error("Error persisting secret on server. ", e);
	    return;
	}

	FFSRound3DTO r3 = prover.getResponse(r2);
	try {
	    String authMsg = ConnectionClientHelper.postToServer(jerseyClient, r3, String.class, serverURL + ffsPath
		    + "/validate");
	    logger.info("Auth msg: " + authMsg);
	} catch (IOException e) {
	    logger.error("Error persisting secret on server. ", e);
	    return;
	}
    }

}

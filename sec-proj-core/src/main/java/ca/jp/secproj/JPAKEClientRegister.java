package ca.jp.secproj;

import java.io.IOException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.jp.secproj.utils.log.Loggable;
import ca.jp.secproj.utils.net.ConnectionClientHelper;

import com.sun.jersey.api.client.Client;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
@Loggable
@Component("JPAKEClientRegister")
public class JPAKEClientRegister implements SecProjExecutable {

    private static Logger logger;

    @Autowired
    private String serverURL;

    @Autowired
    private String jPakePath;

    @Autowired
    private Client jerseyClient = null;

    public void execute(String username, String password) {

	try {
	    ConnectionClientHelper.postToServer(jerseyClient, password, String.class, serverURL + jPakePath
		    + "/savesecret", username);
	} catch (IOException e) {
	    logger.error("Error persisting secret on server. ", e);
	    return;
	}
	logger.info("Sucessfully registered user's password.");
    }
}

package ca.jp.secproj;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import ca.jp.secproj.utils.log.Loggable;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

@Loggable
@Component("StdAuthDemo")
public class StdAuthDemo implements SecProjExecutable {

    private static Logger logger;

    @Autowired
    private String serverURL;

    @Autowired
    private String stdAuthPath;

    @Autowired
    private Client jerseyClient;

    public void execute(String username, String password) {

	long start = System.currentTimeMillis();

	String authHeader = buildAuthHeader(username, "");
	String salt = executeGet(serverURL + stdAuthPath + "/initauth", authHeader);

	String hash = BCrypt.hashpw(password, salt);

	String authHeader2 = buildAuthHeader(username, hash);
	String token = executeGet(serverURL + stdAuthPath + "/checkauth", authHeader2);

	long total = System.currentTimeMillis() - start;

	logger.info("Authorization V1: salt  fetched on server.");
	logger.info("Salt: " + salt);
	logger.info("Hash: " + hash);
	logger.info("Time: " + total);
    }

    private String executeGet(String url, String authHeader) {
	WebResource wr = jerseyClient.resource(url);
	try {
	    return wr.header("Authorization", authHeader).get(String.class);
	} catch (UniformInterfaceException e) {
	    logger.warn("Error doing get (executeGet)", e);
	}
	return null;
    }

    private String buildAuthHeader(String user, String pw) {
	try {
	    return "Basic " + new String(Base64.encode(user + ":" + pw), "UTF-8");
	} catch (UnsupportedEncodingException e) {
	    logger.warn("", e);
	    return null;
	}
    }

    public String getServerURL() {
	return serverURL;
    }

    public void setServerURL(String serverURL) {
	this.serverURL = serverURL;
    }

    public String getStdAuthPath() {
	return stdAuthPath;
    }

    public void setStdAuthPath(String stdAuthPath) {
	this.stdAuthPath = stdAuthPath;
    }

    public Client getJerseyClient() {
	return jerseyClient;
    }

    public void setJerseyClient(Client jerseyClient) {
	this.jerseyClient = jerseyClient;
    }

}

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
@Component("horcsStdAuthTestSaltMem")
public class StdAuthTestSaltMem implements AuthTest {

    private static Logger logger;

    @Autowired
    private String clientUserName;

    @Autowired
    private String clientSalt;

    @Autowired
    private String clientPassword;

    @Autowired
    private String stdAuthUrl;

    @Autowired
    private Client jerseyClient;

    public String executeAuth() {

        long start = System.currentTimeMillis();
        String hash = BCrypt.hashpw(clientPassword, clientSalt);

        String authHeader2 = buildAuthHeader(clientUserName, hash);
        String token = executeGet(stdAuthUrl + "/checkauth", authHeader2);

        long total = System.currentTimeMillis() - start;

        logger.info("Authorization V1: salt persisted on client side. ");
        logger.info("Salt: " + clientSalt);
        logger.info("Hash: " + hash);
        logger.info("Time: " + total);

        return token;
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

    public String getClientUserName() {
        return clientUserName;
    }

    public void setClientUserName(String clientUserName) {
        this.clientUserName = clientUserName;
    }

    public String getClientSalt() {
        return clientSalt;
    }

    public void setClientSalt(String clientSalt) {
        this.clientSalt = clientSalt;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public String getStdAuthUrl() {
        return stdAuthUrl;
    }

    public void setStdAuthUrl(String stdAuthUrl) {
        this.stdAuthUrl = stdAuthUrl;
    }

    public Client getJerseyClient() {
        return jerseyClient;
    }

    public void setJerseyClient(Client jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

}

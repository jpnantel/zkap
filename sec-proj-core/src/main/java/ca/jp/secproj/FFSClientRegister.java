package ca.jp.secproj;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.jp.secproj.crypto.zka.ffs.FFSAuthArbitrator;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSSetupDTO;
import ca.jp.secproj.utils.log.Loggable;
import ca.jp.secproj.utils.net.ConnectionClientHelper;

import com.sun.jersey.api.client.Client;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
@Loggable
@Component("FFSClientRegister")
public class FFSClientRegister implements SecProjExecutable {

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

        int nbRounds = 100;
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

        FFSAuthArbitrator arbitrator = new FFSAuthArbitrator(username, serverId, nbRounds, privateKey);
        FFSSetupDTO setup = arbitrator.GenerateFFSSetup();

        try {
            String authMsg = ConnectionClientHelper.postToServer(jerseyClient, setup, String.class, serverURL + ffsPath
                    + "/setup");
            logger.info("Auth msg: " + authMsg);
        } catch (IOException e) {
            logger.error("Error persisting secret on server. ", e);
            return;
        }

    }

}

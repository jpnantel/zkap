package ca.jp.secproj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound2Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound3Payload;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.jp.secproj.crypto.symkey.AESUtils;
import ca.jp.secproj.crypto.zka.jpake.JPAKEKeyAgreementHelper;
import ca.jp.secproj.crypto.zka.jpake.JPAKERound1PayloadDTO;
import ca.jp.secproj.crypto.zka.jpake.JPAKERound2PayloadDTO;
import ca.jp.secproj.crypto.zka.jpake.JPAKERound3PayloadDTO;
import ca.jp.secproj.utils.log.Loggable;
import ca.jp.secproj.utils.net.ConnectionClientHelper;

import com.sun.jersey.api.client.Client;

@Loggable
@Component("jPakeKeyExchDemo")
public class JPAKEKeyExchDemo implements SecProjExecutable {

    private static Logger logger;

    @Autowired
    private String serverURL;

    @Autowired
    private String jPakePath;

    @Autowired
    private String oraclePath;

    @Autowired
    private Client jerseyClient = null;

    public void execute(String username, String password) {

	JPAKEKeyAgreementHelper jAuth = new JPAKEKeyAgreementHelper(username, password);

	if (executeRound1(jAuth) && executeRound2(jAuth) && executeRound3(jAuth)) {

	    AESUtils aesHelper = new AESUtils(jAuth.getRawAES128Key());

	    boolean loop = true;

	    System.out.print("Agreement with Oracle done. Beginning interactive communication. ");
	    System.out.print("Type \"exit\" to exit. ");

	    String userInput = null;
	    InputStreamReader isr = null;
	    BufferedReader br = null;
	    try {
		isr = new InputStreamReader(System.in);
		br = new BufferedReader(isr);
		while (loop) {
		    System.out.print("What is your message to the great Oracle : ");

		    userInput = br.readLine();
		    userInput = StringUtils.trim(userInput);

		    if (userInput.equals("exit")) {
			return;
		    }

		    String oracleResponse = getOracleResponse(aesHelper, userInput, username);
		    System.out.println("Oracle responded: " + oracleResponse);
		}
	    } catch (IOException e) {
		logger.error("System input error! ", e);
		return;
	    } finally {
		try {
		    if (br != null) {
			br.close();
		    }
		    if (isr != null) {
			isr.close();
		    }
		} catch (IOException e) {
		    logger.error("Terrible news, there was an error while freeing resources... ", e);
		}
	    }
	}
    }

    /**
     * Executes round 1 of the JPAKE key exchange
     * 
     * @param jAuth
     */
    private boolean executeRound1(JPAKEKeyAgreementHelper jAuth) {
	logger.info("************ Round 1 **************");

	JPAKERound1Payload clientRound1Payload = jAuth.getRound1Payload();

	logger.debug("Alice sends to Bob: ");
	logger.debug("g^{x1}=" + clientRound1Payload.getGx1().toString(16));
	logger.debug("g^{x2}=" + clientRound1Payload.getGx2().toString(16));
	logger.debug("KP{x1}={" + clientRound1Payload.getKnowledgeProofForX1()[0].toString(16) + "};{"
		+ clientRound1Payload.getKnowledgeProofForX1()[1].toString(16) + "}");
	logger.debug("KP{x2}={" + clientRound1Payload.getKnowledgeProofForX2()[0].toString(16) + "};{"
		+ clientRound1Payload.getKnowledgeProofForX2()[1].toString(16) + "}");

	JPAKERound1Payload serverRound1Payload;

	try {
	    serverRound1Payload = ConnectionClientHelper.postToServer(jerseyClient,
		    new JPAKERound1PayloadDTO(clientRound1Payload), JPAKERound1PayloadDTO.class,
		    serverURL + jPakePath + "/firstround", jAuth.getUsername()).createPayload();
	    logger.info("Server round 1 payload received successfully. ");
	} catch (IOException | ParseException e) {
	    logger.error("Round 1 server validation error. ", e);
	    return false;
	}

	logger.debug("Bob responds to Alice: ");
	logger.debug("g^{x3}=" + serverRound1Payload.getGx1().toString(16));
	logger.debug("g^{x4}=" + serverRound1Payload.getGx2().toString(16));
	logger.debug("KP{x3}={" + serverRound1Payload.getKnowledgeProofForX1()[0].toString(16) + "};{"
		+ serverRound1Payload.getKnowledgeProofForX1()[1].toString(16) + "}");
	logger.debug("KP{x4}={" + serverRound1Payload.getKnowledgeProofForX2()[0].toString(16) + "};{"
		+ serverRound1Payload.getKnowledgeProofForX2()[1].toString(16) + "}");

	logger.debug("Alice checks g^{x4}!=1: OK");
	logger.debug("Alice checks KP{x3}: OK");
	logger.debug("Alice checks KP{x4}: OK");
	logger.debug("Bob validated content ");

	if (jAuth.validateRound1(serverRound1Payload)) {
	    logger.info("Alice validated round 1 content successfully.");
	    return true;
	} else {
	    logger.error("Alice could not validate round 1. Key exchange failed.");
	    return false;
	}
    }

    /**
     * Executes round 2 of the JPAKE key exchange
     * 
     * @param jAuth
     */
    private boolean executeRound2(JPAKEKeyAgreementHelper jAuth) {
	logger.info("************ Round 2 **************");
	JPAKERound2Payload clientRound2Payload = jAuth.getRound2Payload();
	JPAKERound2Payload serverRound2Payload = null;

	try {
	    serverRound2Payload = ConnectionClientHelper.postToServer(jerseyClient,
		    new JPAKERound2PayloadDTO(clientRound2Payload), JPAKERound2PayloadDTO.class,
		    serverURL + jPakePath + "/secondround", jAuth.getUsername()).createPayload();
	    logger.info("Server round 2 payload received successfully. ");
	} catch (IOException | ParseException e) {
	    logger.error("Round 2 server validation error. ", e);
	    return false;
	}
	logger.debug("Alice sends to Bob: ");
	logger.debug("A=" + clientRound2Payload.getA().toString(16));
	logger.debug("KP{x2*s}={" + clientRound2Payload.getKnowledgeProofForX2s()[0].toString(16) + "},{"
		+ clientRound2Payload.getKnowledgeProofForX2s()[1].toString(16) + "}");
	logger.debug("");

	logger.debug("Bob sends to Alice");
	logger.debug("B=" + serverRound2Payload.getA().toString(16));
	logger.debug("KP{x4*s}={" + serverRound2Payload.getKnowledgeProofForX2s()[0].toString(16) + "},{"
		+ serverRound2Payload.getKnowledgeProofForX2s()[1].toString(16) + "}");

	if (jAuth.validateRound2(serverRound2Payload)) {
	    logger.info("Alice validated round 2 content successfully.");
	    return true;
	} else {
	    logger.error("Alice could not validate round 1. Key exchange failed.");
	    return false;
	}
    }

    /**
     * Executes round 3 of the JPAKE key exchange
     * 
     * @param jAuth
     */
    private boolean executeRound3(JPAKEKeyAgreementHelper jAuth) {
	logger.info("************ Round 3 **************");

	JPAKERound3Payload clientRound3Payload = jAuth.getRound3Payload();

	logger.debug("Alice sends to Bob: ");
	logger.debug("MacTag=" + clientRound3Payload.getMacTag().toString(16));
	JPAKERound3Payload serverRound3Payload = null;
	try {
	    serverRound3Payload = ConnectionClientHelper.postToServer(jerseyClient,
		    new JPAKERound3PayloadDTO(clientRound3Payload), JPAKERound3PayloadDTO.class,
		    serverURL + jPakePath + "/thirdround", jAuth.getUsername()).createPayload();
	} catch (IOException e) {
	    logger.error("Round 3 server validation error. ", e);
	    return false;
	}

	if (jAuth.validateRound3(serverRound3Payload)) {
	    logger.info("Alice validated round 3 content successfully.");
	    return true;
	} else {
	    logger.error("Alice could not validate round 3. Authentication failed.");
	    return false;
	}
    }

    /**
     * Encrypts a string sends it to the oracle REST service, decrypts server's
     * response and returns it.
     * 
     * @param encrypter
     * @param decrypter
     * @param userMessage
     * @param username
     * @return
     */
    private String getOracleResponse(AESUtils aesHelper, String userMessage, String username) {
	String cipheredMessage = aesHelper.cipherString(userMessage);
	if (cipheredMessage == null) {
	    return null;
	}

	String cipheredResponse = null;
	try {
	    cipheredResponse = ConnectionClientHelper.postToServer(jerseyClient, cipheredMessage, String.class,
		    serverURL + oraclePath + "/query", username);
	} catch (IOException e) {
	    logger.error("Error getting response from server: ", e);
	    return null;
	}
	if (cipheredResponse == null) {
	    return null;
	}
	return aesHelper.deCipherString(cipheredResponse);
    }

    public String getServerURL() {
	return serverURL;
    }

    public void setServerURL(String serverURL) {
	this.serverURL = serverURL;
    }

    public String getjPakePath() {
	return jPakePath;
    }

    public void setjPakePath(String jPakePath) {
	this.jPakePath = jPakePath;
    }

    public String getOraclePath() {
	return oraclePath;
    }

    public void setOraclePath(String oraclePath) {
	this.oraclePath = oraclePath;
    }

    public Client getJerseyClient() {
	return jerseyClient;
    }

    public void setJerseyClient(Client jerseyClient) {
	this.jerseyClient = jerseyClient;
    }
}

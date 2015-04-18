package ca.jp.secproj;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jp.secproj.crypto.zka.ffs.FFSAuthArbitrator;
import ca.jp.secproj.crypto.zka.ffs.FFSProver;
import ca.jp.secproj.crypto.zka.ffs.FFSValidator;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound1DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound2DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound3DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSSetupDTO;

public class FFSOfflineTest {

    private static Logger logger = LoggerFactory.getLogger(FFSOfflineTest.class);

    @Test
    public void executeFFSAuhSuccess() throws NoSuchAlgorithmException, UnsupportedEncodingException {
	String password = "hello";
	String proverId = "me";
	String validatorId = "you";
	int nbRounds = 100;

	MessageDigest md = MessageDigest.getInstance("SHA-256");

	md.update(password.getBytes("UTF-8"));
	byte[] privateKey = md.digest();

	FFSAuthArbitrator arbitrator = new FFSAuthArbitrator(proverId, validatorId, nbRounds, privateKey);

	FFSSetupDTO setup = arbitrator.GenerateFFSSetup();
	FFSProver prover = new FFSProver(proverId, validatorId, setup.getN(), nbRounds, privateKey);
	FFSValidator validator = new FFSValidator(proverId, validatorId, setup.getN(), nbRounds, setup.getPublicKey());

	FFSRound1DTO r1 = prover.getWitness();
	FFSRound2DTO r2 = validator.generateChallenge(r1);
	FFSRound3DTO r3 = prover.getResponse(r2);

	boolean auth = validator.authenticate(r3);
	logger.info("Authentication result expected: success");
	logger.info("Authentication result: " + (auth ? "Success" : "Fail"));

	Assert.assertTrue(auth);

    }

    @Test
    public void executeFFSAuhFail() throws NoSuchAlgorithmException, UnsupportedEncodingException {
	String password = "hello";
	String proverId = "me";
	String validatorId = "you";
	int nbRounds = 100;

	MessageDigest md = MessageDigest.getInstance("SHA-256");

	md.update(password.getBytes("UTF-8"));
	byte[] privateKey = md.digest();

	FFSAuthArbitrator arbitrator = new FFSAuthArbitrator(proverId, validatorId, nbRounds, privateKey);

	md.update("phony".getBytes("UTF-8"));
	privateKey = md.digest();

	FFSSetupDTO setup = arbitrator.GenerateFFSSetup();
	FFSProver prover = new FFSProver(proverId, validatorId, setup.getN(), nbRounds, privateKey);
	FFSValidator validator = new FFSValidator(proverId, validatorId, setup.getN(), nbRounds, setup.getPublicKey());

	FFSRound1DTO r1 = prover.getWitness();
	FFSRound2DTO r2 = validator.generateChallenge(r1);
	FFSRound3DTO r3 = prover.getResponse(r2);

	boolean auth = validator.authenticate(r3);

	logger.info("Authentication result expected: fail");
	logger.info("Authentication result: " + (auth ? "Success" : "Fail"));
	Assert.assertFalse(auth);

    }
}

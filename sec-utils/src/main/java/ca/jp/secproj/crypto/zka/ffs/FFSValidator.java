package ca.jp.secproj.crypto.zka.ffs;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound1DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound2DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound3DTO;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class FFSValidator {

    private static Logger logger = LoggerFactory.getLogger(FFSValidator.class);

    private static final BigInteger TWO = new BigInteger("2");

    private String proverId;

    private String validatorId;

    /**
     * Large number such that n= p*q, p and q being large prime numbers
     */
    private BigInteger n;

    /**
     * Number of rounds of FFS to execute (essentially the lenght of all
     * vectors: commitment, witness, challenge and response). There is a chance
     * = 1 / (2^nbRounds) that the prover can fool the validator without the
     * valid secret.
     */
    private int nbRounds;

    /**
     * Prover's public key
     */
    private BigInteger publicKey;

    /**
     * Witness sent by prover
     */
    private BigInteger[] w;

    /**
     * Challenge requested by prover
     */
    private boolean[] challenge;

    /**
     * Response sent by prover;
     */
    private BigInteger[] r;

    /**
     * Validator constructor requiring a big integer n = p*q where p and q are
     * large prime numbers, a number of rounds of the FFS protocol and the
     * provers public key.
     * 
     * @param n
     * @param nbRounds
     * @param publicKey
     */
    public FFSValidator(String proverId, String validatorId, String n, int nbRounds, String publicKey) {
	super();
	this.proverId = proverId;
	this.validatorId = validatorId;
	this.n = new BigInteger(n);
	this.nbRounds = nbRounds;
	this.publicKey = new BigInteger(publicKey);
    }

    /**
     * Generate a challenge for the prover and store the commitment
     * 
     * @param proverCommitment
     * @return
     */
    public FFSRound2DTO generateChallenge(FFSRound1DTO proverCommitment) {
	try {
	    this.w = proverCommitment.getWAsBigInt();
	} catch (ParseException e) {
	    logger.error("Unable to process commitment. ", e);
	    this.w = null;
	    return null;
	}
	if (this.w == null || this.w.length != nbRounds) {
	    this.w = null;
	    logger.info("Invalid commitment. ");
	    return null;
	}
	SecureRandom rng = new SecureRandom();
	this.challenge = new boolean[this.nbRounds];
	for (int i = 0; i < nbRounds; i++) {
	    challenge[i] = rng.nextBoolean();
	}
	return new FFSRound2DTO(proverId, validatorId, challenge);
    }

    /**
     * Authenticate the prover according to the response supplied and the
     * previously generated commitment and challenge.
     * 
     * @param response
     * @return
     */
    public boolean authenticate(FFSRound3DTO response) {
	if (this.w == null || this.w.length != nbRounds) {
	    logger.info("Invalid commitment. Cannot proceed with authentication");
	    return false;
	}
	try {
	    this.r = response.getResponse();
	} catch (ParseException e) {
	    logger.error("Unable to process response. ", e);
	    this.r = null;
	    return false;
	}
	if (this.r == null || this.r.length != nbRounds) {
	    logger.info("Invalid response. Cannot proceed with authentication");
	    return false;
	}
	for (int i = 0; i < nbRounds; i++) {
	    BigInteger r_2 = r[i].modPow(TWO, n);
	    if (challenge[i]) {
		if (!r_2.equals(w[i].multiply(publicKey).mod(n))) {
		    return false;
		}
	    } else {
		if (!r_2.equals(w[i])) {
		    return false;
		}
	    }
	}
	return true;
    }
}

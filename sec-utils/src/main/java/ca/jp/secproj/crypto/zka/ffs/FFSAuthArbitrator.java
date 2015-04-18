package ca.jp.secproj.crypto.zka.ffs;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jp.secproj.crypto.zka.ffs.dto.FFSSetupDTO;

/**
 * Setup manager for FFS: this class acts as the trusted third party
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class FFSAuthArbitrator {

    private static Logger logger = LoggerFactory.getLogger(FFSAuthArbitrator.class);

    private static final BigInteger TWO = new BigInteger("2");

    private SecureRandom random;

    private static final int NB_BITS = 1024;

    private static final int MIN_NB_ROUNDS = 64;

    private static final int MAX_NB_ROUNDS = 256;

    /**
     * Identifier of the prover
     */
    private String proverId;

    /**
     * Identifier of the validator
     */
    private String validatorId;

    /**
     * User's secret password as a big integer
     */
    private BigInteger secret;

    /**
     * Number of rounds of FFS to execute (essentially the lenght of all
     * vectors: commitment, witness, challenge and response). There is a chance
     * = 1 / (2^nbRounds) that the prover can fool the validator without the
     * valid secret.
     */
    private int nbRounds;

    /**
     * Large number such that n= p*q, p and q being large prime numbers
     */
    private BigInteger n;

    /**
     * Prover's public key
     */
    private BigInteger publicKey;

    /**
     * Setup data to be sent to a trusted third party for registration
     */
    private FFSSetupDTO setup;

    /**
     * Initialize the setup generator with a byte array representing a secret
     * (private key) for the prover.
     * 
     * @param passphrase
     *            A byte array that is the private key of the prover. It is
     *            required that the secret is smaller than 2048 bits hence the
     *            byte array lenght must be smaller than 256. The minimum secret
     *            lenght is 128 bit, hence a byte array of lenght 16.
     * 
     * @param nbRounds
     *            A numer of rounds required to consider authentication a
     *            success between 64 and 256
     */
    public FFSAuthArbitrator(String proverId, String validatorId, byte[] passphrase, int nbRounds) {
	if (StringUtils.isBlank(proverId) || StringUtils.isBlank(validatorId)) {
	    throw new IllegalArgumentException("Invalid prover id or validator id");
	}
	if (passphrase == null || passphrase.length < 16 || passphrase.length > 256) {
	    throw new IllegalArgumentException(
		    "Invalid passphrase: null or inapropriate lenght (between 16 and 256; 128 and 20148 bits)");
	}

	if (nbRounds > MIN_NB_ROUNDS && nbRounds < MAX_NB_ROUNDS) {
	    this.nbRounds = nbRounds;
	} else if (nbRounds < MIN_NB_ROUNDS) {
	    this.nbRounds = MIN_NB_ROUNDS;
	    logger.warn("Invalid number of rounds (" + nbRounds + ") supplied. Using min value " + MIN_NB_ROUNDS);
	} else {
	    this.nbRounds = MAX_NB_ROUNDS;
	    logger.warn("Invalid number of rounds (" + nbRounds + ") supplied. Using max value " + MAX_NB_ROUNDS);
	}
	this.proverId = proverId;
	this.validatorId = validatorId;
	this.secret = new BigInteger(passphrase);
	this.random = new SecureRandom();
    }

    /**
     * Generates the required parameters for Feige Fiat Shamir protocol
     * 
     * @return A FFSSetup object containing all required parameters to initiate
     *         the first authentication
     */
    public FFSSetupDTO GenerateFFSSetup() {

	// Generate n if it was not previously done
	if (setup == null) {
	    BigInteger p = null;
	    BigInteger q = null;
	    int iterNbr = 0;
	    boolean nMeetsRequirements = false;
	    // Repeat the generation of n while the user's password and
	    while (!nMeetsRequirements && ++iterNbr <= 100) {
		p = BigInteger.probablePrime(NB_BITS, random);
		q = BigInteger.probablePrime(NB_BITS, random);

		n = p.multiply(q);

		// Ensure n and secret are coprimes and that n > secret
		if (n.gcd(secret).equals(BigInteger.ONE) && n.compareTo(secret) == 1) {
		    nMeetsRequirements = true;
		} else {
		    n = null;
		}
	    }
	    if (n == null) {
		logger.warn("Unable to find n coprime with supplied passphrase");
		return null;
	    }
	    publicKey = secret.modPow(TWO, n);

	    this.setup = new FFSSetupDTO(proverId, validatorId, this.n.toString(), this.nbRounds,
		    this.publicKey.toString());
	}
	return this.setup;
    }

}

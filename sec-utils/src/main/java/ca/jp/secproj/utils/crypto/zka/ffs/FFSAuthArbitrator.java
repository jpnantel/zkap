package ca.jp.secproj.utils.crypto.zka.ffs;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private BigInteger secret;

    private int nbRounds;

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
    public FFSAuthArbitrator(byte[] passphrase, int nbRounds) {
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
	this.secret = new BigInteger(passphrase);
	this.random = new SecureRandom();
    }

    public FFSSetup GenerateFFSSetup() {
	BigInteger n = null;

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

	BigInteger t = secret.modPow(TWO, n);

	return new FFSSetup(n, nbRounds, t);
    }

}

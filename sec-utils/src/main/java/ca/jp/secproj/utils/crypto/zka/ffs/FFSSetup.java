package ca.jp.secproj.utils.crypto.zka.ffs;

import java.math.BigInteger;

/**
 * Container for all the data required to initiate FFS
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class FFSSetup {

    /**
     * Large number such that n= p*q, p and q being large prime numbers
     */
    private BigInteger n;

    /**
     * Number of sucessful challenges required for an authentication to be a
     * success.
     */
    private int nbRounds;

    /**
     * Prover's public key
     */
    private BigInteger t;

    public FFSSetup(BigInteger n, int nbRounds, BigInteger t) {
	super();
	this.n = n;
	this.nbRounds = nbRounds;
	this.t = t;
    }

    public BigInteger getN() {
	return n;
    }

    public int getNbRounds() {
	return nbRounds;
    }

    public BigInteger getT() {
	return t;
    }

}

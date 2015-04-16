package ca.jp.secproj.crypto.zka.ffs;

import java.math.BigInteger;

/**
 * Container for all the data required to initiate FFS
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class FFSSetup {

    /**
     * Identifier of the prover
     */
    private String proverId;

    /**
     * Identifier of the validator
     */
    private String validatorId;

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

    public FFSSetup(String proverId, String validatorId, BigInteger n, int nbRounds, BigInteger t) {
	super();
	this.proverId = proverId;
	this.validatorId = validatorId;
	this.n = n;
	this.nbRounds = nbRounds;
	this.t = t;
    }

    public String getProverId() {
	return proverId;
    }

    public String getValidatorId() {
	return validatorId;
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

package ca.jp.secproj.crypto.zka.ffs;

import java.math.BigInteger;

/**
 * Data transfer object for the container for all the data required to initiate
 * FFS
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class FFSSetupDTO {

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
    private String n;

    /**
     * Number of sucessful challenges required for an authentication to be a
     * success.
     */
    private int nbRounds;

    /**
     * Prover's public key
     */
    private String t;

    /**
     * Default constructor to be used by Jackson json serializer
     */
    public FFSSetupDTO() {

    }

    public FFSSetupDTO(FFSSetup setup) {
	this.proverId = setup.getProverId();
	this.validatorId = setup.getValidatorId();
	this.n = setup.getN().toString();
	this.nbRounds = setup.getNbRounds();
	this.t = setup.getT().toString();
    }

    public FFSSetup generateFFSSetup() {
	return new FFSSetup(this.proverId, this.validatorId, new BigInteger(this.n), this.nbRounds, new BigInteger(
		this.t));
    }

    public String getProverId() {
	return proverId;
    }

    public void setProverId(String proverId) {
	this.proverId = proverId;
    }

    public String getValidatorId() {
	return validatorId;
    }

    public void setValidatorId(String validatorId) {
	this.validatorId = validatorId;
    }

    public String getN() {
	return n;
    }

    public void setN(String n) {
	this.n = n;
    }

    public int getNbRounds() {
	return nbRounds;
    }

    public void setNbRounds(int nbRounds) {
	this.nbRounds = nbRounds;
    }

    public String getT() {
	return t;
    }

    public void setT(String t) {
	this.t = t;
    }

}

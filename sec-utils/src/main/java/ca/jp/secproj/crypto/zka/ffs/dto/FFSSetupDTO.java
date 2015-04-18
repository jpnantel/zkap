package ca.jp.secproj.crypto.zka.ffs.dto;

/**
 * Data transfer object for the container for all the data required to initiate
 * FFS
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class FFSSetupDTO extends FFSDTOBase {

    /**
     * Large number such that n= p*q, p and q being large prime numbers
     */
    private String n;

    /**
     * Number of sucessful challenges required for an authentication to be valid
     * success.
     */
    private int nbRounds;

    /**
     * Prover's public key
     */
    private String publicKey;

    /**
     * Default constructor to be used by Jackson json serializer
     */
    public FFSSetupDTO() {

    }

    public FFSSetupDTO(String proverId, String validatorId, String n, int nbRounds, String publicKey) {
	super(proverId, validatorId);
	this.n = n;
	this.nbRounds = nbRounds;
	this.publicKey = publicKey;
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

    public String getPublicKey() {
	return publicKey;
    }

    public void setPublicKey(String publicKey) {
	this.publicKey = publicKey;
    }

}

package ca.jp.secproj.crypto.zka.ffs.dto;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class FFSDTOBase {

    /**
     * Identifier of the prover
     */
    protected String proverId;

    /**
     * Identifier of the validator
     */
    protected String validatorId;

    public FFSDTOBase() {
	super();
    }

    public FFSDTOBase(String proverId, String validatorId) {
	super();
	this.proverId = proverId;
	this.validatorId = validatorId;
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

}

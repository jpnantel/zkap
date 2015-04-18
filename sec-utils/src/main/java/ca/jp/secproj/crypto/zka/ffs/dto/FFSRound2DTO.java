package ca.jp.secproj.crypto.zka.ffs.dto;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class FFSRound2DTO extends FFSDTOBase {

    private boolean[] challenge;

    public FFSRound2DTO() {
	super();
    }

    public FFSRound2DTO(String proverId, String validatorId, boolean[] challenge) {
	super(proverId, validatorId);
	this.challenge = challenge;
    }

    public boolean[] getChallenge() {
	return challenge;
    }

    public void setChallenge(boolean[] challenge) {
	this.challenge = challenge;
    }

}

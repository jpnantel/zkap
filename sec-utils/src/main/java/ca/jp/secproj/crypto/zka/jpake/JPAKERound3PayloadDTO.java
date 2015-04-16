package ca.jp.secproj.crypto.zka.jpake;

import java.math.BigInteger;

import org.bouncycastle.crypto.agreement.jpake.JPAKERound3Payload;

public class JPAKERound3PayloadDTO {

    private String participantId;

    private String macTag;

    public JPAKERound3PayloadDTO() {
    }

    public JPAKERound3PayloadDTO(JPAKERound3Payload payload) {

	this.participantId = payload.getParticipantId();
	this.macTag = payload.getMacTag().toString();
    }

    public JPAKERound3Payload createPayload() {
	return new JPAKERound3Payload(participantId, new BigInteger(macTag));
    }

    public String getParticipantId() {
	return participantId;
    }

    public void setParticipantId(String participantId) {
	this.participantId = participantId;
    }

    public String getMacTag() {
	return macTag;
    }

    public void setMacTag(String macTag) {
	this.macTag = macTag;
    }
}

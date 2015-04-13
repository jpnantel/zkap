package ca.jp.secproj.utils.crypto.zka;

import java.math.BigInteger;

import org.bouncycastle.crypto.agreement.jpake.JPAKERound2Payload;

import ca.jp.secproj.utils.crypto.BigIntegerUtils;

public class JPAKERound2PayloadDTO {

    private String participantId;

    private String a;

    private String[] kpsx2;

    public JPAKERound2PayloadDTO() {
    }

    public JPAKERound2PayloadDTO(JPAKERound2Payload payload) {
	this.participantId = payload.getParticipantId();
	this.a = payload.getA().toString();
	this.kpsx2 = BigIntegerUtils.BigIntArrayToString(payload.getKnowledgeProofForX2s());
    }

    public String getParticipantId() {
	return participantId;
    }

    public void setParticipantId(String participantId) {
	this.participantId = participantId;
    }

    public String getA() {
	return a;
    }

    public void setA(String a) {
	this.a = a;
    }

    public String[] getKpsx2() {
	return kpsx2;
    }

    public void setKpsx2(String[] kpsx2) {
	this.kpsx2 = kpsx2;
    }

    public JPAKERound2Payload createPayload() {
	return new JPAKERound2Payload(participantId, new BigInteger(a), BigIntegerUtils.StringArrayToBigInt(kpsx2));
    }

}

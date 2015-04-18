package ca.jp.secproj.crypto.zka.jpake;

import java.math.BigInteger;
import java.text.ParseException;

import org.bouncycastle.crypto.agreement.jpake.JPAKERound2Payload;
import org.codehaus.jackson.annotate.JsonIgnore;

import ca.jp.secproj.utils.math.BigIntegerUtils;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
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

    @JsonIgnore
    public JPAKERound2Payload createPayload() throws ParseException {
	return new JPAKERound2Payload(participantId, new BigInteger(a), BigIntegerUtils.StringArrayToBigInt(kpsx2));
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

}

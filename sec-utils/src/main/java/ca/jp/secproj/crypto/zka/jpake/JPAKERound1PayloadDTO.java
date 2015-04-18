package ca.jp.secproj.crypto.zka.jpake;

import java.math.BigInteger;
import java.text.ParseException;

import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.codehaus.jackson.annotate.JsonIgnore;

import ca.jp.secproj.utils.math.BigIntegerUtils;

/**
 * 
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class JPAKERound1PayloadDTO {

    private String participantId;

    private String gx1;

    private String gx2;

    private String[] knowledgeProofForX1;

    private String[] knowledgeProofForX2;

    public JPAKERound1PayloadDTO() {
    }

    public JPAKERound1PayloadDTO(JPAKERound1Payload payload) {
	this.participantId = payload.getParticipantId();
	this.gx1 = payload.getGx1().toString();
	this.gx2 = payload.getGx2().toString();
	this.knowledgeProofForX1 = BigIntegerUtils.BigIntArrayToString(payload.getKnowledgeProofForX1());
	this.knowledgeProofForX2 = BigIntegerUtils.BigIntArrayToString(payload.getKnowledgeProofForX2());
    }

    @JsonIgnore
    public JPAKERound1Payload createPayload() throws ParseException {
	return new JPAKERound1Payload(participantId, new BigInteger(gx1), new BigInteger(gx2),
		BigIntegerUtils.StringArrayToBigInt(this.knowledgeProofForX1),
		BigIntegerUtils.StringArrayToBigInt(this.knowledgeProofForX2));
    }

    public String getParticipantId() {
	return participantId;
    }

    public void setParticipantId(String participantId) {
	this.participantId = participantId;
    }

    public String getGx1() {
	return gx1;
    }

    public void setGx1(String gx1) {
	this.gx1 = gx1;
    }

    public String getGx2() {
	return gx2;
    }

    public void setGx2(String gx2) {
	this.gx2 = gx2;
    }

    public String[] getKnowledgeProofForX1() {
	return knowledgeProofForX1;
    }

    public void setKnowledgeProofForX1(String[] knowledgeProofForX1) {
	this.knowledgeProofForX1 = knowledgeProofForX1;
    }

    public String[] getKnowledgeProofForX2() {
	return knowledgeProofForX2;
    }

    public void setKnowledgeProofForX2(String[] knowledgeProofForX2) {
	this.knowledgeProofForX2 = knowledgeProofForX2;
    }

}

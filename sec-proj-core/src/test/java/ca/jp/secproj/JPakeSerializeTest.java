package ca.jp.secproj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.jpake.JPAKEParticipant;
import org.bouncycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroup;
import org.bouncycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroups;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jp.secproj.utils.crypto.zka.jpake.JPAKERound1PayloadDTO;

public class JPakeSerializeTest {

    private static Logger logger = LoggerFactory.getLogger(JPakeSerializeTest.class);

    @Test
    public void JPakePayloadSerializeAndDeserializeTest() {

	JPAKEPrimeOrderGroup group = JPAKEPrimeOrderGroups.NIST_3072;
	Digest digest = new SHA256Digest();
	SecureRandom random = new SecureRandom();

	JPAKEParticipant clientParticipant = new JPAKEParticipant("test", "password".toCharArray(), group, digest,
		random);

	JPAKERound1Payload clientRound1Payload = clientParticipant.createRound1PayloadToSend();

	try {
	    ObjectMapper mapper = new ObjectMapper();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    mapper.writeValue(baos, new JPAKERound1PayloadDTO(clientRound1Payload));

	    byte[] content = baos.toByteArray();
	    logger.info(new String(content, "UTF-8"));

	    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	    JPAKERound1PayloadDTO testPLD = mapper.readValue(bais, JPAKERound1PayloadDTO.class);
	    testPLD.getGx1();

	    JPAKERound1Payload testPL = testPLD.createPayload();
	    testPL.getGx1();
	} catch (IOException e1) {
	    logger.error("", e1);
	    Assert.fail("Exception thrown!");
	}
    }

}

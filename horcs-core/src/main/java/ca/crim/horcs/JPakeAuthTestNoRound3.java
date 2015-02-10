package ca.crim.horcs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.jpake.JPAKEParticipant;
import org.bouncycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroup;
import org.bouncycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroups;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound2Payload;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.crim.horcs.utils.log.Loggable;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

@Loggable
@Component("horcsAuthJPakeTest2")
public class JPakeAuthTestNoRound3 implements AuthTest {

    private static Logger logger;

    @Autowired
    private String clientUserName;

    @Autowired
    private String clientPassword;

    @Autowired
    private String jpakeAuthUrl;

    @Autowired
    private Client jerseyClient;

    public BigInteger executeAuth() {

        /*
         * Initialization
         * 
         * Pick an appropriate prime order group to use throughout the exchange.
         * Note that both participants must use the same group.
         */
        JPAKEPrimeOrderGroup group = JPAKEPrimeOrderGroups.NIST_3072;

        BigInteger p = group.getP();
        BigInteger q = group.getQ();
        BigInteger g = group.getG();

        logger.info("********* Initialization **********");
        logger.info("Public parameters for the cyclic group:");
        logger.info("p (" + p.bitLength() + " bits): " + p.toString(16));
        logger.info("q (" + q.bitLength() + " bits): " + q.toString(16));
        logger.info("g (" + p.bitLength() + " bits): " + g.toString(16));
        logger.info("p mod q = " + p.mod(q).toString(16));
        logger.info("g^{q} mod p = " + g.modPow(q, p).toString(16));
        logger.info("(Secret passwords used by Alice " + "\"" + clientPassword + "\")\n");

        /*
         * Both participants must use the same hashing algorithm.
         */
        Digest digest = new SHA256Digest();
        SecureRandom random = new SecureRandom();

        JPAKEParticipant clientParticipant = new JPAKEParticipant(clientUserName, clientPassword.toCharArray(), group,
                digest, random);

        /*
         * Round 1
         * 
         * Alice and Bob each generate a round 1 payload, and send it to each
         * other.
         */

        JPAKERound1Payload clientRound1Payload = clientParticipant.createRound1PayloadToSend();

        logger.info("************ Round 1 **************");
        logger.info("Alice sends to Bob: ");
        logger.info("g^{x1}=" + clientRound1Payload.getGx1().toString(16));
        logger.info("g^{x2}=" + clientRound1Payload.getGx2().toString(16));
        logger.info("KP{x1}={" + clientRound1Payload.getKnowledgeProofForX1()[0].toString(16) + "};{"
                + clientRound1Payload.getKnowledgeProofForX1()[1].toString(16) + "}");
        logger.info("KP{x2}={" + clientRound1Payload.getKnowledgeProofForX2()[0].toString(16) + "};{"
                + clientRound1Payload.getKnowledgeProofForX2()[1].toString(16) + "}");
        logger.info("g^{q} mod p = " + g.modPow(q, p).toString(16));

        JPakeAuthResponse<JPAKERound1Payload> serverRound1PayloadResponse;
        try {
            serverRound1PayloadResponse = postToServer(clientRound1Payload, JPAKERound1Payload.class, jpakeAuthUrl
                    + "/firstround", null);
        } catch (CryptoException e) {
            logger.error("Round 1 server (validation?) error. ", e);
            return null;
        }
        JPAKERound1Payload serverRound1Payload = serverRound1PayloadResponse.getRight();
        String serverUser = serverRound1PayloadResponse.getLeft();

        logger.info("Bob responds to Alice: ");
        logger.info("g^{x3}=" + serverRound1Payload.getGx1().toString(16));
        logger.info("g^{x4}=" + serverRound1Payload.getGx2().toString(16));
        logger.info("KP{x3}={" + serverRound1Payload.getKnowledgeProofForX1()[0].toString(16) + "};{"
                + serverRound1Payload.getKnowledgeProofForX1()[1].toString(16) + "}");
        logger.info("KP{x4}={" + serverRound1Payload.getKnowledgeProofForX2()[0].toString(16) + "};{"
                + serverRound1Payload.getKnowledgeProofForX2()[1].toString(16) + "}");

        /*
         * Each participant must then validate the received payload for round 1
         */
        try {
            clientParticipant.validateRound1PayloadReceived(serverRound1Payload);
        } catch (CryptoException e) {
            logger.error("Round 1 validation error", e);
            return null;
        }

        logger.info("Alice checks g^{x4}!=1: OK");
        logger.info("Alice checks KP{x3}: OK");
        logger.info("Alice checks KP{x4}: OK");
        logger.info("Bob validated content ");

        // bob.validateRound1PayloadReceived(aliceRound1Payload);
        // System.out.println("Bob checks g^{x2}!=1: OK");
        // System.out.println("Bob checks KP{x1},: OK");
        // System.out.println("Bob checks KP{x2},: OK");
        // System.out.println("");

        /*
         * Round 2
         * 
         * Alice and Bob each generate a round 2 payload, and send it to each
         * other.
         */

        JPAKERound2Payload clientRound2Payload = clientParticipant.createRound2PayloadToSend();
        JPAKERound2Payload serverRound2Payload = null;

        try {
            serverRound2Payload = postToServer(clientRound2Payload, JPAKERound2Payload.class,
                    jpakeAuthUrl + "/secondround", serverUser).getRight();
        } catch (CryptoException e) {
            logger.error("Round 2 server (validation?) error. ", e);
            return null;
        }

        logger.info("************ Round 2 **************");
        logger.info("Alice sends to Bob: ");
        logger.info("A=" + clientRound2Payload.getA().toString(16));
        logger.info("KP{x2*s}={" + clientRound2Payload.getKnowledgeProofForX2s()[0].toString(16) + "},{"
                + clientRound2Payload.getKnowledgeProofForX2s()[1].toString(16) + "}");
        logger.info("");

        logger.info("Bob sends to Alice");
        logger.info("B=" + serverRound2Payload.getA().toString(16));
        logger.info("KP{x4*s}={" + serverRound2Payload.getKnowledgeProofForX2s()[0].toString(16) + "},{"
                + serverRound2Payload.getKnowledgeProofForX2s()[1].toString(16) + "}");

        /*
         * Each participant must then validate the received payload for round 2
         */

        try {
            clientParticipant.validateRound2PayloadReceived(serverRound2Payload);
        } catch (CryptoException e) {
            logger.error("Round 2 validation error", e);
        }
        logger.info("Alice checks KP{x4*s}: OK\n");

        /*
         * After round 2, each participant computes the keying material.
         */

        BigInteger clientKeyingMaterial = clientParticipant.calculateKeyingMaterial();

        logger.info("********* After round 2 ***********");
        logger.info("Alice computes key material \t K=" + clientKeyingMaterial.toString(16));

        /*
         * You must derive a session key from the keying material applicable to
         * whatever encryption algorithm you want to use.
         */

        BigInteger clientKey = deriveSessionKey(clientKeyingMaterial);

        /*
         * At this point, you can stop and use the session keys if you want.
         * This is implicit key confirmation.
         * 
         * If you want to explicitly confirm that the key material matches, you
         * can continue on and perform round 3.
         */

        logger.info("Key computation complete! ");

        return clientKey;
    }

    /**
     * Exécution d'un appel HTTP POST à l'URL spécifié et sérialise un objet de
     * classe T pour l'envoyer dans le InputStream de la requête et s'attend à
     * recevoir un autre objet T dans le InputStream de la réponse. L'objet de
     * la réponse sera désérialisé avant d'être retourné. Il faut que l'objet T
     * soit sérialisable.
     * 
     * @param send
     *            Objet à envoyer
     * @param url
     *            Url du service web
     * @param clazz
     *            classe de l'objet T
     * @return l'objet T de réponse du serveur.
     */
    private <T extends Serializable> JPakeAuthResponse<T> postToServer(T send, Class<T> clazz, String url, String user)
            throws CryptoException {

        WebResource wr = jerseyClient.resource(url);
        ClientResponse r = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(send);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

            Builder b = wr.entity(bais, MediaType.APPLICATION_OCTET_STREAM);
            if (StringUtils.isNotBlank(user)) {
                b.header("user", user);
            }
            r = b.post(ClientResponse.class);
            String serverUser = null;
            if (r.getHeaders() != null && r.getHeaders().get("user") != null && r.getHeaders().get("user").size() > 0) {
                serverUser = r.getHeaders().get("user").get(0);
            }
            Status s = r.getClientResponseStatus();
            if (s == Status.OK) {
                InputStream is = r.getEntityInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                T serverPayload = clazz.cast(ois.readObject());
                ois.close();

                return new JPakeAuthResponse<T>(serverUser, serverPayload);
            } else {
                throw new CryptoException("Server error: " + s.getStatusCode());
            }
        } catch (ClassNotFoundException | IOException | ClassCastException e) {
            logger.warn("Error doing post (postToServer)", e);
        }
        return null;
    }

    private static BigInteger deriveSessionKey(BigInteger keyingMaterial) {
        /*
         * You should use a secure key derivation function (KDF) to derive the
         * session key.
         * 
         * For the purposes of this example, I'm just going to use a hash of the
         * keying material.
         */
        SHA256Digest digest = new SHA256Digest();

        byte[] keyByteArray = keyingMaterial.toByteArray();

        byte[] output = new byte[digest.getDigestSize()];

        digest.update(keyByteArray, 0, keyByteArray.length);

        digest.doFinal(output, 0);

        return new BigInteger(output);
    }

    public String getAlicePassword() {
        return clientPassword;
    }

    public void setAlicePassword(String alicePassword) {
        this.clientPassword = alicePassword;
    }

    public String getJpakeAuthUrl() {
        return jpakeAuthUrl;
    }

    public void setJpakeAuthUrl(String jpakeAuthUrl) {
        this.jpakeAuthUrl = jpakeAuthUrl;
    }

    public Client getJerseyClient() {
        return jerseyClient;
    }

    public void setJerseyClient(Client jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

    private class JPakeAuthResponse<T extends Serializable> extends Pair<String, T> {
        /**
         * 
         */
        private static final long serialVersionUID = -2741089721818001338L;

        private String serverUser;

        private T serverPayload;

        public JPakeAuthResponse(String user, T serverPayload) {
            this.serverUser = user;
            this.serverPayload = serverPayload;
        }

        @Override
        public T setValue(T value) {
            T oldVal = this.serverPayload;
            this.serverPayload = value;
            return oldVal;
        }

        @Override
        public String getLeft() {
            return serverUser;
        }

        @Override
        public T getRight() {
            return serverPayload;
        }

    }

}

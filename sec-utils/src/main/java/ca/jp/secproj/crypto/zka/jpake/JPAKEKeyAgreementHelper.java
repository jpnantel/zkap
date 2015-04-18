package ca.jp.secproj.crypto.zka.jpake;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.jpake.JPAKEParticipant;
import org.bouncycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroup;
import org.bouncycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroups;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound2Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound3Payload;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class JPAKEKeyAgreementHelper {

    private static Logger logger = LoggerFactory.getLogger(JPAKEKeyAgreementHelper.class);

    private JPAKEPrimeOrderGroup group;

    private Digest digest;

    private SecureRandom random;

    private JPAKEParticipant participant;

    private BigInteger keyingMaterial;

    private byte[] rawAESKey;

    private String username;

    public JPAKEKeyAgreementHelper(String username, String password) {

	this.username = username;

	/*
	 * Initialization
	 * 
	 * Pick an appropriate prime order group to use throughout the exchange.
	 * Note that both participants must use the same group.
	 */
	this.group = JPAKEPrimeOrderGroups.NIST_3072;

	BigInteger p = group.getP();
	BigInteger q = group.getQ();
	BigInteger g = group.getG();

	logger.debug("********* Initialization **********");
	logger.debug("Public parameters for the cyclic group:");
	logger.debug("p (" + p.bitLength() + " bits): " + p.toString(16));
	logger.debug("q (" + q.bitLength() + " bits): " + q.toString(16));
	logger.debug("g (" + p.bitLength() + " bits): " + g.toString(16));
	logger.debug("p mod q = " + p.mod(q).toString(16));
	logger.debug("g^{q} mod p = " + g.modPow(q, p).toString(16));

	/*
	 * Both participants must use the same hashing algorithm.
	 */
	this.digest = new SHA256Digest();
	this.random = new SecureRandom();

	this.participant = new JPAKEParticipant(username, password.toCharArray(), group, digest, random);
    }

    /**
     * Crée un paquet à envoyer à l'interlocuteur pour la phase 1 (paquet
     * d'Alice à envoyer à Bob)
     * 
     * @return
     */
    public JPAKERound1Payload getRound1Payload() {
	return this.participant.createRound1PayloadToSend();
    }

    /**
     * Valide le paquet envoyé par l'interlocuteur pour la phase 1(paquet de Bob
     * validé par Alice)
     * 
     * 
     * @param bobPayload
     * @return
     */
    public boolean validateRound1(JPAKERound1Payload bobPayload) {

	try {
	    this.participant.validateRound1PayloadReceived(bobPayload);
	} catch (CryptoException | IllegalStateException e) {
	    logger.error("Round 1 validation error", e);
	    return false;
	}
	return true;
    }

    /**
     * Crée un paquet à envoyer à l'interlocuteur pour la phase 2 (paquet
     * d'Alice à envoyer à Bob)
     * 
     * @return
     */
    public JPAKERound2Payload getRound2Payload() {
	return this.participant.createRound2PayloadToSend();
    }

    /**
     * Valide le paquet envoyé par l'interlocuteur pour la phase 2 (paquet de
     * Bob validé par Alice)
     * 
     * 
     * @param bobPayload
     * @return
     */
    public boolean validateRound2(JPAKERound2Payload bobPayload) {

	try {
	    this.participant.validateRound2PayloadReceived(bobPayload);
	} catch (CryptoException | IllegalStateException e) {
	    logger.error("Round 1 validation error", e);
	    return false;
	}
	return true;
    }

    /**
     * Crée un paquet à envoyer à l'interlocuteur pour la phase 3 (paquet
     * d'Alice à envoyer à Bob)
     * 
     * @return
     */
    public JPAKERound3Payload getRound3Payload() {
	return this.participant.createRound3PayloadToSend(getKeyingMaterial());
    }

    /**
     * Valide le paquet envoyé par l'interlocuteur pour la phase 3 (paquet de
     * Bob validé par Alice)
     * 
     * 
     * @param bobPayload
     * @return
     */
    public boolean validateRound3(JPAKERound3Payload bobPayload) {

	try {
	    this.participant.validateRound3PayloadReceived(bobPayload, getKeyingMaterial());
	} catch (CryptoException | IllegalStateException e) {
	    logger.error("Round 3 validation error", e);
	    return false;
	}
	return true;
    }

    /**
     * Computes and returns the keying material
     * 
     * @return
     */
    public BigInteger getKeyingMaterial() {
	if (this.keyingMaterial == null) {
	    this.keyingMaterial = this.participant.calculateKeyingMaterial();
	}
	return this.keyingMaterial;
    }

    /**
     * Derives a raw byte array AES key from the keying material resulting of
     * the exchange.
     * 
     * @return
     */
    public byte[] getRawAES128Key() {
	if (rawAESKey == null) {

	    byte[] key = null;
	    try {
		key = keyingMaterial.toByteArray();
	    } catch (IllegalStateException e) {
		logger.error("Unable to genrate key because of incomplete agreement. ", e);
		return null;
	    }
	    MessageDigest sha = null;
	    try {
		sha = MessageDigest.getInstance("SHA-1");
	    } catch (NoSuchAlgorithmException e) {
		logger.error("Unable to find SHA-1 algorithm. ", e);
		return null;
	    }
	    key = sha.digest(key);
	    rawAESKey = Arrays.copyOf(key, 16);
	}
	return rawAESKey;
    }

    /**
     * Gets the username used for the participant
     * 
     * @return
     */
    public String getUsername() {
	return username;
    }

}

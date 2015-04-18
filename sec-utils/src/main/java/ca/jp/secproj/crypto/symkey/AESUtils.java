package ca.jp.secproj.crypto.symkey;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.util.Base64;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class AESUtils {

    private static Logger logger = LoggerFactory.getLogger(AESUtils.class);

    private Cipher encrypter;

    private Cipher decrypter;

    public AESUtils(byte[] aes128KeyAsBytes) {

	SecretKeySpec key = new SecretKeySpec(aes128KeyAsBytes, "AES");

	try {
	    encrypter = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    encrypter.init(Cipher.ENCRYPT_MODE, key);

	} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
	    logger.error("Encrypter init error! ", e);
	    return;
	}

	try {
	    decrypter = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    decrypter.init(Cipher.DECRYPT_MODE, key);

	} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
	    logger.error("Decrypter init error! ", e);
	    return;
	}
    }

    public String cipherString(String plainTxt) {
	byte[] cipheredMessage = null;
	try {
	    cipheredMessage = encrypter.doFinal(plainTxt.getBytes("UTF-8"));
	} catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
	    logger.error("Encryption error: ", e);
	    return null;
	}
	return new String(Base64.encode(cipheredMessage));

    }

    public String deCipherString(String base64EncodedEncryptedText) {
	String decypheredResponse = null;
	try {
	    decypheredResponse = new String(decrypter.doFinal(Base64.decode(base64EncodedEncryptedText)), "UTF-8");
	} catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
	    logger.error("Decryption error. ", e);
	    return null;
	}
	return decypheredResponse;
    }

}

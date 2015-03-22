package ca.jp.secproj.utils.crypto;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class SymmetricStreamCipher extends AbstractBlockCipher {

    public SymmetricStreamCipher(String cipherString, String chainingMode, String padding, String provider,
            boolean bypassSecurityCheck) throws NoSuchAlgorithmException, NoSuchPaddingException,
            NoSuchProviderException {
        super(cipherString, chainingMode, padding, provider, bypassSecurityCheck);
    }

    public InputStream decrypt(InputStream is, byte[] iv, KeyInfo key) throws InvalidKeyException,
            InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key.getKey(), ivSpec);
        return new CipherInputStream(is, cipher);
    }

    public OutputStream encrypt(OutputStream os) {
        return new CipherOutputStream(os, cipher);
    }

}

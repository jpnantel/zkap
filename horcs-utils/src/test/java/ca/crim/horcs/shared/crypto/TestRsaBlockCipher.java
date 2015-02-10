package ca.crim.horcs.shared.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import ca.crim.horcs.utils.crypto.rsa.RsaBlockCipher;
import ca.crim.horcs.utils.crypto.rsa.RsaKeyInfo.KeyType;
import ca.crim.horcs.utils.crypto.rsa.RsaKeyManager;
import ca.crim.horcs.utils.domain.AppVersion;

public class TestRsaBlockCipher {

    private final static String PRIV_KEY_NAME = "priv";

    private final static String PRIV_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALr0aY2o1VmvKIJH8eMHAh9ddXnvoeOYuAGI6whdKF+oyRkTEivPIsqVZWChfVLBjhwqqu4A8uoWsbPxTSOBN6HAFE9GP0rMqyNLdnPy/rCrjdd1kdES2uDrw8ttnXIUXWe2/7W4w+WAp72RZRSseP37LXsQkiWdOvPNrGvOYbM9AgMBAAECgYAbVNH9mLogE2Bbs4LkSsj/pt0ex26mZFQ6PTcHEP9vBLDpjoUwjGassSyR9OnqaEuLSJ2Wj+LzX//4q4U4dJKaPgr+qcM37Ze6cbcgbRr/2t3qkqTsqS6H2e8TX1Nekx27BlN73GyFJ/OBUu130hGxR5t2egLO6Xr3s2nl8TutzQJBAOBY5pu10r5GAoYCB3CXoOC4+hd1GvZvA2dFjNeogvtvZEHE8R3mpr5wWvFep9OwcL1gJCFcsu0xWb/LA92fSIMCQQDVVPJ9+NFN+FM0ufiYAeA1V3wexgZmgnFDpZFFpQuRRDT+lMf5XHEBaslL+JCAyD+c9RG+rbAiiLOrB2RGwMk/AkAlE6wxCbClj451NTqjifuliAyU+d6V4azvUVhAf/H9ueAY2oJ6zZa7HHh7MYiM/Lj1I0F1XguYFbLedp/R7bQfAkAqipcU0eKDRuroz9EoFu+sE3cX9+tc09RcZg7zMXd+D3j175t1OZ77Zl0f/kBqvgQZe7F/Mcm3CUWy7HVtupInAkBOiSald1PE+VZJ6GwGFdR1Vzrt/SvYM4EW7Jvo8D5FUyLg7NzbGe62IjVGxjf5+5T9OXnU6A3zRkN0z/pL2viQ";

    private static final String PUB_KEY_NAME = "agentPub";

    private static final String PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC69GmNqNVZryiCR/HjBwIfXXV576HjmLgBiOsIXShfqMkZExIrzyLKlWVgoX1SwY4cKqruAPLqFrGz8U0jgTehwBRPRj9KzKsjS3Zz8v6wq43XdZHREtrg68PLbZ1yFF1ntv+1uMPlgKe9kWUUrHj9+y17EJIlnTrzzaxrzmGzPQIDAQAB";

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException,
            NoSuchProviderException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException, InvalidKeySpecException {

        AppVersion av = null;
        try {
            av = new AppVersion("1.3.6d");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Array - Array

        RsaKeyManager keyManager = new RsaKeyManager();

        keyManager.addKey(PRIV_KEY_NAME, Base64.decodeBase64(PRIV_KEY.getBytes("UTF-8")), KeyType.PRIVATE, av);
        keyManager.addKey(PUB_KEY_NAME, Base64.decodeBase64(PUB_KEY.getBytes("UTF-8")), KeyType.PUBLIC, av);

        File orig = new File("d:/Avant.hor");
        FileInputStream inOrig = new FileInputStream(orig);

        File crypt = new File("d:/Crypt_aa.hor");
        FileOutputStream outCrypt = new FileOutputStream(crypt);
        RsaBlockCipher rbc = new RsaBlockCipher();

        byte[] out = rbc.encrypt(IOUtils.toByteArray(inOrig), keyManager.getKey(PUB_KEY_NAME, av));
        outCrypt.write(out);
        inOrig.close();
        outCrypt.close();

        FileInputStream inCrypt = new FileInputStream(crypt);
        File uncrypt = new File("d:/Uncrypt_aa.hor");
        FileOutputStream outUncrypt = new FileOutputStream(uncrypt);
        // RsaUtils.blockCipherDecrypt(inCrypt, outUncrypt, rsa, 1024, 10);
        rbc = new RsaBlockCipher();
        out = rbc.decrypt(IOUtils.toByteArray(inCrypt), keyManager.getKey(PRIV_KEY_NAME, av));
        outUncrypt.write(out);
        inCrypt.close();
        outUncrypt.close();

        // Array - Stream
        keyManager = new RsaKeyManager();

        keyManager.addKey(PRIV_KEY_NAME, Base64.decodeBase64(PRIV_KEY.getBytes("UTF-8")), KeyType.PRIVATE, av);
        keyManager.addKey(PUB_KEY_NAME, Base64.decodeBase64(PUB_KEY.getBytes("UTF-8")), KeyType.PUBLIC, av);

        orig = new File("d:/Avant.hor");
        inOrig = new FileInputStream(orig);

        crypt = new File("d:/Crypt_as.hor");
        outCrypt = new FileOutputStream(crypt);
        rbc = new RsaBlockCipher();

        out = rbc.encrypt(IOUtils.toByteArray(inOrig), keyManager.getKey(PUB_KEY_NAME, av));
        outCrypt.write(out);
        inOrig.close();
        outCrypt.close();

        inCrypt = new FileInputStream(crypt);
        uncrypt = new File("d:/Uncrypt_as.hor");
        outUncrypt = new FileOutputStream(uncrypt);
        // RsaUtils.blockCipherDecrypt(inCrypt, outUncrypt, rsa, 1024, 10);
        rbc = new RsaBlockCipher();
        rbc.decrypt(inCrypt, outUncrypt, keyManager.getKey(PRIV_KEY_NAME, av));
        // out = rbc.decrypt(IOUtils.toByteArray(inCrypt),
        // keyManager.getKey(PRIV_KEY_NAME));
        // outUncrypt.write(out);
        inCrypt.close();
        outUncrypt.close();

        // Stream - Array

        keyManager = new RsaKeyManager();

        keyManager.addKey(PRIV_KEY_NAME, Base64.decodeBase64(PRIV_KEY.getBytes("UTF-8")), KeyType.PRIVATE, av);
        keyManager.addKey(PUB_KEY_NAME, Base64.decodeBase64(PUB_KEY.getBytes("UTF-8")), KeyType.PUBLIC, av);

        orig = new File("d:/Avant.hor");
        inOrig = new FileInputStream(orig);

        crypt = new File("d:/Crypt_sa.hor");
        outCrypt = new FileOutputStream(crypt);
        rbc = new RsaBlockCipher();

        rbc.encrypt(inOrig, outCrypt, keyManager.getKey(PUB_KEY_NAME, av));
        // out = rbc.encrypt(IOUtils.toByteArray(inOrig),
        // keyManager.getKey(PUB_KEY_NAME));
        // outCrypt.write(out);
        inOrig.close();
        outCrypt.close();

        inCrypt = new FileInputStream(crypt);
        uncrypt = new File("d:/Uncrypt_sa.hor");
        outUncrypt = new FileOutputStream(uncrypt);
        // RsaUtils.blockCipherDecrypt(inCrypt, outUncrypt, rsa, 1024, 10);
        rbc = new RsaBlockCipher();
        // rbc.decrypt(inCrypt, outUncrypt, keyManager.getKey(PRIV_KEY_NAME));
        out = rbc.decrypt(IOUtils.toByteArray(inCrypt), keyManager.getKey(PRIV_KEY_NAME, av));
        outUncrypt.write(out);
        inCrypt.close();
        outUncrypt.close();

        // Stream - Stream

        keyManager = new RsaKeyManager();

        keyManager.addKey(PRIV_KEY_NAME, Base64.decodeBase64(PRIV_KEY.getBytes("UTF-8")), KeyType.PRIVATE, av);
        keyManager.addKey(PUB_KEY_NAME, Base64.decodeBase64(PUB_KEY.getBytes("UTF-8")), KeyType.PUBLIC, av);

        orig = new File("d:/Avant.hor");
        inOrig = new FileInputStream(orig);

        crypt = new File("d:/Crypt_ss.hor");
        outCrypt = new FileOutputStream(crypt);
        rbc = new RsaBlockCipher();

        rbc.encrypt(inOrig, outCrypt, keyManager.getKey(PUB_KEY_NAME, av));
        // out = rbc.encrypt(IOUtils.toByteArray(inOrig),
        // keyManager.getKey(PUB_KEY_NAME));
        // outCrypt.write(out);
        inOrig.close();
        outCrypt.close();

        inCrypt = new FileInputStream(crypt);
        uncrypt = new File("d:/Uncrypt_ss.hor");
        outUncrypt = new FileOutputStream(uncrypt);
        // RsaUtils.blockCipherDecrypt(inCrypt, outUncrypt, rsa, 1024, 10);
        rbc = new RsaBlockCipher();
        rbc.decrypt(inCrypt, outUncrypt, keyManager.getKey(PRIV_KEY_NAME, av));
        // out = rbc.decrypt(IOUtils.toByteArray(inCrypt),
        // keyManager.getKey(PRIV_KEY_NAME));
        // outUncrypt.write(out);
        inCrypt.close();
        outUncrypt.close();
    }

}

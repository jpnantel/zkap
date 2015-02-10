package ca.crim.horcs.shared.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import ca.crim.horcs.utils.crypto.KeyInfo;
import ca.crim.horcs.utils.crypto.SymmetricStreamCipher;

public class TestStreamCipher {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException,
            InvalidParameterSpecException, NoSuchPaddingException, IOException, NoSuchProviderException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        SymmetricStreamCipher scm = new SymmetricStreamCipher("AES", "CBC", "PKCS5Padding", "SunJCE", true);

        SecretKey sk = scm.generateNewKey(128, null);

        scm.initForEncrypt(new KeyInfo(sk, 128));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = scm.encrypt(baos);
        os.write(1);
        os.write(1);

        os.flush();
        os.close();

        baos.toByteArray();
    }
}

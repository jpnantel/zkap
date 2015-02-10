package ca.crim.horcs.shared.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import javax.crypto.SecretKey;

import org.apache.commons.io.IOUtils;

import ca.crim.horcs.utils.crypto.KeyInfo;
import ca.crim.horcs.utils.crypto.SignatureManager;
import ca.crim.horcs.utils.crypto.SymmetricBlockCipher;
import ca.crim.horcs.utils.crypto.rsa.KeyPairFactory;

public class TestChiffrement {

    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();

            // Get the data
            File f = new File("D:\\Temp\\weka-batch\\weka-batch-dist\\weka-dev-assembly-3.7.7.1-SNAPSHOT.jar");
            FileInputStream fis = new FileInputStream(f);
            byte[] testData = IOUtils.toByteArray(fis);

            SecureRandom random = new SecureRandom();
            // Sign the data with SHA512withRSA in the SunRsaSign Provider
            SignatureManager sm = new SignatureManager("SHA512withRSA", "SunRsaSign", random, false);
            long startKey = System.currentTimeMillis();
            // Generate the keys
            KeyPairFactory kf = new KeyPairFactory("RSA", "SunRsaSign", 2048, random);
            System.out.println("Génération des clés: " + (System.currentTimeMillis() - startKey) / 1000d);
            // Sign the data (hash with SHA then crypt with RSA)
            byte[] sig = sm.computeSignature(testData, kf.getPrivateKey());
            // Verify the signature (Hash data, decrypt signature then compare
            // hashes.
            boolean sigOk = sm.verifySignature(testData, sig, kf.getPublicKey());

            System.out.println("Signature OK?: " + (sigOk ? "true" : "false"));

            // Declare a new block cipher manager
            SymmetricBlockCipher aes = new SymmetricBlockCipher("AES", "CBC", "PKCS5PADDING", "SunJCE", false);
            // Get the symmetric key
            SecretKey sk = aes.generateNewKey(256, random);

            // Init the manager and get the IV
            byte[] iv = aes.initForEncrypt(new KeyInfo(sk, 256));
            // Crypt the data and write it to file
            byte[] crypt = aes.encrypt(testData);

            FileOutputStream fos = new FileOutputStream(new File(
                    "D:\\Temp\\weka-batch\\weka-batch-dist\\weka-dev-assembly-3.7.7.1-CRYPT.jar"));
            IOUtils.write(crypt, fos);

            // Decrypt the data and write it to file
            byte[] origData = aes.decrypt(crypt, iv, sk.getEncoded());
            fos = new FileOutputStream(new File(
                    "D:\\Temp\\weka-batch\\weka-batch-dist\\weka-dev-assembly-3.7.7.1-UNCRYPT.jar"));

            IOUtils.write(origData, fos);
            System.out.println((System.currentTimeMillis() - start) / 1000d
                    + " secondes pour signer, vérifier, crypter et décrypter un jar de 7Mb");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

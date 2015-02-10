package ca.crim.horcs.shared.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.io.IOUtils;

import ca.crim.horcs.utils.crypto.RsaKeyFileManager;
import ca.crim.horcs.utils.crypto.rsa.RsaBlockCipher;
import ca.crim.horcs.utils.domain.AppVersion;

public class MainUncipherHorFile {

    /**
     * @param args
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException,
            NoSuchPaddingException, NoSuchProviderException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {

        String fileName = "C:/Users/nanteljp/Downloads/D225W71CIYCP6EEYFV0ASF7FUHU2U74W.hor";

        AppVersion versionBidon = new AppVersion("1", "5", "0a");

        RsaKeyFileManager rkfm = new RsaKeyFileManager("Picture_Of_a_cute_cat");
        RsaBlockCipher rbc = new RsaBlockCipher();
        File f1 = new File(fileName);
        FileInputStream fis = new FileInputStream(f1);
        byte[] data = IOUtils.toByteArray(fis);
        fis.close();

        byte[] uncDat = rbc.decrypt(data, rkfm.getKey(RsaKeyFileManager.CLIENT_DECIPHER_KEY, versionBidon));

        File f2 = new File(fileName + "2.hor");
        FileOutputStream fos = new FileOutputStream(f2);
        IOUtils.write(uncDat, fos);
        fos.close();

    }

}

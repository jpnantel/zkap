package ca.crim.horcs.shared.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import org.apache.commons.codec.binary.Base64;

import ca.crim.horcs.utils.crypto.SignatureManager;
import ca.crim.horcs.utils.crypto.rsa.RsaKeyInfo.KeyType;
import ca.crim.horcs.utils.crypto.rsa.RsaKeyManager;
import ca.crim.horcs.utils.domain.AppVersion;

public class TestSignature {

    private final static String SERVER_SIGN_KEY_NAME = "sign";

    private final static String SERVER_VERIFY_KEY_NAME = "verify";

    private final static String SERVER_SIGN_KEY_BYTES = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOeuCfqoJFF3NIL3y4dV06t8+pIV//h07xGAvaYzK4nHco5iSwoUjIh/xXlxWsr2tngOtZn6YvZ+5zK9+gnBYWUZCak7kcGkRSAogPmmF1gcdBcBMzcLXlvyvH67ENf9kgaOQpbP1TtBt8xh0GpXNsIARS/tGshkqBCHXRrIlCkPAgMBAAECgYBodlLNkG7/xI4K14wPs6VDV5Drubu23ZLGA5R/keb5stJ2XN+U5fsPuPzjazcS+PcBXzCHAx0II+Q/PuW07FrrDSMI/ymPud/eexuzLBbsnJKTLme1m4Fl6DvdJ3q83TuP4es378ZH30fU5glrkJkxk2vx2x/ZvWTd2M9h6bnKcQJBAPYyxxVwzzVhqQQupR+VyTXkFJP7C5NBB8b+387mC69HEWCHtQj4aZ9GFZGOo1wmlm2gIToBNfBd1aRkPIm+3sUCQQDw50pd1CpQ86U0apouFnlVWwtXYVZttb0EYHGehxxsk3FCN8F4s1eXC77n+WhcNOao5W6O6EDRaj0f4bat5iXDAkEA4Qt1U7H0lnUYzAvfQEnozOwZq1L1N7tf4pSj/DnoQGYUCkrCPtrjoYWsvYrG5LsZEkgnVyA4L+l2GnjNpZPliQJBAIslLSExERgdAwlZoaCJPp/8rHpWMLPBy2Ghyz7Dz7kvGLYkuk1bWXZXRHd86biIncpkaw2EPRw4fwHRP9EGJD0CQGcV88JCZG5aD/LOnlgc3FX0jClfCb4sh4HsMSm65YiGWfrHJAdYWyP0MZj+XFcGzKfeaNpngjJc0DOWOwzAMh8=";

    private final static String SERVER_VERIFY_KEY_BYTES = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDnrgn6qCRRdzSC98uHVdOrfPqSFf/4dO8RgL2mMyuJx3KOYksKFIyIf8V5cVrK9rZ4DrWZ+mL2fucyvfoJwWFlGQmpO5HBpEUgKID5phdYHHQXATM3C15b8rx+uxDX/ZIGjkKWz9U7QbfMYdBqVzbCAEUv7RrIZKgQh10ayJQpDwIDAQAB";

    /**
     * @param args
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     */
    public static void main(String[] args) throws InvalidKeyException, UnsupportedEncodingException,
            SignatureException, NoSuchAlgorithmException, NoSuchProviderException {
        RsaKeyManager keys = new RsaKeyManager();

        AppVersion av = null;
        try {
            av = new AppVersion("1.3.6d");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            keys.addKey(SERVER_SIGN_KEY_NAME, Base64.decodeBase64(SERVER_SIGN_KEY_BYTES.getBytes("UTF-8")),
                    KeyType.PRIVATE, av);
            keys.addKey(SERVER_VERIFY_KEY_NAME, Base64.decodeBase64(SERVER_VERIFY_KEY_BYTES.getBytes("UTF-8")),
                    KeyType.PUBLIC, av);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        SignatureManager signatureManager = new SignatureManager("SHA1withRSA", "SunRsaSign", null, true);

        long startSign = System.currentTimeMillis();
        String test = "000WREXGV7KEZ4LHODY681TMDOEVOUXF";
        String signature = new String(Base64.encodeBase64(signatureManager.computeSignature(test.getBytes("UTF-8"),
                PrivateKey.class.cast(keys.getKey(SERVER_SIGN_KEY_NAME, av).getKey()))), "UTF-8");
        long endSign = System.currentTimeMillis();
        boolean valid = signatureManager.verifySignature(test.getBytes("UTF-8"),
                Base64.decodeBase64(signature.getBytes("UTF-8")),
                PublicKey.class.cast(keys.getKey(SERVER_VERIFY_KEY_NAME, av).getKey()));
        long endTime = System.currentTimeMillis();

        System.out.println("Is valid: " + valid);
        System.out.println("Sign time: " + (endSign - startSign));
        System.out.println("Verify time: " + (endTime - endSign));

    }
}

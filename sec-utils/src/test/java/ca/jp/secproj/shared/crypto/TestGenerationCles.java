package ca.jp.secproj.shared.crypto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.NoSuchPaddingException;

import ca.jp.secproj.utils.crypto.rsa.KeyPairFactory;

public class TestGenerationCles {

	/**
	 * @param args
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws NullPointerException
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException,
			NoSuchProviderException, NullPointerException, IOException,
			InvalidKeySpecException, NoSuchPaddingException {

		Encoder enc = Base64.getEncoder();
		// AbstractBlockCipher aes = new SymmetricBlockCipher(null, null, null,
		// null, false);

		// FileOutputStream fos = new FileOutputStream();

		FileWriter fw = new FileWriter(new File("d:/keys.txt"));

		KeyPairFactory kpf = new KeyPairFactory("RSA", "SunRsaSign", 1024,
				new SecureRandom());
		X509EncodedKeySpec pubKey = new X509EncodedKeySpec(kpf.getPublicKey()
				.getEncoded());
		PKCS8EncodedKeySpec privKey = new PKCS8EncodedKeySpec(kpf
				.getPrivateKey().getEncoded());
		fw.write(new String(enc.encode(pubKey.getEncoded()), "UTF-16LE"));
		fw.write(new String(enc.encode(privKey.getEncoded()),
				"UTF-16LE"));
		System.out
				.println("aPub: "
						+ new String(enc.encode(pubKey.getEncoded()),
								"UTF-8"));
		System.out
				.println("aPriv: "
						+ new String(enc.encode(privKey.getEncoded()),
								"UTF-8"));

		kpf = new KeyPairFactory("RSA", "SunRsaSign", 1024, new SecureRandom());
		pubKey = new X509EncodedKeySpec(kpf.getPublicKey().getEncoded());
		privKey = new PKCS8EncodedKeySpec(kpf.getPrivateKey().getEncoded());
		fw.write(new String(enc.encode(pubKey.getEncoded()),
				"UTF-16LE"));
		fw.write(new String(enc.encode(privKey.getEncoded()),
				"UTF-16LE"));
		System.out
				.println("cPub: "
						+ new String(enc.encode(pubKey.getEncoded()),
								"UTF-8"));
		System.out
				.println("cPriv: "
						+ new String(enc.encode(privKey.getEncoded()),
								"UTF-8"));

		fw.close();
		// System.out.println(kf.generatePrivate(ks2));
		//
		// File temp = new File("./clientPub.key");
		// FileOutputStream fos = new FileOutputStream(temp);
		// // fos.write(kpf.getPublicKeyBytes());
		// fos.flush();
		// fos.close();
		//
		// temp = new File("./clientPriv.key");
		// fos = new FileOutputStream(temp);
		// // fos.write(kpf.getPrivateKeyBytes());
		// fos.flush();
		// fos.close();
		//
		// kpf = new KeyPairFactory("RSA", "SunRsaSign", 1024, null);
		// temp = new File("./agentPub.key");
		// fos = new FileOutputStream(temp);
		// // fos.write(kpf.getPublicKeyBytes());
		// fos.flush();
		// fos.close();
		//
		// temp = new File("./agentPriv.key");
		// fos = new FileOutputStream(temp);
		// // fos.write(kpf.getPrivateKeyBytes());
		// fos.flush();
		// fos.close();

	}

}

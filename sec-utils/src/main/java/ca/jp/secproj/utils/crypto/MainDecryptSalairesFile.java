package ca.jp.secproj.utils.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class MainDecryptSalairesFile {

	private static final boolean VERBOSE = true;

	private static FileInputStream fis;

	public static void main(String[] args) throws IOException {

		DecryptRSA rsaDH = new DecryptRSA(args[0], args[1], VERBOSE);

		if (VERBOSE) {
			System.out.println("q DH crypt: " + args[2]);
			System.out.println("p DH crypt: " + args[3]);
			System.out.println("g DH crypt: " + args[4]);
		}

		DecryptDiffieHellman ddh = new DecryptDiffieHellman(rsaDH.getUncryptedMessage(args[2]),
				rsaDH.getUncryptedMessage(args[3]), rsaDH.getUncryptedMessage(args[4]), VERBOSE);

		BigInteger gxy = ddh.getDHGxy();

		try {
			File f1 = new File("D:\\PermanentData\\USherbrooke\\GEI774\\salaires.mm.txt");

			fis = new FileInputStream(f1);
			byte[] fileBytes = new byte[(int) f1.length()];
			fis.read(fileBytes);
			SimpleStreamCipher ssc = new SimpleStreamCipher(fileBytes, gxy, VERBOSE);
			System.out.println("----Message décrypté----");
			System.out.println(ssc.getStreamCipheredMessage());

			// File f2 = new
			// File("D:\\PermanentData\\USherbrooke\\GEI774\\test.txt");
			// fis = new FileInputStream(f2);
			// fileBytes = new byte[(int) f2.length()];
			// fis.read(fileBytes);
			// System.out.println("----Message crypté----");
			// System.out.println(fileBytes.toString());
			// BigInteger key = new BigInteger("33423871");
			//
			// SimpleStreamCipher ssc2 = new SimpleStreamCipher(fileBytes, key);
			// System.out.println("----Message décrypté----");
			// System.out.println(ssc2.getStreamCipheredMessage());
		} catch (IOException ex) {

		} finally {
			fis.close();
		}

	}
}

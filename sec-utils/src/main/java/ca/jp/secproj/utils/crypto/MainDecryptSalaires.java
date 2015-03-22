package ca.jp.secproj.utils.crypto;

public class MainDecryptSalaires {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DecryptRSA rsa = new DecryptRSA(args[0], args[1], false);

		for (int i = 2; i < args.length; i++) {
			System.out.println(rsa.getUncryptedMessage(args[i]).toString());
		}

	}

}

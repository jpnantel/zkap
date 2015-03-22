package ca.jp.secproj.utils.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DecryptRSA {
	// private final static SecureRandom RANDOM = new SecureRandom();

	private boolean verbose;

	private BigInteger n;

	private BigInteger e;

	private BigInteger d;

	public DecryptRSA(String n, String e, boolean verbose) {
		this.n = new BigInteger(n);
		this.e = new BigInteger(e);
		this.verbose = verbose;

		BigInteger p = BigIntegerMath.pMinusOneFactor(this.n);
		BigInteger q = this.n.divide(p);
		BigInteger test = p.multiply(q);
		boolean success = test.equals(this.n);

		if (this.verbose) {
			System.out.println("n: " + this.n.toString());
			System.out.println("e: " + this.e.toString());
			System.out.println("p: " + p.toString());
			System.out.println("q: " + q.toString());
			System.out.println("P(p premier) avec MillerRabin(64): " + p.isProbablePrime(64));
			System.out.println("P(q premier) avec MillerRabin(64): " + q.isProbablePrime(64));
		}
		if (!success) {
			System.out.println("Erreur dans le calcul de p et q pour RSA");
			return;
		}
		BigInteger p_1 = p.subtract(BigInteger.ONE);
		BigInteger q_1 = q.subtract(BigInteger.ONE);
		BigInteger phi = p_1.multiply(q_1);

		if (this.verbose) {
			System.out.println("p-1: " + p_1.toString());
			System.out.println("q-1: " + q_1.toString());
			System.out.println("phi(n): " + phi.toString());
		}

		BigInteger[] euclidMatrix = BigIntegerMath.euclid(this.e, phi);
		if (euclidMatrix[0] == null || !euclidMatrix[0].equals(BigInteger.ONE)) {
			System.out.println("Impossible de trouver d");
		}

		this.d = BigIntegerMath.lnr(euclidMatrix[1], phi);
		if (this.verbose) {
			System.out.println("d * e == 1 mod phi(n) ?: " + d.multiply(this.e).mod(phi).equals(BigInteger.ONE));
			System.out.println("d: " + d.toString());
		}
	}

	public BigInteger getUncryptedMessage(String message) {
		return (new BigInteger(message)).modPow(d, n);
	}
}

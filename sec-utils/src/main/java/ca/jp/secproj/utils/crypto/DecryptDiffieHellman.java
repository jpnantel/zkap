package ca.jp.secproj.utils.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DecryptDiffieHellman {

	private final static SecureRandom RANDOM = new SecureRandom();

	private boolean verbose;

	private BigInteger q;

	private BigInteger p;

	private BigInteger g;

	public DecryptDiffieHellman(BigInteger q, BigInteger p, BigInteger g, boolean verbose) {
		this.q = q;
		this.p = p;
		this.g = g;
		this.verbose = verbose;
		
		execDHTests();
	}

	private void execDHTests() {
		double isQPrime = BigIntegerMath.primeProbability(q, 1, RANDOM);
		double isPPrime = BigIntegerMath.primeProbability(p, 64, RANDOM);
		boolean isPFactorOfQ = BigIntegerMath.lnr(p, q).equals(BigInteger.ONE);
		boolean gOK = g.modPow(q, p).equals(BigInteger.ONE);

		if (verbose) {
			System.out.println("--------------------------------");
			System.out.println("---Diffie-Hellman---------------");
			System.out.println("--------------------------------");
			System.out.println("q_DH décrypt: " + q.toString());
			System.out.println("p_DH décrypt: " + p.toString());
			System.out.println("g_DH décrypt: " + g.toString());
			System.out.println("Probabilité que q_DH soit premier: " + isQPrime);
			System.out.println("Probabilité que p_DH soit premier: " + isPPrime);
			System.out.println("p_DH est un facteur de q_DH?: " + (isPFactorOfQ ? "oui" : "non"));
			System.out.println("g_DH ok?: " + (gOK ? "oui" : "non"));
		}
	}

	public BigInteger getDHGxy() {
		if (p.subtract(g).equals(BigInteger.ONE)) {
			if (verbose) {
				System.out.println("g = p-1");
				System.out.println("La clé pour le cryptage par flux est soit: ");
				System.out.println("1:	" + g);
				System.out.println("ou");
				System.out.println("2:	1");
			}
			return g;
		}
		return null;
	}

}

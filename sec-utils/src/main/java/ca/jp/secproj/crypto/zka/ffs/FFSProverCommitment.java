package ca.jp.secproj.crypto.zka.ffs;

import java.math.BigInteger;
import java.security.SecureRandom;

import ca.jp.secproj.crypto.BigIntegerUtils;

public class FFSProverCommitment {
    private static final BigInteger TWO = new BigInteger("2");

    private BigInteger[] m;

    private BigInteger[] w;

    private FFSSetup setup;

    private SecureRandom random;

    public FFSProverCommitment(FFSSetup setup) {
	this.setup = setup;
	this.m = new BigInteger[setup.getNbRounds()];
	this.w = new BigInteger[setup.getNbRounds()];
	this.random = new SecureRandom();
    }

    public void computeCommitment() {
	// compute n-1
	BigInteger maxVal = setup.getN().add(BigInteger.ONE.negate());
	// loop over the m array and generate a random biginteger m_i
	for (int i = 0; i < setup.getNbRounds(); i++) {
	    m[i] = BigIntegerUtils.generateRandomBigInt(maxVal, random);
	    w[i] = m[i].modPow(TWO, setup.getN());
	}
    }
}

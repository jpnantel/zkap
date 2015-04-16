package ca.jp.secproj.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BigIntegerUtils {

    public static String[] BigIntArrayToString(BigInteger[] array) {

	String[] outArr = new String[array.length];
	for (int i = 0; i < array.length; i++) {
	    outArr[i] = array[i].toString();
	}
	return outArr;
    }

    public static BigInteger[] StringArrayToBigInt(String[] array) {

	BigInteger[] outArr = new BigInteger[array.length];
	for (int i = 0; i < array.length; i++) {
	    outArr[i] = new BigInteger(array[i]);
	}
	return outArr;
    }

    public static BigInteger generateRandomBigInt(BigInteger maxValue) {
	SecureRandom random = new SecureRandom();
	return generateRandomBigInt(new BigInteger("3"), maxValue, random);
    }

    public static BigInteger generateRandomBigInt(BigInteger maxValue, SecureRandom random) {
	return generateRandomBigInt(new BigInteger("3"), maxValue, random);
    }

    public static BigInteger generateRandomBigInt(BigInteger minValue, BigInteger maxValue, SecureRandom random) {
	BigInteger r;
	do {
	    r = new BigInteger(maxValue.bitLength(), random);
	} while (r.compareTo(minValue) > 0 && r.compareTo(maxValue) <= 0);
	return r;
    }

}

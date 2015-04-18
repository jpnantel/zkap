package ca.jp.secproj.utils.math;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class BigIntegerUtils {

    /**
     * Converts a biginteger array into an array of string representations.
     * 
     * @param inArr
     * @return
     */
    public static String[] BigIntArrayToString(BigInteger[] inArr) {
	if (inArr == null) {
	    return null;
	}
	String[] outArr = new String[inArr.length];
	for (int i = 0; i < inArr.length; i++) {
	    if (inArr[i] != null) {
		outArr[i] = inArr[i].toString();
	    }
	}
	return outArr;
    }

    /**
     * Converts an array of string into an array of bigintegers
     * 
     * @param inArr
     * @return
     */
    public static BigInteger[] StringArrayToBigInt(String[] inArr) throws ParseException {
	if (inArr == null) {
	    return null;
	}
	BigInteger[] outArr = new BigInteger[inArr.length];
	for (int i = 0; i < inArr.length; i++) {
	    if (inArr[i] != null) {
		try {
		    outArr[i] = new BigInteger(inArr[i]);
		} catch (NumberFormatException e) {
		    throw new ParseException(e.getMessage(), i);
		}
	    }
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

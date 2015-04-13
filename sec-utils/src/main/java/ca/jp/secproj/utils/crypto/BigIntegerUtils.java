package ca.jp.secproj.utils.crypto;

import java.math.BigInteger;

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

}

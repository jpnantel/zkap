package ca.jp.secproj.utils.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathHelper {

    public static String generateUniqueToken() {
	Random generateur = new Random();
	String token = "";

	int numberAsciiStart = 48;
	int numberAsciiEnd = 57;
	int letterAsciiStart = 65;
	int letterAsciiEnd = 90;

	int start = numberAsciiStart;
	int end = letterAsciiEnd;

	int numbers = 0;
	while (numbers < 32) {

	    long range = (long) end - (long) start + 1;
	    long fraction = (long) (range * generateur.nextDouble());
	    int randomNumber = (int) (fraction + start);

	    if ((randomNumber >= numberAsciiStart && randomNumber <= numberAsciiEnd)
		    || (randomNumber >= letterAsciiStart && randomNumber <= letterAsciiEnd)) {
		token += (char) (randomNumber);
		numbers++;
	    }
	}

	return token;
    }

    /**
     * Calcul de l'équart type sur un tableau de int
     * 
     * @param values
     * @return
     */
    public static float standardDeviation(int[] values) {
	int count = 0;
	int sum = 0;
	for (int v : values) {
	    sum += v;
	    count++;
	}
	float avg = ((float) sum) / count;
	float sumEqAvg = 0.0f;
	for (int v : values) {
	    sumEqAvg += Math.abs(avg - v);
	}
	return sumEqAvg / count;
    }

    /**
     * Calcul de l'équart type sur un tableau de float
     * 
     * @param values
     * @return
     */
    public static float standardDeviation(float[] values) {
	int count = 0;
	float sum = 0;
	for (float v : values) {
	    sum += v;
	    count++;
	}
	float avg = ((float) sum) / count;
	float sumEqAvg = 0.0f;
	for (float v : values) {
	    sumEqAvg += Math.abs(avg - v);
	}
	return sumEqAvg / count;
    }

    public static int sum(int[] ints) {
	int result = 0;
	for (int i = 0; i < ints.length; i++) {
	    result += ints[i];
	}
	return result;
    }

    public static int round(int val, int precision) {
	int remainder = val % precision;
	double threshold = ((double) precision) / 2d;
	if (remainder == 0) {
	    return val;
	} else if (remainder < threshold) {
	    return val - remainder;
	} else {
	    return val + precision - remainder;
	}
    }

    /**
     * Arrondi d'un double à un certain nombre de décimales.
     * 
     * @see <a
     *      href="http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places">http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places</a>
     * 
     * 
     * @param value
     * @param precision
     * @return
     */
    public static double round(double value, int precision) {
	if (precision < 0) {
	    throw new IllegalArgumentException();
	}
	BigDecimal bd = new BigDecimal(value);
	bd = bd.setScale(precision, RoundingMode.HALF_UP);
	return bd.doubleValue();
    }

    /**
     * Arrondi d'un float à un certain nombre de décimales.
     * 
     * @see <a
     *      href="http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places">http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places</a>
     * 
     * 
     * @param value
     * @param precision
     * @return
     */
    public static float round(float value, int precision) {
	if (precision < 0) {
	    throw new IllegalArgumentException();
	}
	BigDecimal bd = new BigDecimal(value);
	bd = bd.setScale(precision, RoundingMode.HALF_UP);
	return bd.floatValue();
    }
}
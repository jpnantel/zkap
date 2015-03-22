package ca.jp.secproj.utils.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

/*****************************************************************************
 * * Pollard p-1 Factorization * * * * Christer Karlsson * * * * Find a k such
 * that p-1 divides k!, k!=(p-1)*q * * Apply Fermat's little theorem: * * 2^(k!)
 * = 2^(q*(p-1)) = (2^(p-1))^q ~ 1^q ~1 (mod p) * * This implies that p divides
 * 2^(k!) - 1. * * Let M be the least poistive residue of 2^(k!) - 1 (mod n) * *
 * so that M = (2^k! - 1) -n*t for some integer t. * * As p divides both n and
 * 2^k! - 1, p divides M. * * A factor of n is therefore a non trivial gcd(M,n)
 * * * *
 *****************************************************************************/
public class PollardP1 {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        // Variables
        boolean solved = false;
        int bitlength = 32; //
        // Size of our prime numbers
        BigInteger k = BigInteger.ONE; // 1st
        // Counter
        BigInteger L = BigInteger.ONE;
        // Upper limit
        BigInteger p1;
        // 1st Prime number
        BigInteger p2; // 2nd Prime number
        BigInteger n = new BigInteger("5157437"); // Our random composite number

        BigInteger r; // Remainder
        // r[i] generate a test value
        // for p1 32 bits long,
        // probably prime.
        p1 = new BigInteger("16"); // Set p1 to composite number

        while (!p1.isProbablePrime(100)) {
            p1 = new BigInteger(bitlength, // size in bits //
                    100, // 1 in 100 chance not prime //
                    random);
        }
        if (p1.isProbablePrime(100))
            System.out.print("Prime p1: ");
        System.out.println(p1 + " " + p1.bitLength() + " bits long"); // generate
                                                                      // a
        // test value for p2 32 bits
        // long, probably prime.
        p2 = new BigInteger("16"); //
        // Set p2 to composite number
        while (!p2.isProbablePrime(100)) {
            p2 = new BigInteger(bitlength, // size in bits //
                    100, // 1 in 100 chance not prime
                    random);
        }
        if (p2.isProbablePrime(100))
            System.out.print("Prime p2: ");
        System.out.println(p2 + " " + p2.bitLength() + " bits long"); // Create
                                                                      // a
        // composite number
        n = p1.multiply(p2);
        System.out.print("Composite Number n: ");
        System.out.println(n + " " + n.bitLength() + " bits long");
        System.out.println("..."); //
        // Set r[1]
        r = new BigInteger("2");
        // Set the upper limit
        L = sqrt(n);
        // Start solving
        while (!solved && k.compareTo(L) < 0) {
            k = k.add(BigInteger.ONE);
            r = r.modPow(k, n);
            if (((r.subtract(BigInteger.ONE)).gcd(n)).compareTo(BigInteger.ONE) != 0) {
                System.out.println("We found a solution in " + k.subtract(BigInteger.ONE) + " iterations");
                System.out.print(n + " = " + (r.subtract(BigInteger.ONE)).gcd(n) + " * "
                        + n.divide((r.subtract(BigInteger.ONE)).gcd(n)));
                solved = true;
            }
        } // We
          // found no factor
        if (!solved)
            System.out.println("Unable to find a factorization with " + L + " iterations");
    }

    public static BigInteger sqrt(BigInteger x) {
        BigInteger p, s, one, two;
        one = new BigInteger("1");
        two = new BigInteger("2");
        p = x.divide(two);
        s = (x.divide(p.pow(1))).add(p.multiply(two.subtract(one)));
        s = s.divide(two);
        while (p.compareTo(s) != 0 && one.compareTo(p.subtract(s)) != 0) {
            p = s;
            s = (x.divide(p.pow(1))).add(p.multiply(two.subtract(one)));
            s = s.divide(two);
        } // Was xa perfect square?
        if ((s.multiply(s)).compareTo(x) == 0)
            return (s); // Make sure that s is the floor of sqrt(x)

        while ((s.multiply(s)).compareTo(x) < 0)
            s = s.add(one);
        s = s.subtract(one);
        return (s);
    }

}

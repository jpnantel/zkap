package ca.jp.secproj.utils.crypto.zka;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Classe représentant un challenge dont une partie peut être envoyée au serveur
 * (Feige-Fiat-Shamir partie Peggy).
 * 
 * @author nanteljp
 * 
 */
public class ZKPClientChallenge extends ZKPChallenge {

    /**
     * Eclipse generated
     */
    private static final long serialVersionUID = 7003658275050113995L;

    private static SecureRandom rnd = new SecureRandom();

    private static final BigInteger TWO = new BigInteger("2");

    private BigInteger a1;

    private BigInteger a2;

    private ZKPClientChallenge(BigInteger a1, BigInteger a2, BigInteger t1, BigInteger t2) {
        super(t1, t2);
        this.a1 = a1;
        this.a2 = a2;
    }

    /**
     * Méthode fabrique pour un challenge ZKP par Feige-Fiat-Shamir
     * 
     * @param r
     * @param n
     * @return
     */
    public static ZKPClientChallenge generateNewChallengePair(BigInteger r, BigInteger n) {
        BigInteger innerA1 = new BigInteger(1024, rnd);
        BigInteger innerA2 = r.multiply(innerA1.modInverse(n));
        BigInteger innerT1 = innerA1.modPow(TWO, n);
        BigInteger innerT2 = innerA2.modPow(TWO, n);
        // Vérification que: t1 * t2 = s mod n
        assert (r.modPow(TWO, n).equals(innerT1.multiply(innerT2).mod(n)));

        return new ZKPClientChallenge(innerA1, innerA2, innerT1, innerT2);
    }

    public ZKPChallenge deriveServerChallenge() {
        return new ZKPChallenge(super.t1, super.t2);
    }

    public BigInteger getA1() {
        return a1;
    }

    public BigInteger getA2() {
        return a2;
    }

}

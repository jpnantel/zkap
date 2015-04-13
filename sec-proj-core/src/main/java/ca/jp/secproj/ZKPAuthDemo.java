package ca.jp.secproj;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import ca.jp.secproj.utils.crypto.zka.ZKPClientChallengeSet;
import ca.jp.secproj.utils.log.Loggable;

@Loggable
@Component("ZKPAuthDemo")
public class ZKPAuthDemo {

    private static Logger logger;

    private SecureRandom rnd;

    private static final BigInteger TWO = new BigInteger("2");

    public ZKPAuthDemo() {
	this.rnd = new SecureRandom();
    }

    public void execute(String username, String password) {
	BigInteger p = BigInteger.probablePrime(1024, rnd);
	BigInteger q = BigInteger.probablePrime(1024, rnd);
	BigInteger n = p.multiply(q);

	// BigInteger r = BigInteger.probablePrime(1024, rnd);

	BigInteger r = null;
	try {
	    r = new BigInteger(
		    "est-ce que s doit être plus grand que n pour que la méthode fonctionne?".getBytes("UTF-8"));
	    r = r.nextProbablePrime();
	} catch (UnsupportedEncodingException e) {
	    logger.warn("", e);
	    return;
	}

	BigInteger s = r.modPow(TWO, n);

	logger.info("r: " + r.toString());
	logger.info("s: " + s.toString());
	logger.info("p: " + p.toString());
	logger.info("q: " + q.toString());
	logger.info("n: " + n.toString());

	ZKPClientChallengeSet challenges = new ZKPClientChallengeSet(100, n, r);
    }

}

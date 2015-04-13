package ca.jp.secproj;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptSaltHashGen {

    private static final Logger logger = LoggerFactory.getLogger(BCryptSaltHashGen.class);

    @Test
    public void generateHashAndSaltFromPasswordWithBcrypt() {

	String password = "password";

	long start = System.currentTimeMillis();

	String salt = BCrypt.gensalt(15);
	String hash = BCrypt.hashpw(password, salt);

	long total = System.currentTimeMillis() - start;

	Assert.assertNotNull(salt);
	Assert.assertNotNull(hash);

	logger.info("Salt: " + salt);
	logger.info("Hash: " + hash);
	logger.info("Time: " + total);
    }
}

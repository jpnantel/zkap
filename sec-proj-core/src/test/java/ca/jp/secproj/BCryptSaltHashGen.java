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

        for (int i = 10; i <= 30; i = i + 5) {

            String password = "password";

            long start = System.currentTimeMillis();

            String salt = BCrypt.gensalt(i);
            String hash = BCrypt.hashpw(password, salt);

            long total = System.currentTimeMillis() - start;

            Assert.assertNotNull(salt);
            Assert.assertNotNull(hash);

            logger.info("Nb bcrypt iter: " + i);
            logger.info("Salt: " + salt);
            logger.info("Hash: " + hash);
            logger.info("Time: " + total);
        }
    }
}

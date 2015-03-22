import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptSaltHashGen {

    private static final Logger logger = LoggerFactory.getLogger(BCryptSaltHashGen.class);

    public static void main(String[] args) {

        String password = "password";

        long start = System.currentTimeMillis();

        String salt = BCrypt.gensalt(15);
        String hash = BCrypt.hashpw(password, salt);

        long total = System.currentTimeMillis() - start;

        logger.info("Salt: " + salt);
        logger.info("Hash: " + hash);
        logger.info("Time: " + total);

    }
}

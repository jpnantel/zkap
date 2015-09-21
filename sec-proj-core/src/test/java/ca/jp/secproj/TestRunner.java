package ca.jp.secproj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRunner {

    private static Logger logger = LoggerFactory.getLogger(TestRunner.class);

    public static void main(String[] args) {

        // Result result = JUnitCore.runClasses(JPakeSerializeTest.class,
        // BCryptSaltHashGen.class, FFSOfflineTest.class);
        // for (Failure failure : result.getFailures()) {
        // logger.info(failure.toString());
        // }
        // if (result.wasSuccessful()) {
        // logger.info("Tests successful!");
        // }

        // FFSOfflineTest test = new FFSOfflineTest();
        BCryptSaltHashGen test = new BCryptSaltHashGen();
        test.generateHashAndSaltFromPasswordWithBcrypt();
        // try {
        // test.executeFFSAuhSuccess();
        // test.executeFFSAuhFail();
        // } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
        // logger.error("", e);
        // }
    }

}

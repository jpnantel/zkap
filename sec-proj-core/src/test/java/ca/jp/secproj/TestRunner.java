package ca.jp.secproj;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRunner {

    private static Logger logger = LoggerFactory.getLogger(TestRunner.class);

    public static void main(String[] args) {

	Result result = JUnitCore.runClasses(JPakeSerializeTest.class, BCryptSaltHashGen.class);
	for (Failure failure : result.getFailures()) {
	    logger.info(failure.toString());
	}
	if (result.wasSuccessful()) {
	    logger.info("Tests successful!");
	}
    }

}

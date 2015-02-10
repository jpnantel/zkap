package ca.crim.horcs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.crim.horcs.utils.MathHelper;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {

            ctx = new ClassPathXmlApplicationContext("/ca/crim/horcs/applicationContext.xml");
            // ctx.getAutowireCapableBeanFactory();
            // ctx.getBeanFactoryPostProcessors();

            // testJpake(ctx);
            // testJPakeNoRound3(ctx);
            // testStd(ctx);
            // testStdSaltMem(ctx);

            testZKPAuth(ctx);

        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    private static void testJpake(ClassPathXmlApplicationContext ctx) {
        logger.info("-------------------");
        logger.info("JPake complete test");
        logger.info("-------------------");
        JPakeAuthTest authTest = ctx.getBean("horcsAuthJPakeTest", JPakeAuthTest.class);

        loopTest(20, authTest);

    }

    private static void testJPakeNoRound3(ClassPathXmlApplicationContext ctx) {
        logger.info("-------------------");
        logger.info("JPake no round 3 test");
        logger.info("-------------------");
        JPakeAuthTestNoRound3 authTest = ctx.getBean("horcsAuthJPakeTest2", JPakeAuthTestNoRound3.class);
        loopTest(20, authTest);
    }

    private static void testStd(ClassPathXmlApplicationContext ctx) {
        logger.info("-------------------");
        logger.info("Std auth test");
        logger.info("-------------------");
        StdAuthTest authTest = ctx.getBean("horcsStdAuthTest", StdAuthTest.class);
        loopTest(20, authTest);
    }

    private static void testStdSaltMem(ClassPathXmlApplicationContext ctx) {
        logger.info("-------------------");
        logger.info("Std auth salt mem on client side");
        logger.info("-------------------");
        StdAuthTestSaltMem authTest = ctx.getBean("horcsStdAuthTestSaltMem", StdAuthTestSaltMem.class);
        loopTest(20, authTest);
    }

    private static void testZKPAuth(ClassPathXmlApplicationContext ctx) {
        logger.info("-------------------");
        logger.info("ZKP auth");
        logger.info("-------------------");
        ZKPAuth authTest = ctx.getBean("ZKPAuth", ZKPAuth.class);
        loopTest(20, authTest);
    }

    private static void loopTest(int nbLoops, AuthTest authTest) {

        List<Long> times = new ArrayList<>();
        long avg = 0;

        for (int i = 0; i < nbLoops; i++) {
            long start = System.currentTimeMillis();
            Object clientAuth = authTest.executeAuth();
            long thisTime = System.currentTimeMillis() - start;
            if (clientAuth != null) {
                avg += thisTime;
                times.add(thisTime);
            }
        }

        avg /= times.size();

        float stdDev = 0;

        for (Long t : times) {
            stdDev += Math.abs(avg - t);
        }
        stdDev /= times.size();

        logger.info("Number of failures: " + (nbLoops - times.size()));
        logger.info("Mean time (s): " + MathHelper.round((float) avg / 1000f, 4));
        logger.info("Standard deviation: " + MathHelper.round(stdDev / 1000f, 4));
    }

}

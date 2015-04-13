package ca.jp.secproj;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Usage: Sp�cifier le nom du composant � ex�cuter comme premier param�tre,
     * usager comme second, mot de passe comme troisi�me
     * 
     * @param args
     */
    public static void main(String[] args) {

	String componentToRun;
	String user;
	String password;

	if (args == null || args.length != 3 || StringUtils.isBlank(args[0]) || StringUtils.isBlank(args[1])
		|| StringUtils.isBlank(args[2])) {
	    logger.error("Param�tres invalides!");
	    logger.error("Usage: Sp�cifier le nom du composant � ex�cuter comme premier param�tre");
	    logger.error("usager comme second, mot de passe comme troisi�me");
	    logger.error("Ex: jPakeAuthDemo someuser somepassword");
	    return;
	} else {
	    componentToRun = args[0];
	    user = args[1];
	    password = args[2];
	}

	ClassPathXmlApplicationContext ctx = null;
	try {

	    ctx = new ClassPathXmlApplicationContext("/ca/jp/secproj/applicationContext.xml");

	    SecProjExecutable executable = ctx.getBean(componentToRun, SecProjExecutable.class);
	    executable.execute(user, password);
	} catch (BeansException e) {
	    logger.error("Composant sp�cifi� introuvable. ", e);
	} finally {
	    if (ctx != null) {
		ctx.close();
	    }
	}
    }
}

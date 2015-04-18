package ca.jp.secproj;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Usage: Spécifier le nom du composant à exécuter comme premier paramètre,
     * usager comme second, mot de passe comme troisième
     * 
     * @param args
     */
    public static void main(String[] args) {

	System.setProperty("javax.net.debug", "all");

	String componentToRun;
	String user;
	String password;

	if (args == null || args.length != 3 || StringUtils.isBlank(args[0]) || StringUtils.isBlank(args[1])
		|| StringUtils.isBlank(args[2])) {
	    logger.error("Paramètres invalides!");
	    logger.error("Usage: Spécifier le nom du composant à exécuter comme premier paramètre");
	    logger.error("usager comme second, mot de passe comme troisième");
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
	} catch (Exception e) {
	    logger.error("Composant spécifié introuvable. ", e);
	} finally {
	    if (ctx != null) {
		ctx.close();
	    }
	}
    }
}

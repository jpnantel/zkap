package ca.crim.horcs.utils.i18n;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Remplacement de RuntimeException avec messages I18n
 */
public class AppException extends RuntimeException {

    private static final Logger logger = Logger.getLogger(AppException.class.getName());

    private static final long serialVersionUID = 1L;

    public AppException(String msg) {
        super(msg);
        logger.log(Level.SEVERE, msg);
    }

    public AppException(String msg, Exception e) {
        super(msg, e);
        logger.log(Level.SEVERE, msg, e);
    }

}

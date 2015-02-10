package ca.crim.horcs.utils.lang;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

public class DateUtils {

    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd, HH:mm:ss";

    private static final Logger logger = Logger.getLogger(DateUtils.class.getName());

    public static String formatDate(Date laDate, String pattern) {
        if (laDate != null && !StringUtils.isEmpty(pattern)) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(laDate);
        }
        return null;
    }

    public static String formatDate(Date laDate) {
        try {
            return formatDate(laDate, DEFAULT_DATETIME_PATTERN);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erreur de format de date pour le pattern standard.", e);
        }
        return null;
    }
}

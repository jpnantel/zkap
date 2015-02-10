package ca.crim.horcs.utils.net;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import ca.crim.horcs.utils.domain.Credential;

/**
 * Classe d'utilitaires pour les communications par HTTP
 * 
 * @author nanteljp
 * 
 */
public class HttpUtils {

    private static Logger logger = Logger.getLogger(HttpUtils.class.getName());

    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * Construction de la valeur de l'en-tête d'authentification Basic pour un
     * appel HTTP.
     * 
     * @param cr
     *            Informations d'authentification utilisées pour construire
     *            l'en-tête.
     * @return La valeur de l'en-tête.
     */
    public static String buildBasicAuthHeaderValue(Credential cr) {
        if (cr != null && !StringUtils.isEmpty(cr.getUser()) && !StringUtils.isEmpty(cr.getPassword())) {
            String crString = cr.getUser() + ":" + cr.getPassword();
            try {
                return "BASIC " + new String(Base64.encodeBase64(crString.getBytes()), DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                logger.log(Level.SEVERE, "Erreur lors de l'encodage en base 64 de: " + crString, e);
                return null;
            }
        }
        return null;
    }

    /**
     * Retourne un objet {@link Credential} à partir de la valeur d'une en-tête
     * d'authentification.
     * 
     * @param authHeaderValue
     *            Valeur de l'en-tête d'authentification.
     * @return Les informations d'authentification décodées.
     */
    public static Credential getCredentialsFromHeader(String authHeaderValue) {
        if (!StringUtils.isEmpty(authHeaderValue)) {
            String authenticationScheme = authHeaderValue.substring(0, authHeaderValue.indexOf(" "));
            if (authenticationScheme.equalsIgnoreCase("basic")) {
                String credentials = authHeaderValue.substring(authHeaderValue.indexOf(" ") + 1);
                try {
                    credentials = new String(Base64.decodeBase64(credentials.getBytes()), DEFAULT_CHARSET);
                } catch (UnsupportedEncodingException e) {
                    logger.log(Level.SEVERE, "Erreur de décodage en base 64 de: " + credentials, e);
                    return null;
                }
                return new Credential(credentials.substring(0, credentials.indexOf(":")),
                        credentials.substring(credentials.indexOf(":") + 1));
            }
        }
        return null;
    }
}

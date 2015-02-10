package ca.crim.horcs.utils.string;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Série d'utilitaire pour manipuler des chaînes de caractères ou pour faire des
 * conversions à partir de chaînes de caractère.
 * 
 * @author nanteljp
 * 
 */
public class StringUtils {

    private static final String SEPARATOR = ";";

    private static final String DEFAULT_CHARSET = "UTF-8";

    public static String replaceInString(String str, Object... args) {
        if (str != null && args != null) {
            for (int i = 0; i < args.length; i++) {
                str = str.replaceAll("\\{" + i + "\\}", String.valueOf(args[i]));
            }
        }
        return str;
    }

    public static boolean equals(String s1, String s2) {
        return (s1 == s2) || (s1 != null && s2 != null && s1.equals(s2));
    }

    /**
     * Génération d'un identifiant uniques composé de 32 caractères
     * alphanumériques ASCII (10 chiffres et 26 lettres). Le nombre total de
     * chaînes est 36^32 donc environ 6,33e10^49. Le risque de collision est
     * donc très faible, même pour une collection d'un grand nombre de ces clés.
     * 
     * @return Un identifiant unique composé de 32 caractèrs ASCII.
     */
    public static String generateUniqueToken() {
        Random generateur = new Random();
        String token = "";

        int numberAsciiStart = 48;
        int numberAsciiEnd = 57;
        int letterAsciiStart = 65;
        int letterAsciiEnd = 90;

        int start = numberAsciiStart;
        int end = letterAsciiEnd;

        int numbers = 0;
        while (numbers < 32) {

            long range = (long) end - (long) start + 1;
            long fraction = (long) (range * generateur.nextDouble());
            int randomNumber = (int) (fraction + start);

            if ((randomNumber >= numberAsciiStart && randomNumber <= numberAsciiEnd)
                    || (randomNumber >= letterAsciiStart && randomNumber <= letterAsciiEnd)) {
                token += (char) (randomNumber);
                numbers++;
            }
        }

        return token;
    }

    /**
     * Conversion d'une {@link Map} de {@link String} en {@link String} pour le
     * passage en paramètre de méthodes XMLRPC ou comme propriété système d'une
     * JVM.
     * 
     * @param map
     *            Une {@link Map} à transformer.
     * @return Le résultat de la transformation.
     */
    public static String mapToString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : map.keySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            String value = map.get(key);
            try {
                stringBuilder.append((key != null ? URLEncoder.encode(key, DEFAULT_CHARSET) : ""));
                stringBuilder.append("=");
                stringBuilder.append(value != null ? URLEncoder.encode(value, DEFAULT_CHARSET) : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires " + DEFAULT_CHARSET + " encoding support", e);
            }
        }
        return stringBuilder.toString();
    }

    public static Map<String, String> stringToMap(String input) {
        Map<String, String> map = new HashMap<String, String>();

        String[] nameValuePairs = input.split("&");
        for (String nameValuePair : nameValuePairs) {
            String[] nameValue = nameValuePair.split("=");
            try {
                map.put(URLDecoder.decode(nameValue[0], DEFAULT_CHARSET),
                        nameValue.length > 1 ? URLDecoder.decode(nameValue[1], DEFAULT_CHARSET) : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires " + DEFAULT_CHARSET + " encoding support", e);
            }
        }

        return map;
    }

    /**
     * Conversion d'une collection de {@link String} en une seule ou chaque
     * élément de la collection est séparé par un le séparateur par défaut.
     * 
     * @param coll
     *            Collection de chaînes de caractères.
     * @return Une chaîne de caractère contenant tous les éléments de la
     *         collection.
     */
    public static String collectionToString(Collection<String> coll) {
        if (coll == null || coll.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(31 * coll.size());
        boolean first = true;
        for (String e : coll) {
            if (first) {
                first = false;
            } else {
                sb.append(SEPARATOR);
            }
            sb.append(e);
        }
        return sb.toString();
    }

    /**
     * Remise sous forme de collection d'une chaîne de caractères contenant des
     * éléments séparés par le séparateur par défaut.
     * 
     * @param string
     *            Chaîne de caractères construite de plusieurs chaînes de
     *            caractères concaténées et séparées par des virgules.
     * @return Une liste de chaîne de caractères qui était contenues dans la
     *         chaîne passée en paramètres.
     */
    public static List<String> stringToList(String string) {
        return Arrays.asList(string.split(SEPARATOR));
    }
}

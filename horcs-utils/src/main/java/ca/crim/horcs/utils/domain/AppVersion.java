package ca.crim.horcs.utils.domain;

import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe pour représenter une version de l'application. Un des buts peut être
 * de correctement évaluer qu'une version est antérieure à une autre. On peut
 * ainsi par exemple définir des fonctionnalités d'un web service quelconque qui
 * sont disponible seulement à partir d'une version spécifiée, ou entre deux
 * versions.
 * 
 * Exemples de versions: 1.3.6d (maj: 1, min: 3, sub: 6d) , 1.4.0c (maj: 1, min:
 * 4, sub: 0c)
 * 
 * 
 * @author nanteljp
 * 
 */
public class AppVersion implements Comparable<AppVersion> {

    private static final String PARSE_EXCEPTION_MESSAGE = "Unexpected version format. ";

    /**
     * Version à partir de laquelle on doit appliquer l'encryption.
     */
    public static final AppVersion ENCRYPT_FROM = new AppVersion("2", "0", "0a");

    /**
     * Principale version de l'application
     */
    private String major;

    /**
     * Version intermédiaire de la version principale.
     */
    private String minor;

    /**
     * Sous version.
     */
    private String sub;

    /**
     * Constructeur d'après les trois composantes d'une version.
     * 
     * @param major
     *            Version majeure.
     * @param minor
     *            Version mineure.
     * @param sub
     *            Sous-version.
     */
    public AppVersion(String major, String minor, String sub) {
        this.major = major;
        this.minor = minor;
        this.sub = sub;
    }

    /**
     * Construction de l'objet version d'après une représentation sous forme de
     * chaîne de caractère. Remplace la sérialisation par jackson car ne fait
     * pas appel au constructeur de la classe étendue.
     * 
     * @param version
     *            Version sous forme de chaîne de caractère.
     * @throws ParseException
     *             Exception lancée si le format de la chaîne de caractère ne
     *             correspond pas au format attendu.
     */
    public AppVersion(String version) throws ParseException {
        if (StringUtils.isEmpty(version)) {
            return;
        }
        String[] parts = version.split("\\.");
        // On s'attend à 3 parties non vides et non nulles.
        if (parts.length == 3) {
            if (StringUtils.isEmpty(parts[0])) {
                throw new ParseException(PARSE_EXCEPTION_MESSAGE, 0);
            }
            if (StringUtils.isEmpty(parts[1])) {
                throw new ParseException(PARSE_EXCEPTION_MESSAGE, parts[0].length());
            }
            if (StringUtils.isEmpty(parts[2])) {
                throw new ParseException(PARSE_EXCEPTION_MESSAGE, parts[0].length() + parts[1].length());
            }
            this.major = parts[0];
            this.minor = parts[1];
            this.sub = parts[2];
        } else {
            throw new ParseException(PARSE_EXCEPTION_MESSAGE, 0);
        }
    }

    protected Class<AppVersion> getSelfClass() {
        return AppVersion.class;
    }

    protected void mapValuesToSelf(AppVersion obj) {
        this.major = obj.major;
        this.minor = obj.minor;
        this.sub = obj.sub;
    }

    @Override
    public String toString() {
        if (StringUtils.isEmpty(major) || StringUtils.isEmpty(minor) || StringUtils.isEmpty(sub)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(15);
        sb.append(major).append(".").append(minor).append(".").append(sub);
        return sb.toString();
    }

    @Override
    public int compareTo(AppVersion o) {
        if (o == null) {
            return 1;
        }
        int majC = compare(major, o.major);
        if (majC == 0) {
            int minC = compare(minor, o.minor);
            if (minC == 0) {
                return compare(sub, o.sub);
            } else {
                return minC;
            }
        } else {
            return majC;
        }
    }

    /**
     * Méthode pour comparer deux chaînes de caractères null-proof.
     * 
     * @param s1
     *            Première chaîne à comparer.
     * @param s2
     *            Deuxième chaîne à comparer.
     * @return -1 si s1 < s2, si s1 > s2, 0 sinon (s1=s2)
     */
    private int compare(String s1, String s2) {
        if (s1 == null) {
            if (s2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (s2 == null) {
                return 1;
            } else {
                return s1.compareTo(s2);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (AppVersion.class.isInstance(o)) {
            if (compareTo(AppVersion.class.cast(o)) == 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (StringUtils.isEmpty(toString())) {
            return 0;
        } else {
            return toString().hashCode();
        }
    }

    /**
     * 
     * @return {@link AppVersion#major}
     */
    public String getMajor() {
        return major;
    }

    /**
     * 
     * @return {@link AppVersion#minor}
     */
    public String getMinor() {
        return minor;
    }

    /**
     * 
     * @return {@link AppVersion#sub}
     */
    public String getSub() {
        return sub;
    }

}

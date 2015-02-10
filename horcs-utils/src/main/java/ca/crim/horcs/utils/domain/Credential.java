package ca.crim.horcs.utils.domain;

/**
 * Classe utilisée pour conserver les informations d'authentification d'un
 * usager
 * 
 * @author nanteljp
 * 
 */
public class Credential {

    /**
     * Nom de la propriété utilisée pour stocker un objet {@link Credential}
     * dans une requête.
     */
    public static final String CREDENTIAL_PROP_NAME = "credential";

    /**
     * Nom d'utilisateur
     */
    private String user;

    /**
     * Mot de passe
     */
    private String password;

    /**
     * Constructeur par défaut.
     */
    public Credential() {
    }

    /**
     * Constructeur par paramètre d'un objet {@link Credential}
     * 
     * @param user
     *            Le nom d'utilisateur.
     * @param password
     *            Le mot de passe.
     */
    public Credential(String user, String password) {
        this.user = user;
        this.password = password;
    }

    /**
     * Accesseur
     * 
     * @return {@link Credential#user }
     */
    public String getUser() {
        return user;
    }

    /**
     * Mutateur
     * 
     * @param user
     *            {@link Credential#user }
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Accesseur
     * 
     * @return {@link Credential#password }
     */
    public String getPassword() {
        return password;
    }

    /**
     * Mutateur
     * 
     * @param password
     *            {@link Credential#password }
     */
    public void setPassword(String password) {
        this.password = password;
    }

}

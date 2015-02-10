package ca.crim.horcs.utils.domain;

/**
 * Classe utilis�e pour conserver les informations d'authentification d'un
 * usager
 * 
 * @author nanteljp
 * 
 */
public class Credential {

    /**
     * Nom de la propri�t� utilis�e pour stocker un objet {@link Credential}
     * dans une requ�te.
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
     * Constructeur par d�faut.
     */
    public Credential() {
    }

    /**
     * Constructeur par param�tre d'un objet {@link Credential}
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

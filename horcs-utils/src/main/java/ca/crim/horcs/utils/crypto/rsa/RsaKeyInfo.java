package ca.crim.horcs.utils.crypto.rsa;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import ca.crim.horcs.utils.crypto.KeyInfo;

/**
 * Classe contenant une clé RSA exposant plus d'informations sur sa nature sans
 * avoir à utiliser les {@link KeySpec} que l'on doit instancier à partir de
 * {@link KeyFactory}.
 * 
 * @author nanteljp
 * 
 */
public class RsaKeyInfo extends KeyInfo {

    /**
     * Énumération des types de clés RSA.
     * 
     * @author nanteljp
     * 
     */
    public enum KeyType {
        /**
         * Clé publique à distribuer à tous.
         */
        PUBLIC,
        /**
         * Clé privée à conserver en sécurité.
         */
        PRIVATE
    }

    /**
     * {@link KeyType Type} de la clé.
     * 
     */
    private KeyType keyType;

    /**
     * Taille de l'exposant de la clé.
     */
    private int exponentSize;

    /**
     * Constructeur du wrapper de clé RSA.
     * 
     * @param key
     *            La clé elle-même.
     * @param keyType
     *            {@link KeyType Type} de la clé.
     * @param modulusSize
     *            Taille du modulo de la clé.
     * @param exponentSize
     *            Taille de l'exposant de la clé.
     * @throws InvalidKeySpecException
     *             Si la taille de la clé n'est pas un multiple de 512.
     */
    public RsaKeyInfo(Key key, KeyType keyType, int modulusSize, int exponentSize) throws InvalidKeySpecException {
        super(key, modulusSize);
        if (modulusSize % 512 != 0) {
            throw new InvalidKeySpecException("Invalid modulus or exponent lenght. ");
        }
        this.key = key;
        this.keyType = keyType;
    }

    /**
     * Accès au {@link KeyType type} de la clé.
     * 
     * @return Le {@link KeyType type} de la clé.
     */
    public KeyType getKeyType() {
        return keyType;
    }

    /**
     * Accès à la taille du modulo de la clé.
     * 
     * @return La taille du modulo de la clé.
     */
    public int getModulusSize() {
        return keySize;
    }

    /**
     * Accès à la taille de l'exposant de la clé.
     * 
     * @return La taille de l'exposant de la clé.
     */
    public int getExponentSize() {
        return exponentSize;
    }

}
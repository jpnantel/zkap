package ca.jp.secproj.utils.crypto.rsa;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import ca.jp.secproj.utils.crypto.KeyInfo;

/**
 * Classe contenant une cl� RSA exposant plus d'informations sur sa nature sans
 * avoir � utiliser les {@link KeySpec} que l'on doit instancier � partir de
 * {@link KeyFactory}.
 * 
 * @author nanteljp
 * 
 */
public class RsaKeyInfo extends KeyInfo {

    /**
     * �num�ration des types de cl�s RSA.
     * 
     * @author nanteljp
     * 
     */
    public enum KeyType {
        /**
         * Cl� publique � distribuer � tous.
         */
        PUBLIC,
        /**
         * Cl� priv�e � conserver en s�curit�.
         */
        PRIVATE
    }

    /**
     * {@link KeyType Type} de la cl�.
     * 
     */
    private KeyType keyType;

    /**
     * Taille de l'exposant de la cl�.
     */
    private int exponentSize;

    /**
     * Constructeur du wrapper de cl� RSA.
     * 
     * @param key
     *            La cl� elle-m�me.
     * @param keyType
     *            {@link KeyType Type} de la cl�.
     * @param modulusSize
     *            Taille du modulo de la cl�.
     * @param exponentSize
     *            Taille de l'exposant de la cl�.
     * @throws InvalidKeySpecException
     *             Si la taille de la cl� n'est pas un multiple de 512.
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
     * Acc�s au {@link KeyType type} de la cl�.
     * 
     * @return Le {@link KeyType type} de la cl�.
     */
    public KeyType getKeyType() {
        return keyType;
    }

    /**
     * Acc�s � la taille du modulo de la cl�.
     * 
     * @return La taille du modulo de la cl�.
     */
    public int getModulusSize() {
        return keySize;
    }

    /**
     * Acc�s � la taille de l'exposant de la cl�.
     * 
     * @return La taille de l'exposant de la cl�.
     */
    public int getExponentSize() {
        return exponentSize;
    }

}
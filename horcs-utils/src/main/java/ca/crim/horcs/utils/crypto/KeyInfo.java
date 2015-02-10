package ca.crim.horcs.utils.crypto;

import java.security.Key;

/**
 * Wrapper pour une {@link Key clé} indiquant en plus sa taille.
 * 
 * @author nanteljp
 * 
 */
public class KeyInfo {

    /**
     * {@link Key clé}
     */
    protected Key key;

    /**
     * Taille de la clé.
     */
    protected int keySize;

    /**
     * Constructeur unique.
     * 
     * @param key
     *            Clé.
     * @param keySize
     *            Taille de la clé.
     */
    public KeyInfo(Key key, int keySize) {
        super();
        this.key = key;
        this.keySize = keySize;
    }

    /**
     * Accès à la clé.
     * 
     * @return La clé.
     */
    public Key getKey() {
        return key;
    }

    /**
     * Taille de la clé.
     * 
     * @return Taille de la clé.
     */
    public int getKeySize() {
        return keySize;
    }

}

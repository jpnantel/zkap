package ca.crim.horcs.utils.crypto;

import java.security.Key;

/**
 * Wrapper pour une {@link Key cl�} indiquant en plus sa taille.
 * 
 * @author nanteljp
 * 
 */
public class KeyInfo {

    /**
     * {@link Key cl�}
     */
    protected Key key;

    /**
     * Taille de la cl�.
     */
    protected int keySize;

    /**
     * Constructeur unique.
     * 
     * @param key
     *            Cl�.
     * @param keySize
     *            Taille de la cl�.
     */
    public KeyInfo(Key key, int keySize) {
        super();
        this.key = key;
        this.keySize = keySize;
    }

    /**
     * Acc�s � la cl�.
     * 
     * @return La cl�.
     */
    public Key getKey() {
        return key;
    }

    /**
     * Taille de la cl�.
     * 
     * @return Taille de la cl�.
     */
    public int getKeySize() {
        return keySize;
    }

}

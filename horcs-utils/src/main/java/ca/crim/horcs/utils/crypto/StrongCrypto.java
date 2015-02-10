package ca.crim.horcs.utils.crypto;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

/**
 * Classe abstraite utilis�e pour valider l'installation de Java Cryptography
 * Extension (JCE) Unlimited Strength Jurisdiction Policy Files. Cette
 * impl�mentation des {@link java.security.Provider Provider} permet
 * l'utilisation de jeux de chiffrement avec des cl�s de taille plus
 * s�curitaire.
 * 
 * @author nanteljp
 * 
 */
public abstract class StrongCrypto {

    /**
     * Constructeur effectuant la v�rification que Java Cryptography Extension
     * (JCE) Unlimited Strength Jurisdiction Policy Files est install�.
     */
    public StrongCrypto(boolean bypassStrongSecurityCheck) {
        if (!bypassStrongSecurityCheck) {
            try {
                if (Cipher.getMaxAllowedKeyLength("AES") <= 256) {
                    throw new RuntimeException("Please install the Java Cryptography Extension (JCE) Unlimited "
                            + "Strength Jurisdiction Policy Files. ");

                }
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(
                        "Unable to verify the availability of the Java Cryptography Extension (JCE) Unlimited "
                                + "Strength Jurisdiction Policy Files. ", e);
            }
        }
    }
}

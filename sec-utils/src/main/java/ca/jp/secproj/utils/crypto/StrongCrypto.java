package ca.jp.secproj.utils.crypto;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

/**
 * Classe abstraite utilisée pour valider l'installation de Java Cryptography
 * Extension (JCE) Unlimited Strength Jurisdiction Policy Files. Cette
 * implémentation des {@link java.security.Provider Provider} permet
 * l'utilisation de jeux de chiffrement avec des clés de taille plus
 * sécuritaire.
 * 
 * @author nanteljp
 * 
 */
public abstract class StrongCrypto {

    /**
     * Constructeur effectuant la vérification que Java Cryptography Extension
     * (JCE) Unlimited Strength Jurisdiction Policy Files est installé.
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

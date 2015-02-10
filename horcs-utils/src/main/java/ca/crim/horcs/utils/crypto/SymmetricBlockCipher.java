package ca.crim.horcs.utils.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Gestionnaire pour le cryptage par bloc de vecteurs de byte. Pour crypter, il
 * faut constuire l'objet, appeler la méthode d'initialisation tout en
 * conservant le vecteur d'initialisation (IV) qui est requis pour décrypter et
 * finalement appeler la méthode d'encryption. Pour décrypter, il suffit de
 * construire l'objet avec le même jeu de chiffrement et d'appeler la méthode de
 * décryptage avec la même clé et le même IV que lors de l'encryption.
 * 
 * @author nanteljp
 * 
 */
public class SymmetricBlockCipher extends AbstractBlockCipher {

    /**
     * Constructeur du gestionnaire de chiffrement par bloc. Si un jeu de
     * chiffrement n'est pas spécifié, AES en mode chain block chaining avec le
     * padding PKCS5 sera utilisé (probablement le jeu de chiffrement le plus
     * sécuritaire à ce jour (2012-11-28) lorsque la clé est de 256 bits et que
     * le IV est aléatoire.
     * 
     * @param cipherString
     *            La méthode de chiffrement à utiliser
     * @param chainingMode
     *            La méthode de re-génération de la clé
     * @param padding
     *            La méthode de padding à utiliser.
     * @throws NoSuchAlgorithmException
     *             Si ce jeu de chiffrement n'existe pas dans les Providers
     *             fournis
     * @throws NoSuchPaddingException
     *             Si cet algorithme de padding n'existe pas dans les Providers
     *             fournis
     * @throws NoSuchProviderException
     */
    public SymmetricBlockCipher(String cipherString, String chainingMode, String padding, String provider,
            boolean bypassSecurityCheck) throws NoSuchAlgorithmException, NoSuchPaddingException,
            NoSuchProviderException {
        super(cipherString, chainingMode, padding, provider, bypassSecurityCheck);
    }

    /**
     * Encryption des données passées en paramètre selon ce qui a été configuré
     * auparavant.
     * 
     * @param data
     *            Données à encrypter.
     * @return Les données encryptées.
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] encrypt(byte[] data) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(data);
    }

    /**
     * Décryption des données passées en paramètre.
     * 
     * @param data
     *            Données à décrypter
     * @param iv
     *            Vecteur d'initialisation
     * @param keyBytes
     *            Clé de chiffrement
     * @return Les données décryptées.
     * @throws IllegalBlockSizeException
     *             Si l'algorithme ne peut pas padder les données et que ces
     *             dernières ne forment pas un nombre de blocs complets.
     * @throws BadPaddingException
     *             S'il y a une erreur lors du déchiffrement en raison du
     *             padding.
     * @throws InvalidKeyException
     *             Si la clé provoque une erreur lors du déchiffrement.
     * @throws InvalidAlgorithmParameterException
     *             Si le vecteur d'initialisation provoque une erreur lors du
     *             déchiffrement.
     */
    public byte[] decrypt(byte[] data, byte[] iv, byte[] keyBytes) throws IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        SecretKeySpec key = new SecretKeySpec(keyBytes, cipherString);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher.doFinal(data);
    }

}

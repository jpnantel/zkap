package ca.jp.secproj.utils.crypto;

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
 * faut constuire l'objet, appeler la m�thode d'initialisation tout en
 * conservant le vecteur d'initialisation (IV) qui est requis pour d�crypter et
 * finalement appeler la m�thode d'encryption. Pour d�crypter, il suffit de
 * construire l'objet avec le m�me jeu de chiffrement et d'appeler la m�thode de
 * d�cryptage avec la m�me cl� et le m�me IV que lors de l'encryption.
 * 
 * @author nanteljp
 * 
 */
public class SymmetricBlockCipher extends AbstractBlockCipher {

    /**
     * Constructeur du gestionnaire de chiffrement par bloc. Si un jeu de
     * chiffrement n'est pas sp�cifi�, AES en mode chain block chaining avec le
     * padding PKCS5 sera utilis� (probablement le jeu de chiffrement le plus
     * s�curitaire � ce jour (2012-11-28) lorsque la cl� est de 256 bits et que
     * le IV est al�atoire.
     * 
     * @param cipherString
     *            La m�thode de chiffrement � utiliser
     * @param chainingMode
     *            La m�thode de re-g�n�ration de la cl�
     * @param padding
     *            La m�thode de padding � utiliser.
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
     * Encryption des donn�es pass�es en param�tre selon ce qui a �t� configur�
     * auparavant.
     * 
     * @param data
     *            Donn�es � encrypter.
     * @return Les donn�es encrypt�es.
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] encrypt(byte[] data) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(data);
    }

    /**
     * D�cryption des donn�es pass�es en param�tre.
     * 
     * @param data
     *            Donn�es � d�crypter
     * @param iv
     *            Vecteur d'initialisation
     * @param keyBytes
     *            Cl� de chiffrement
     * @return Les donn�es d�crypt�es.
     * @throws IllegalBlockSizeException
     *             Si l'algorithme ne peut pas padder les donn�es et que ces
     *             derni�res ne forment pas un nombre de blocs complets.
     * @throws BadPaddingException
     *             S'il y a une erreur lors du d�chiffrement en raison du
     *             padding.
     * @throws InvalidKeyException
     *             Si la cl� provoque une erreur lors du d�chiffrement.
     * @throws InvalidAlgorithmParameterException
     *             Si le vecteur d'initialisation provoque une erreur lors du
     *             d�chiffrement.
     */
    public byte[] decrypt(byte[] data, byte[] iv, byte[] keyBytes) throws IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        SecretKeySpec key = new SecretKeySpec(keyBytes, cipherString);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher.doFinal(data);
    }

}

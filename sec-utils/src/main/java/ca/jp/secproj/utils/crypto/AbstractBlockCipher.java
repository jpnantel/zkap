package ca.jp.secproj.utils.crypto;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe abstraite utilitaire pour envelopper un algorithme de chiffrement par
 * bloc. Procure les fonctionnalit�s de base comme l'instanciation,
 * l'initialisation de l'algorithme de chiffrement et la gestion des vecteurs
 * d'initialisation. La classe fournit aussi une m�thode utilisatire pour
 * g�n�rer une cl� symm�trique pour l'algorithme instanci�. Voir la
 * documentation de Sun JCA pour les combinaisons possibles de jeux de
 * chiffrement.
 * 
 * @author nanteljp
 * 
 */
public abstract class AbstractBlockCipher extends StrongCrypto {

    /**
     * Jeu de chiffrement utilis�.
     */
    protected Cipher cipher;

    /**
     * Nom de l'algorithme de chiffrement.
     */
    protected String cipherString;

    /**
     * Constructeur de base.
     * 
     * @param cipherString
     *            Algorithme de chiffrement � utiliser.
     * @param chainingMode
     *            Mode de r�insertion de la cl� de chiffrement.
     * @param padding
     *            Mode de rembourrage des donn�es � utiliser.
     * @param provider
     *            Fournisseur de service cryptographique � utiliser.
     * @param bypassSecurityCheck
     *            Vrai si on ne souhaite pas se restreindre � des tailles de
     *            cl�s tr�s s�curitaires. Si on met faux et que les fournisseurs
     *            cryptographiques sans limitations ne sont pas install�s, une
     *            exception sera lanc�es. (Voir: {@link StrongCrypto})
     * @throws NoSuchAlgorithmException
     *             Si l'algorithme est introuvable ppour le fournisseur sp�cifi�
     *             ou s'il n'existe pas dans aucun des fournisseurs sinon.
     * @throws NoSuchPaddingException
     *             Si l'algorithme de rembourrage des donn�es n'existe pas ou ne
     *             peut pas �tre utilis� avec l'algorithme de chiffrement
     *             sp�cifi�.
     * @throws NoSuchProviderException
     *             Si le fournisseur n'existe pas ou n'est pas enregistr� aupr�s
     *             du JRE.
     */
    public AbstractBlockCipher(String cipherString, String chainingMode, String padding, String provider,
            boolean bypassSecurityCheck) throws NoSuchAlgorithmException, NoSuchPaddingException,
            NoSuchProviderException {
        // V�rification de s�curit�.
        super(bypassSecurityCheck);
        // Si on d�tecte un champ manquant, utilisation de l'algorithme par
        // d�faut AES/CBC/PKCS5
        if (StringUtils.isEmpty(cipherString) || StringUtils.isEmpty(chainingMode) || StringUtils.isEmpty(padding)) {
            cipherString = "AES";
            chainingMode = "CBC";
            padding = "PKCS5Padding";
        }
        this.cipherString = cipherString;
        if (StringUtils.isEmpty(provider)) {
            this.cipher = Cipher.getInstance(cipherString + "/" + chainingMode + "/" + padding);
        } else {
            this.cipher = Cipher.getInstance(cipherString + "/" + chainingMode + "/" + padding, provider);
        }

    }

    /**
     * Initialisation de l'objet pour l'encryption sans avoir de vecteur
     * d'initialisation (IV) au pr�alable.
     * 
     * @param keyBytes
     *            Cl� d'encryption.
     * @return Le vecteur d'initialisation.
     * @throws InvalidKeyException
     *             Si la cl� d�clenche une erreur lors de l'initialisation de
     *             l'algorithme de chiffrement.
     * @throws InvalidParameterSpecException
     *             Si l'algorithme de chiffrement n'a pas besoin de vecteur
     *             d'initialisation.
     */
    public byte[] initForEncrypt(KeyInfo key) throws InvalidKeyException, InvalidParameterSpecException {
        cipher.init(Cipher.ENCRYPT_MODE, key.getKey());
        AlgorithmParameters params = cipher.getParameters();
        return params.getParameterSpec(IvParameterSpec.class).getIV();
    }

    /**
     * Initialisation de l'objet pour le chiffrement d'un message � partir d'une
     * cl� et d'un vecteur d'initialisation (IV).
     * 
     * @param keyBytes
     *            Cl� de chiffrement encod�e en base 64.
     * @param ivBytes
     *            Vecteur d'initialisation encod� en base 64.
     * @throws InvalidKeyException
     *             Si une erreur due � la cl� se produit lors de
     *             l'initialisation de l'algorithme de chiffrement.
     * @throws InvalidAlgorithmParameterException
     *             Si une erreur due au vecteur d'initialisation se produit lors
     *             de l'initialisation de l'algorithme de chiffrement.
     */
    public void initForEncrypt(KeyInfo key, byte[] ivBytes) throws InvalidKeyException,
            InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, key.getKey(), ivSpec);
    }

    /**
     * G�n�re une cl� de la longueur sp�cifi�e pour l'algorithme de chiffrement
     * par bloc sp�cifi�. La sortie est un tableau de byte.
     * 
     * @param keySize
     *            Taille de la cl� � g�n�rer en nombre de bits. (128, 192 ou 256
     *            pour AES)
     * @param random
     *            G�n�rateur de nombres al�atoires. Un nouveau sera cr�� si
     *            aucun n'est sp�cifi�.
     * @return Une cl� de chiffrement pour l'algorithme de chiffrement par bloc
     *         sp�cifi� de la taille sp�cifi�e.
     * @throws NoSuchAlgorithmException
     *             Si on sp�cifie un algorithme qui n'existe pas au constructeur
     *             de l'objet.
     */
    public SecretKey generateNewKey(int keySize, SecureRandom random) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(cipherString);
        if (random != null) {
            keyGen.init(keySize, random);
        } else {
            keyGen.init(keySize);
        }
        return keyGen.generateKey();
    }
}

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
 * bloc. Procure les fonctionnalités de base comme l'instanciation,
 * l'initialisation de l'algorithme de chiffrement et la gestion des vecteurs
 * d'initialisation. La classe fournit aussi une méthode utilisatire pour
 * générer une clé symmétrique pour l'algorithme instancié. Voir la
 * documentation de Sun JCA pour les combinaisons possibles de jeux de
 * chiffrement.
 * 
 * @author nanteljp
 * 
 */
public abstract class AbstractBlockCipher extends StrongCrypto {

    /**
     * Jeu de chiffrement utilisé.
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
     *            Algorithme de chiffrement à utiliser.
     * @param chainingMode
     *            Mode de réinsertion de la clé de chiffrement.
     * @param padding
     *            Mode de rembourrage des données à utiliser.
     * @param provider
     *            Fournisseur de service cryptographique à utiliser.
     * @param bypassSecurityCheck
     *            Vrai si on ne souhaite pas se restreindre à des tailles de
     *            clés très sécuritaires. Si on met faux et que les fournisseurs
     *            cryptographiques sans limitations ne sont pas installés, une
     *            exception sera lancées. (Voir: {@link StrongCrypto})
     * @throws NoSuchAlgorithmException
     *             Si l'algorithme est introuvable ppour le fournisseur spécifié
     *             ou s'il n'existe pas dans aucun des fournisseurs sinon.
     * @throws NoSuchPaddingException
     *             Si l'algorithme de rembourrage des données n'existe pas ou ne
     *             peut pas être utilisé avec l'algorithme de chiffrement
     *             spécifié.
     * @throws NoSuchProviderException
     *             Si le fournisseur n'existe pas ou n'est pas enregistré auprès
     *             du JRE.
     */
    public AbstractBlockCipher(String cipherString, String chainingMode, String padding, String provider,
            boolean bypassSecurityCheck) throws NoSuchAlgorithmException, NoSuchPaddingException,
            NoSuchProviderException {
        // Vérification de sécurité.
        super(bypassSecurityCheck);
        // Si on détecte un champ manquant, utilisation de l'algorithme par
        // défaut AES/CBC/PKCS5
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
     * d'initialisation (IV) au préalable.
     * 
     * @param keyBytes
     *            Clé d'encryption.
     * @return Le vecteur d'initialisation.
     * @throws InvalidKeyException
     *             Si la clé déclenche une erreur lors de l'initialisation de
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
     * Initialisation de l'objet pour le chiffrement d'un message à partir d'une
     * clé et d'un vecteur d'initialisation (IV).
     * 
     * @param keyBytes
     *            Clé de chiffrement encodée en base 64.
     * @param ivBytes
     *            Vecteur d'initialisation encodé en base 64.
     * @throws InvalidKeyException
     *             Si une erreur due à la clé se produit lors de
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
     * Génère une clé de la longueur spécifiée pour l'algorithme de chiffrement
     * par bloc spécifié. La sortie est un tableau de byte.
     * 
     * @param keySize
     *            Taille de la clé à générer en nombre de bits. (128, 192 ou 256
     *            pour AES)
     * @param random
     *            Générateur de nombres aléatoires. Un nouveau sera créé si
     *            aucun n'est spécifié.
     * @return Une clé de chiffrement pour l'algorithme de chiffrement par bloc
     *         spécifié de la taille spécifiée.
     * @throws NoSuchAlgorithmException
     *             Si on spécifie un algorithme qui n'existe pas au constructeur
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

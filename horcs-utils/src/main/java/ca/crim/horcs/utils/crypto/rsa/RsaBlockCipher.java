package ca.crim.horcs.utils.crypto.rsa;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import ca.crim.horcs.utils.crypto.AbstractBlockCipher;

/**
 * Chiffrement par bloc par RSA.
 * 
 * @author nanteljp
 * 
 */
public class RsaBlockCipher extends AbstractBlockCipher {

    private static Logger logger = Logger.getLogger(RsaBlockCipher.class.getName());

    /**
     * Taille recommandée pour l'échantillonage.
     */
    private final static int RECOMMENDED_SAMPLING_MODULUS = 20;

    /**
     * Taille d'échantillonage. Un élément par bloc de la taille spécifiée sera
     * sélectionné. Étant donné un ensemble de taille n dont les éléments sont
     * indexés par [0..n-1], si i est l'élément courant et x la fréquence
     * d'échantillonage, la règle pour déterminer la sélection de l'élément ou
     * non est: i = 0 mod x (i est dans la même classe d'équvalence que 0 avec
     * modulo x) -> choisi, non choisi sinon.
     */
    private final int samplingModulus;

    /**
     * Taille requise par le padding.
     */
    private int paddingSize;

    /**
     * Constructeur par défaut. Utilise le padding PKCS1 avec la fréquence
     * d'échantillonage recommandée
     * {@link RsaBlockCipher#RECOMMENDED_SAMPLING_MODULUS}
     * 
     * @throws NoSuchAlgorithmException
     *             Si l'algorithme RSA n'existe pas dans le provider SunJCE
     *             (serait très étonnant puisqu'il fait partie de Java standard
     *             depuis 1.4)
     * @throws NoSuchPaddingException
     *             Si l'algorithme de padding PKCS1 n'existe pas dans SunJCE.
     * @throws NoSuchProviderException
     *             Si le JRE est incapable de trouver SunJCE dans sa
     *             configuration des providers.
     */
    public RsaBlockCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        super("RSA", "ECB", "PKCS1PADDING", "SunJCE", true);
        this.paddingSize = 15;
        this.samplingModulus = RECOMMENDED_SAMPLING_MODULUS;

        logger.log(Level.INFO, super.cipher.getAlgorithm());
    }

    /**
     * Constructeur permettant de spécifier la fréquence d'échantillonage. Voir
     * {@link RsaBlockCipher#RsaBlockCipher()}.
     * 
     * @param samplingModulus
     *            Fréquence d'échantillonage. Voir
     *            {@link RsaBlockCipher#samplingModulus}
     * @throws NoSuchAlgorithmException
     *             Si l'algorithme RSA n'existe pas dans le provider SunJCE
     *             (serait très étonnant puisqu'il fait partie de Java standard
     *             depuis 1.4)
     * @throws NoSuchPaddingException
     *             Si l'algorithme de padding PKCS1 n'existe pas dans SunJCE.
     * @throws NoSuchProviderException
     *             Si le JRE est incapable de trouver SunJCE dans sa
     *             configuration des providers.
     */
    public RsaBlockCipher(int samplingModulus) throws NoSuchAlgorithmException, NoSuchPaddingException,
            NoSuchProviderException {
        super("RSA", "ECB", "PKCS1PADDING", "SunJCE", true);
        this.paddingSize = 15;
        this.samplingModulus = samplingModulus;
    }

    /**
     * Constructeur permettant de spécifier la fréquence d'échantillonage et le
     * schéma de padding à utiliser. Voir
     * {@link RsaBlockCipher#RsaBlockCipher()}.
     * 
     * @param samplingModulus
     *            Fréquence d'échantillonage. Voir
     *            {@link RsaBlockCipher#samplingModulus}.
     * @param padding
     *            Schéma de padding à utiliser par l'algorithme de chiffrement.
     * @param paddingSize
     *            Taille nécessaire en byte pour insérer le padding dans un bloc
     *            de données chiffrées.
     * @throws NoSuchAlgorithmException
     *             Si l'algorithme RSA n'existe pas dans le provider SunJCE
     *             (serait très étonnant puisqu'il fait partie de Java standard
     *             depuis 1.4)
     * @throws NoSuchPaddingException
     *             Si l'algorithme de padding spécifié n'existe pas dans SunJCE.
     * @throws NoSuchProviderException
     *             Si le JRE est incapable de trouver SunJCE dans sa
     *             configuration des providers.
     */
    public RsaBlockCipher(int samplingModulus, String padding, int paddingSize) throws NoSuchAlgorithmException,
            NoSuchPaddingException, NoSuchProviderException {
        super("RSA", "ECB", padding, "SunJCE", true);
        this.paddingSize = paddingSize;
        this.samplingModulus = samplingModulus;
    }

    /**
     * Décryption des données avec un chiffrement par bloc avec RSA à partir
     * d'un flux entrant vers un flux sortant.
     * 
     * @param input
     *            Données à décrypter.
     * @param output
     *            Données décryptées.
     * @param key
     *            Clé de chiffrement.
     * @throws InvalidKeyException
     *             Si la clé n'est pas valide ou n'a pas le bon format lors de
     *             l'initialisation de l'algorithme de chiffrement.
     * @throws IllegalBlockSizeException
     *             S'il se produit une erreur quant à la taille des blocs lors
     *             du chiffrement.
     * @throws BadPaddingException
     *             S'il se produit une erreur de padding lors du chiffrement.
     * @throws IOException
     *             S'il se produit une erreur lors de la lecture ou de
     *             l'écriture dans les flux de données.
     */
    public void decrypt(InputStream input, OutputStream output, RsaKeyInfo key) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        cipher.init(Cipher.DECRYPT_MODE, key.getKey());
        long start = System.currentTimeMillis();

        RsaUtils.blockCipherDecrypt(input, output, cipher, key.getModulusSize(), samplingModulus);
        logger.info("Time to decrypt (in ms): " + ((System.currentTimeMillis() - start)));
    }

    /**
     * Encryption des données avec un chiffrement par bloc avec RSA à partir
     * d'un flux entrant vers un flux sortant.
     * 
     * @param input
     *            Données à chiffrer.
     * @param output
     *            Données chiffrées.
     * @param key
     *            Clé de chiffrement.
     * @throws InvalidKeyException
     *             Si la clé n'est pas valide ou n'a pas le bon format lors de
     *             l'initialisation de l'algorithme de chiffrement.
     * @throws IllegalBlockSizeException
     *             S'il se produit une erreur quant à la taille des blocs lors
     *             du chiffrement.
     * @throws BadPaddingException
     *             S'il se produit une erreur de padding lors du chiffrement.
     * @throws IOException
     *             S'il se produit une erreur lors de la lecture ou de
     *             l'écriture dans les flux de données.
     */
    public void encrypt(InputStream input, OutputStream output, RsaKeyInfo key) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        cipher.init(Cipher.ENCRYPT_MODE, key.getKey());
        long start = System.currentTimeMillis();
        RsaUtils.blockCipherEncrypt(input, output, cipher, key.getModulusSize(), paddingSize, samplingModulus);
        logger.info("Time to encrypt (in ms): " + ((System.currentTimeMillis() - start)));
    }

    /**
     * Déchiffrement des données avec un chiffrement par bloc avec RSA à partir
     * d'un tableau de byte vers un tableau de byte.
     * 
     * @param input
     *            Données à chiffrer.
     * @param output
     *            Données chiffrées.
     * @param key
     *            Clé de chiffrement.
     * @throws InvalidKeyException
     *             Si la clé n'est pas valide ou n'a pas le bon format lors de
     *             l'initialisation de l'algorithme de chiffrement.
     * @throws IllegalBlockSizeException
     *             S'il se produit une erreur quant à la taille des blocs lors
     *             du chiffrement.
     * @throws BadPaddingException
     *             S'il se produit une erreur de padding lors du chiffrement.
     * @throws IOException
     *             S'il se produit une erreur lors de la lecture ou de
     *             l'écriture dans les flux de données.
     */
    public byte[] decrypt(byte[] input, RsaKeyInfo key) throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, IOException {
        cipher.init(Cipher.DECRYPT_MODE, key.getKey());
        long start = System.currentTimeMillis();

        byte[] out = RsaUtils.blockCipherDecrypt(input, cipher, key.getModulusSize(), samplingModulus);
        logger.info("Time to decrypt (in ms): " + ((System.currentTimeMillis() - start)));
        return out;
    }

    /**
     * Déchiffrement des données avec un chiffrement par bloc avec RSA à partir
     * d'un tableau de byte vers un tableau de byte.
     * 
     * @param input
     *            Données à chiffrer.
     * @param output
     *            Données chiffrées.
     * @param key
     *            Clé de chiffrement.
     * @throws InvalidKeyException
     *             Si la clé n'est pas valide ou n'a pas le bon format lors de
     *             l'initialisation de l'algorithme de chiffrement.
     * @throws IllegalBlockSizeException
     *             S'il se produit une erreur quant à la taille des blocs lors
     *             du chiffrement.
     * @throws BadPaddingException
     *             S'il se produit une erreur de padding lors du chiffrement.
     * @throws IOException
     *             S'il se produit une erreur lors de la lecture ou de
     *             l'écriture dans les flux de données.
     */
    public byte[] encrypt(byte[] input, RsaKeyInfo key) throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, IOException {
        cipher.init(Cipher.ENCRYPT_MODE, key.getKey());
        long start = System.currentTimeMillis();
        byte[] out = RsaUtils.blockCipherEncrypt(input, cipher, key.getModulusSize(), paddingSize, samplingModulus);
        logger.info("Time to encrypt (in ms): " + ((System.currentTimeMillis() - start)));
        return out;
    }
}

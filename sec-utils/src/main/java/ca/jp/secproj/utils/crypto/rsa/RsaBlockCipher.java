package ca.jp.secproj.utils.crypto.rsa;

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

import ca.jp.secproj.utils.crypto.AbstractBlockCipher;

/**
 * Chiffrement par bloc par RSA.
 * 
 * @author nanteljp
 * 
 */
public class RsaBlockCipher extends AbstractBlockCipher {

    private static Logger logger = Logger.getLogger(RsaBlockCipher.class.getName());

    /**
     * Taille recommand�e pour l'�chantillonage.
     */
    private final static int RECOMMENDED_SAMPLING_MODULUS = 20;

    /**
     * Taille d'�chantillonage. Un �l�ment par bloc de la taille sp�cifi�e sera
     * s�lectionn�. �tant donn� un ensemble de taille n dont les �l�ments sont
     * index�s par [0..n-1], si i est l'�l�ment courant et x la fr�quence
     * d'�chantillonage, la r�gle pour d�terminer la s�lection de l'�l�ment ou
     * non est: i = 0 mod x (i est dans la m�me classe d'�quvalence que 0 avec
     * modulo x) -> choisi, non choisi sinon.
     */
    private final int samplingModulus;

    /**
     * Taille requise par le padding.
     */
    private int paddingSize;

    /**
     * Constructeur par d�faut. Utilise le padding PKCS1 avec la fr�quence
     * d'�chantillonage recommand�e
     * {@link RsaBlockCipher#RECOMMENDED_SAMPLING_MODULUS}
     * 
     * @throws NoSuchAlgorithmException
     *             Si l'algorithme RSA n'existe pas dans le provider SunJCE
     *             (serait tr�s �tonnant puisqu'il fait partie de Java standard
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
     * Constructeur permettant de sp�cifier la fr�quence d'�chantillonage. Voir
     * {@link RsaBlockCipher#RsaBlockCipher()}.
     * 
     * @param samplingModulus
     *            Fr�quence d'�chantillonage. Voir
     *            {@link RsaBlockCipher#samplingModulus}
     * @throws NoSuchAlgorithmException
     *             Si l'algorithme RSA n'existe pas dans le provider SunJCE
     *             (serait tr�s �tonnant puisqu'il fait partie de Java standard
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
     * Constructeur permettant de sp�cifier la fr�quence d'�chantillonage et le
     * sch�ma de padding � utiliser. Voir
     * {@link RsaBlockCipher#RsaBlockCipher()}.
     * 
     * @param samplingModulus
     *            Fr�quence d'�chantillonage. Voir
     *            {@link RsaBlockCipher#samplingModulus}.
     * @param padding
     *            Sch�ma de padding � utiliser par l'algorithme de chiffrement.
     * @param paddingSize
     *            Taille n�cessaire en byte pour ins�rer le padding dans un bloc
     *            de donn�es chiffr�es.
     * @throws NoSuchAlgorithmException
     *             Si l'algorithme RSA n'existe pas dans le provider SunJCE
     *             (serait tr�s �tonnant puisqu'il fait partie de Java standard
     *             depuis 1.4)
     * @throws NoSuchPaddingException
     *             Si l'algorithme de padding sp�cifi� n'existe pas dans SunJCE.
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
     * D�cryption des donn�es avec un chiffrement par bloc avec RSA � partir
     * d'un flux entrant vers un flux sortant.
     * 
     * @param input
     *            Donn�es � d�crypter.
     * @param output
     *            Donn�es d�crypt�es.
     * @param key
     *            Cl� de chiffrement.
     * @throws InvalidKeyException
     *             Si la cl� n'est pas valide ou n'a pas le bon format lors de
     *             l'initialisation de l'algorithme de chiffrement.
     * @throws IllegalBlockSizeException
     *             S'il se produit une erreur quant � la taille des blocs lors
     *             du chiffrement.
     * @throws BadPaddingException
     *             S'il se produit une erreur de padding lors du chiffrement.
     * @throws IOException
     *             S'il se produit une erreur lors de la lecture ou de
     *             l'�criture dans les flux de donn�es.
     */
    public void decrypt(InputStream input, OutputStream output, RsaKeyInfo key) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        cipher.init(Cipher.DECRYPT_MODE, key.getKey());
        long start = System.currentTimeMillis();

        RsaUtils.blockCipherDecrypt(input, output, cipher, key.getModulusSize(), samplingModulus);
        logger.info("Time to decrypt (in ms): " + ((System.currentTimeMillis() - start)));
    }

    /**
     * Encryption des donn�es avec un chiffrement par bloc avec RSA � partir
     * d'un flux entrant vers un flux sortant.
     * 
     * @param input
     *            Donn�es � chiffrer.
     * @param output
     *            Donn�es chiffr�es.
     * @param key
     *            Cl� de chiffrement.
     * @throws InvalidKeyException
     *             Si la cl� n'est pas valide ou n'a pas le bon format lors de
     *             l'initialisation de l'algorithme de chiffrement.
     * @throws IllegalBlockSizeException
     *             S'il se produit une erreur quant � la taille des blocs lors
     *             du chiffrement.
     * @throws BadPaddingException
     *             S'il se produit une erreur de padding lors du chiffrement.
     * @throws IOException
     *             S'il se produit une erreur lors de la lecture ou de
     *             l'�criture dans les flux de donn�es.
     */
    public void encrypt(InputStream input, OutputStream output, RsaKeyInfo key) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        cipher.init(Cipher.ENCRYPT_MODE, key.getKey());
        long start = System.currentTimeMillis();
        RsaUtils.blockCipherEncrypt(input, output, cipher, key.getModulusSize(), paddingSize, samplingModulus);
        logger.info("Time to encrypt (in ms): " + ((System.currentTimeMillis() - start)));
    }

    /**
     * D�chiffrement des donn�es avec un chiffrement par bloc avec RSA � partir
     * d'un tableau de byte vers un tableau de byte.
     * 
     * @param input
     *            Donn�es � chiffrer.
     * @param output
     *            Donn�es chiffr�es.
     * @param key
     *            Cl� de chiffrement.
     * @throws InvalidKeyException
     *             Si la cl� n'est pas valide ou n'a pas le bon format lors de
     *             l'initialisation de l'algorithme de chiffrement.
     * @throws IllegalBlockSizeException
     *             S'il se produit une erreur quant � la taille des blocs lors
     *             du chiffrement.
     * @throws BadPaddingException
     *             S'il se produit une erreur de padding lors du chiffrement.
     * @throws IOException
     *             S'il se produit une erreur lors de la lecture ou de
     *             l'�criture dans les flux de donn�es.
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
     * D�chiffrement des donn�es avec un chiffrement par bloc avec RSA � partir
     * d'un tableau de byte vers un tableau de byte.
     * 
     * @param input
     *            Donn�es � chiffrer.
     * @param output
     *            Donn�es chiffr�es.
     * @param key
     *            Cl� de chiffrement.
     * @throws InvalidKeyException
     *             Si la cl� n'est pas valide ou n'a pas le bon format lors de
     *             l'initialisation de l'algorithme de chiffrement.
     * @throws IllegalBlockSizeException
     *             S'il se produit une erreur quant � la taille des blocs lors
     *             du chiffrement.
     * @throws BadPaddingException
     *             S'il se produit une erreur de padding lors du chiffrement.
     * @throws IOException
     *             S'il se produit une erreur lors de la lecture ou de
     *             l'�criture dans les flux de donn�es.
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

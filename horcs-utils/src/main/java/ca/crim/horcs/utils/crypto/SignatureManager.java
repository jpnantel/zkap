package ca.crim.horcs.utils.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

/**
 * Gestionnaire de signatures utilisant une paire de clés publique / privée pour
 * signer le message afin de s'assurer qu'il n'a pas été modifié en chemin.
 * 
 * @author nanteljp
 * 
 */
public class SignatureManager extends StrongCrypto {

    /**
     * Algorithme de signature utilisé.
     */
    private Signature sig;

    /**
     * Générateur de nombres aléatoires utilisé.
     */
    private SecureRandom sr;

    /**
     * Constructeur pour le gestionnaire de signatures. Aucune méthode ne
     * supporte les signatures DSA en raison des paramètres supplémentaires
     * requis. Une exception sera lancée si l'algorithme contient DSA, ou s'il
     * n'est pas un algorithme de signature.
     * 
     * @param algorithm
     * @param sr
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public SignatureManager(String algorithm, String provider, SecureRandom sr, boolean bypassSecurityCheck)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        super(bypassSecurityCheck);
        if (sr == null) {
            this.sr = new SecureRandom();
        } else {
            this.sr = sr;
        }
        if (algorithm == null || "".equals(algorithm)) {
            // MD5 considéré faible et SHA1 a quelques risques de collisions.
            algorithm = "SHA512withRSA";
        }
        if (provider != null) {
            this.sig = Signature.getInstance(algorithm, provider);
        } else {
            this.sig = Signature.getInstance(algorithm);
        }
    }

    /**
     * Calcul de la signature
     * 
     * @param content
     *            Contenu que l'on veut signer.
     * @param prKey
     *            Clé privée utilisée pour signer.
     * @return La signature des données passées en paramètre.
     * @throws InvalidKeyException
     *             Si la clé échoue è l'initialisation de l'algorithme de
     *             signature.
     * @throws SignatureException
     *             Si la signature ne peut être calculée.
     */
    public byte[] computeSignature(byte[] content, PrivateKey prKey) throws InvalidKeyException, SignatureException {
        sig.initSign(prKey, sr);
        sig.update(content);
        return sig.sign();
    }

    /**
     * Vérification de la signature
     * 
     * @param data
     *            Message pour lequel on veut vérifier la signature.
     * @param signature
     *            Signature à vérifier.
     * @param pubKey
     *            Clé publique associée à la clé privée utilisée pour signer.
     * @return Vrai si le message n'a pas été modifié depuis sa signature.
     * @throws InvalidKeyException
     *             S'il se produit une erreur lors de l'initialisation.
     * @throws SignatureException
     *             S'il se produit une erreur lors de la vérification.
     */
    public boolean verifySignature(byte[] data, byte[] signature, PublicKey pubKey) throws InvalidKeyException,
            SignatureException {
        sig.initVerify(pubKey);
        sig.update(data);
        return sig.verify(signature);
    }
}

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
 * Gestionnaire de signatures utilisant une paire de cl�s publique / priv�e pour
 * signer le message afin de s'assurer qu'il n'a pas �t� modifi� en chemin.
 * 
 * @author nanteljp
 * 
 */
public class SignatureManager extends StrongCrypto {

    /**
     * Algorithme de signature utilis�.
     */
    private Signature sig;

    /**
     * G�n�rateur de nombres al�atoires utilis�.
     */
    private SecureRandom sr;

    /**
     * Constructeur pour le gestionnaire de signatures. Aucune m�thode ne
     * supporte les signatures DSA en raison des param�tres suppl�mentaires
     * requis. Une exception sera lanc�e si l'algorithme contient DSA, ou s'il
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
            // MD5 consid�r� faible et SHA1 a quelques risques de collisions.
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
     *            Cl� priv�e utilis�e pour signer.
     * @return La signature des donn�es pass�es en param�tre.
     * @throws InvalidKeyException
     *             Si la cl� �choue � l'initialisation de l'algorithme de
     *             signature.
     * @throws SignatureException
     *             Si la signature ne peut �tre calcul�e.
     */
    public byte[] computeSignature(byte[] content, PrivateKey prKey) throws InvalidKeyException, SignatureException {
        sig.initSign(prKey, sr);
        sig.update(content);
        return sig.sign();
    }

    /**
     * V�rification de la signature
     * 
     * @param data
     *            Message pour lequel on veut v�rifier la signature.
     * @param signature
     *            Signature � v�rifier.
     * @param pubKey
     *            Cl� publique associ�e � la cl� priv�e utilis�e pour signer.
     * @return Vrai si le message n'a pas �t� modifi� depuis sa signature.
     * @throws InvalidKeyException
     *             S'il se produit une erreur lors de l'initialisation.
     * @throws SignatureException
     *             S'il se produit une erreur lors de la v�rification.
     */
    public boolean verifySignature(byte[] data, byte[] signature, PublicKey pubKey) throws InvalidKeyException,
            SignatureException {
        sig.initVerify(pubKey);
        sig.update(data);
        return sig.verify(signature);
    }
}

package ca.jp.secproj.utils.crypto.rsa;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.apache.commons.lang3.StringUtils;

import ca.jp.secproj.utils.crypto.StrongCrypto;

/**
 * Classe utilitaire pour envelopper la construction de paires de cl�s pour de
 * l'encryption � cl� publique / priv�e. Typiquement les algorithmes possibles
 * sont bas�s sur RSA comme DSA.
 * 
 * @author nanteljp
 * 
 */
public class KeyPairFactory {

    /**
     * G�n�rateur de cl�s � �tre initialis�.
     */
    private KeyPairGenerator kpg;

    /**
     * Paire de cl�s g�n�r�e.
     */
    private KeyPair kp;

    /**
     * Constructeur de la fabrique de paires de cl�s. On doit minimalement
     * sp�cifier un algorithme et on peut aussi sp�cifier un {@link Provider
     * provider}. Il faut aussi sp�cifier la taille de la cl� voulue. On peut
     * sp�cifier un g�n�rateur de nombres al�atoires s�curitaire, mais un
     * nouveau sera cr�� si aucun n'est sp�cifi�. Il faut installer les versions
     * sans restrictions des fournisseurs de service cryptographiques pour
     * utiliser des cl�s de taille plus s�curitaire (voir: {@link StrongCrypto}
     * ).
     * 
     * @param algorithm
     *            Nom de l'algorithme pour lequel on veut une paire de cl�s.
     * @param provider
     *            Nom du fournisseur de service cryptographique. (Facultatif)
     * @param keySize
     *            Taille de la cl� � g�n�rer.
     * @param random
     *            G�n�rateur de nombre al�atoire s�curitaires.
     * @throws NoSuchAlgorithmException
     *             Si l'algorithme n'existe pas pour le fournisseur sp�cifi� ou
     *             n'existe pas dans aucun fourniseeur sinon.
     * @throws NoSuchProviderException
     *             Si le fournisseur cryptographique n'existe pas. V�rifier le
     *             nom et / ou consulter la documentation de Sun JCA.
     */
    public KeyPairFactory(String algorithm, String provider, int keySize, SecureRandom random)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        if (StringUtils.isEmpty(provider)) {
            kpg = KeyPairGenerator.getInstance(algorithm);
        } else {
            kpg = KeyPairGenerator.getInstance(algorithm, provider);
        }
        if (random != null) {
            kpg.initialize(keySize, random);
        } else {
            kpg.initialize(keySize);
        }
        kp = kpg.generateKeyPair();
    }

    /**
     * Acc�s � la cl� publique de la paire.
     * 
     * @return La cl� publique g�n�r�e.
     */
    public PublicKey getPublicKey() {
        return kp.getPublic();
    }

    /**
     * Acc�s � la cl� priv�e g�n�r�e.
     * 
     * @return La cl� priv�e.
     */
    public PrivateKey getPrivateKey() {
        return kp.getPrivate();
    }

}

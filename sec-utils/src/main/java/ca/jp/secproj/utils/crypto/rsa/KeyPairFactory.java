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
 * Classe utilitaire pour envelopper la construction de paires de clés pour de
 * l'encryption à clé publique / privée. Typiquement les algorithmes possibles
 * sont basés sur RSA comme DSA.
 * 
 * @author nanteljp
 * 
 */
public class KeyPairFactory {

    /**
     * Générateur de clés à être initialisé.
     */
    private KeyPairGenerator kpg;

    /**
     * Paire de clés générée.
     */
    private KeyPair kp;

    /**
     * Constructeur de la fabrique de paires de clés. On doit minimalement
     * spécifier un algorithme et on peut aussi spécifier un {@link Provider
     * provider}. Il faut aussi spécifier la taille de la clé voulue. On peut
     * spécifier un générateur de nombres aléatoires sécuritaire, mais un
     * nouveau sera créé si aucun n'est spécifié. Il faut installer les versions
     * sans restrictions des fournisseurs de service cryptographiques pour
     * utiliser des clés de taille plus sécuritaire (voir: {@link StrongCrypto}
     * ).
     * 
     * @param algorithm
     *            Nom de l'algorithme pour lequel on veut une paire de clés.
     * @param provider
     *            Nom du fournisseur de service cryptographique. (Facultatif)
     * @param keySize
     *            Taille de la clé à générer.
     * @param random
     *            Générateur de nombre aléatoire sécuritaires.
     * @throws NoSuchAlgorithmException
     *             Si l'algorithme n'existe pas pour le fournisseur spécifié ou
     *             n'existe pas dans aucun fourniseeur sinon.
     * @throws NoSuchProviderException
     *             Si le fournisseur cryptographique n'existe pas. Vérifier le
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
     * Accès à la clé publique de la paire.
     * 
     * @return La clé publique générée.
     */
    public PublicKey getPublicKey() {
        return kp.getPublic();
    }

    /**
     * Accès à la clé privée générée.
     * 
     * @return La clé privée.
     */
    public PrivateKey getPrivateKey() {
        return kp.getPrivate();
    }

}

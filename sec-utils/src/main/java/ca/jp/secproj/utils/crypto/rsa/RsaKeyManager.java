package ca.jp.secproj.utils.crypto.rsa;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.TreeMap;

import ca.jp.secproj.utils.crypto.KeyManager;
import ca.jp.secproj.utils.crypto.rsa.RsaKeyInfo.KeyType;
import ca.jp.secproj.utils.domain.AppVersion;

/**
 * Gestionnaire de clés pour le chifffrement par RSA.
 * 
 * 
 * @author nanteljp
 * 
 */
public class RsaKeyManager extends KeyManager<RsaKeyInfo> {

    /**
     * Ajout d'une clé dans le magasin à partir de sa valeur encodée binaire.
     * 
     * @param keyName
     *            Nom de la clé.
     * @param keyBytes
     *            Valeur binaire de la clé encodée.
     * @param keyType
     *            Type de clé (publique ou privée).
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public boolean addKey(String keyName, byte[] keyBytes, KeyType keyType, AppVersion av)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        TreeMap<AppVersion, RsaKeyInfo> keys = keyMap.get(keyName);

        // Instanciation d'une fabrique à clé RSA.
        KeyFactory kf = KeyFactory.getInstance("RSA");
        Key key = null;
        RsaKeyInfo ki = null;
        switch (keyType) {
        case PUBLIC:
            // Génération d'une clé publique à partir de l'encodage X509.
            key = kf.generatePublic(new X509EncodedKeySpec(keyBytes));
            // Récupération de la spécification de clé publique à partir de
            // la clé fabriquée à l'aide de la fabrique.
            RSAPublicKeySpec pubKs = kf.getKeySpec(key, RSAPublicKeySpec.class);
            // Instanciation du key info avec les information récupérées.
            ki = new RsaKeyInfo(key, keyType, pubKs.getModulus().bitLength(), pubKs.getPublicExponent().bitLength());
            break;
        case PRIVATE:
            // Génération d'une clé privée à partir de l'encodage PKCS8 de
            // la clé.
            key = kf.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            // Génération de la spécification de la clé à partir de la clé
            // générée et de la fabrique.
            RSAPrivateCrtKeySpec privKs = kf.getKeySpec(key, RSAPrivateCrtKeySpec.class);
            // Instanciation du keyinfo.
            ki = new RsaKeyInfo(key, keyType, privKs.getModulus().bitLength(), privKs.getPrivateExponent().bitLength());
            break;
        }
        if (ki != null) {
            if (keys == null) {
                keys = new TreeMap<AppVersion, RsaKeyInfo>();
                keyMap.put(keyName, keys);
            }
            keys.put(av, ki);
            return true;
        }
        return false;
    }
}

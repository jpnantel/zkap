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
 * Gestionnaire de cl�s pour le chifffrement par RSA.
 * 
 * 
 * @author nanteljp
 * 
 */
public class RsaKeyManager extends KeyManager<RsaKeyInfo> {

    /**
     * Ajout d'une cl� dans le magasin � partir de sa valeur encod�e binaire.
     * 
     * @param keyName
     *            Nom de la cl�.
     * @param keyBytes
     *            Valeur binaire de la cl� encod�e.
     * @param keyType
     *            Type de cl� (publique ou priv�e).
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public boolean addKey(String keyName, byte[] keyBytes, KeyType keyType, AppVersion av)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        TreeMap<AppVersion, RsaKeyInfo> keys = keyMap.get(keyName);

        // Instanciation d'une fabrique � cl� RSA.
        KeyFactory kf = KeyFactory.getInstance("RSA");
        Key key = null;
        RsaKeyInfo ki = null;
        switch (keyType) {
        case PUBLIC:
            // G�n�ration d'une cl� publique � partir de l'encodage X509.
            key = kf.generatePublic(new X509EncodedKeySpec(keyBytes));
            // R�cup�ration de la sp�cification de cl� publique � partir de
            // la cl� fabriqu�e � l'aide de la fabrique.
            RSAPublicKeySpec pubKs = kf.getKeySpec(key, RSAPublicKeySpec.class);
            // Instanciation du key info avec les information r�cup�r�es.
            ki = new RsaKeyInfo(key, keyType, pubKs.getModulus().bitLength(), pubKs.getPublicExponent().bitLength());
            break;
        case PRIVATE:
            // G�n�ration d'une cl� priv�e � partir de l'encodage PKCS8 de
            // la cl�.
            key = kf.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            // G�n�ration de la sp�cification de la cl� � partir de la cl�
            // g�n�r�e et de la fabrique.
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

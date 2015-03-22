package ca.jp.secproj.utils.crypto;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import ca.jp.secproj.utils.domain.AppVersion;

/**
 * Gestionnaire de clés de type T héritant de {@link KeyInfo}
 * 
 * @author nanteljp
 * 
 * @param <T>
 *            Classe qui est une spécialisation de {@link KeyInfo}
 */
public abstract class KeyManager<T extends KeyInfo> {

    /**
     * Conteneur de clés par nom.
     */
    protected Map<String, TreeMap<AppVersion, T>> keyMap;

    /**
     * Constructeur par défaut.
     */
    public KeyManager() {
        this.keyMap = new HashMap<String, TreeMap<AppVersion, T>>();
    }

    /**
     * Accès à une clé par son nom et la version.
     * 
     * @param keyName
     *            Nom de la clé.
     * @param av
     *            Version de l'application
     * @return Clé ayant le nom recherché et dont la version est inférieure ou
     *         égale à la version demandée. La méthode renvoie la plus grande
     *         clé inférieure ou égale à la version spécifiée.
     */
    public T getKey(String keyName, AppVersion av) {
        TreeMap<AppVersion, T> keys = keyMap.get(keyName);
        T key = keys.get(av);
        if (key != null) {
            return key;
        }
        Entry<AppVersion, T> e = keys.lowerEntry(av);
        if (e != null) {
            return e.getValue();
        }
        return null;
    }

    /**
     * Retrait d'une clé.
     * 
     * @param keyName
     *            Nom de la clé à retirer.
     * @return Vrai si la clé a été retirée avec succès, faux sinon.
     */
    public boolean removeKey(String keyName) {
        return keyMap.remove(keyName) != null;
    }

}

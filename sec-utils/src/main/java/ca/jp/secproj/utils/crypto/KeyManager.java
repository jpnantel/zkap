package ca.jp.secproj.utils.crypto;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import ca.jp.secproj.utils.domain.AppVersion;

/**
 * Gestionnaire de cl�s de type T h�ritant de {@link KeyInfo}
 * 
 * @author nanteljp
 * 
 * @param <T>
 *            Classe qui est une sp�cialisation de {@link KeyInfo}
 */
public abstract class KeyManager<T extends KeyInfo> {

    /**
     * Conteneur de cl�s par nom.
     */
    protected Map<String, TreeMap<AppVersion, T>> keyMap;

    /**
     * Constructeur par d�faut.
     */
    public KeyManager() {
        this.keyMap = new HashMap<String, TreeMap<AppVersion, T>>();
    }

    /**
     * Acc�s � une cl� par son nom et la version.
     * 
     * @param keyName
     *            Nom de la cl�.
     * @param av
     *            Version de l'application
     * @return Cl� ayant le nom recherch� et dont la version est inf�rieure ou
     *         �gale � la version demand�e. La m�thode renvoie la plus grande
     *         cl� inf�rieure ou �gale � la version sp�cifi�e.
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
     * Retrait d'une cl�.
     * 
     * @param keyName
     *            Nom de la cl� � retirer.
     * @return Vrai si la cl� a �t� retir�e avec succ�s, faux sinon.
     */
    public boolean removeKey(String keyName) {
        return keyMap.remove(keyName) != null;
    }

}

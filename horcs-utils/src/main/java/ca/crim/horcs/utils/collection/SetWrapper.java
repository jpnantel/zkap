package ca.crim.horcs.utils.collection;

import java.util.ArrayList;
import java.util.List;

import org.apache.taglibs.standard.tag.common.core.NullAttributeException;

/**
 * Classe utilisée pour envelopper des objects quelconques avec un autre type
 * d'objet qui le remplace son comportement pour les collections
 * 
 * @author nanteljp
 * 
 * @param <T>
 * @param <U>
 */
public class SetWrapper<T extends Comparable<T>, U> implements Comparable<SetWrapper<T, U>> {

    private T key;

    private U value;

    public SetWrapper(T key, U value) throws NullAttributeException {
        if (key == null) {
            throw new NullAttributeException("Impossible de créer l'objet avec un paramètre null", "key");
        }
        if (value == null) {
            throw new NullAttributeException("Impossible de créer l'objet avec un paramètre null", "value");
        }
        this.value = value;
        this.key = key;
    }

    @Override
    public int compareTo(SetWrapper<T, U> o) {
        return this.key.compareTo(o.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode() + value.hashCode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        SetWrapper<T, U> c = null;
        try {
            c = SetWrapper.class.cast(o);
        } catch (ClassCastException e) {
            return false;
        }
        return c.key.equals(this.key) && c.value.equals(this.value);
    }

    public U getValue() {
        return value;
    }

    public T getKey() {
        return key;
    }

    public static <T extends Comparable<T>, U> List<SetWrapper<T, U>> sharedValues(List<SetWrapper<T, U>> l1, T[] t1) {
        List<SetWrapper<T, U>> lr = new ArrayList<SetWrapper<T, U>>();
        for (SetWrapper<T, U> l : l1) {
            for (int i = 0; i < t1.length; i++) {
                if (l.getKey().compareTo(t1[i]) == 0) {
                    lr.add(l);
                }
            }
        }
        return lr;
    }

}

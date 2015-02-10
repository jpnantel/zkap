package ca.crim.horcs.utils.collection;

/**
 * Permet le stockage d'un objet et d'une clé de comparaison pour les tri
 * 
 * @author bondst
 * 
 * @param <T>
 * @param <U>
 */
public class Pair<T> implements Comparable<Pair<T>> {

    private T value;

    @SuppressWarnings("rawtypes")
    private Comparable key;

    public Pair(T value, Comparable<?> key) {
        this.value = value;
        this.key = key;

    }

    @SuppressWarnings("unchecked")
    public int compareTo(Pair<T> o) {
        int compareTo = key.compareTo(o.key);
        return compareTo;
    }

    public T value() {
        return value;
    }

}

package ca.crim.horcs.utils.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Retourne les éléments de objects triés en ordre croissant selon values
 * 
 * @author bondst
 * 
 * @param <T>
 */
public class SortedIterator<T> implements Iterator<T> {

    private T[] objects;

    private int objectsLen;

    private int[] values;

    boolean reversed;

    private int startIdx;

    private boolean findNextDone;

    private T next;

    /**
     * Constructeur
     * 
     * @param objects
     * @param values
     */
    public SortedIterator(T[] objects, int[] values) {
        this(objects, values, objects.length, false);
    }

    /**
     * Constructeur
     * 
     * @param objects
     * @param values
     */
    public SortedIterator(T[] objects, int[] values, boolean reversed) {
        this(objects, values, objects.length, reversed);
    }

    /**
     * Constructeur
     * 
     * @param objects
     * @param values
     */
    public SortedIterator(T[] objects, int[] values, int objectsLen, boolean reversed) {
        this.objects = objects;
        this.values = values;
        this.objectsLen = objectsLen;
        this.reversed = reversed;
        this.startIdx = 0;
        for (int x = 0; x < values.length; x++) {
            if (values[x] == -1 || objects[x] == null) {
                // Place au début les invalides
                T tmpO = objects[startIdx];
                int tmpV = values[startIdx];
                objects[startIdx] = objects[x];
                values[startIdx] = values[x];
                objects[x] = tmpO;
                values[x] = tmpV;
                startIdx++;
            }
        }
    }

    /**
     * Place dans "next" la prochaine valeur la plus petite non utilisée
     */
    private void findNext() {
        next = null;
        int minIdx = -1;
        for (int x = startIdx; x < objectsLen; x++) {
            if ((minIdx == -1 || (reversed && values[x] > values[minIdx]) || (!reversed && values[x] < values[minIdx]))) {
                minIdx = x;
            }
        }
        if (minIdx >= 0) {
            next = objects[minIdx];
            // Modification de startIdx
            T tmpO = objects[startIdx];
            int tmpV = values[startIdx];
            objects[startIdx] = objects[minIdx];
            values[startIdx] = values[minIdx];
            objects[minIdx] = tmpO;
            values[minIdx] = tmpV;
            startIdx++;

        }
        findNextDone = true;
    }

    @Override
    public boolean hasNext() {
        if (!findNextDone) {
            findNext();
        }
        return next != null;
    }

    @Override
    public T next() {
        if (!findNextDone) {
            findNext();
        }
        if (next == null) {
            throw new NoSuchElementException();
        }
        T result = next;
        this.findNextDone = false;
        return result;
    }
    
    public int getScore() {
        return this.values[startIdx];
    }

    @Override
    public void remove() {

    }

}

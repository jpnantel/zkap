package ca.crim.horcs.utils.collection;

import java.util.Collection;
import java.util.Comparator;

public class CollectionSizeComparator implements Comparator<Collection<?>> {

    public int compare(Collection<?> o1, Collection<?> o2) {
        int thisVal = o1.size();
        int anotherVal = o2.size();
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

}

package ca.crim.horcs.utils.collection;

import java.util.Comparator;

public class IntArrayLengthComparator implements Comparator<int[]> {

    public int compare(int[] o1, int[] o2) {
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            return 1;
        } else if (o2 == null) {
            return -1;
        } else {
            return ((Integer) o1.length).compareTo(o2.length);
        }
    }

}

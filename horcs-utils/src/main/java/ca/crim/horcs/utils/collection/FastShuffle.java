package ca.crim.horcs.utils.collection;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Utility for random processing
 * 
 * @author bondst
 * 
 */
public class FastShuffle {

    private static Random r = new Random();

    public static void setSeed(long seed) {
        r = new Random(seed);
    }

    public static double random() {
        return r.nextDouble();
    }

    public static void shuffle(List<?> list) {
        Collections.shuffle(list, r);
    }
    
    public static <T> void shuffle(T[] arr, int fromIndex, int toIndex) {
        int a, b;
        T tmp;
        int size = toIndex - fromIndex;
        for (int i = size; i > 1; i--) {
            a = i - 1;
            b = r.nextInt(i);
            tmp = arr[fromIndex + a];
            arr[fromIndex + a] = arr[fromIndex + b];
            arr[fromIndex + b] = tmp;
        }
    }

    public static <T> void shuffle(T[] arr) {
        int a, b;
        T tmp;
        int size = arr.length;
        for (int i = size; i > 1; i--) {
            a = i - 1;
            b = r.nextInt(i);
            tmp = arr[a];
            arr[a] = arr[b];
            arr[b] = tmp;
        }
    }

    public static void shuffle(int[] arr) {
        int a, b, tmp;
        int size = arr.length;
        for (int i = size; i > 1; i--) {
            a = i - 1;
            b = r.nextInt(i);
            tmp = arr[a];
            arr[a] = arr[b];
            arr[b] = tmp;
        }
    }
}


package ca.crim.horcs.utils.collection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class ArrayHelper {

    /**
     * Concat unique sur le tableau
     * 
     * @param t1
     * @param t2
     * @return un nouveau tableau
     */
    public static int[] concatSortedUnique(int[]... tabs) {
        int[] tab = concat(tabs);
        if (tab == null || tab.length <= 1) {
            return tab;
        }

        // Élimination des doublons
        Arrays.sort(tab);
        int[] result = new int[tab.length];
        int idx = 0;
        result[idx++] = tab[0];
        for (int i = 1; i < tab.length; i++) {
            if (tab[i] != tab[i - 1]) {
                result[idx++] = tab[i];
            }
        }

        int[] newResult = new int[idx];
        System.arraycopy(result, 0, newResult, 0, idx);
        return newResult;
    }

    /**
     * Concaténation de tableaux de int
     * 
     * @param t1
     * @param t2
     * @return un nouveau tableau
     */
    public static int[] concat(int[]... tabs) {
        int newSize = 0;
        for (int[] tab : tabs) {
            if (tab != null) {
                newSize += tab.length;
            }
        }
        int[] newTab = new int[newSize];
        int idx = 0;
        for (int[] tab : tabs) {
            if (tab != null) {
                for (int val : tab) {
                    newTab[idx++] = val;
                }
            }
        }
        return newTab;
    }

    /**
     * Concaténation de tableaux de float
     * 
     * @param t1
     * @param t2
     * @return un nouveau tableau
     */
    public static float[] concat(float[]... tabs) {
        int newSize = 0;
        for (float[] tab : tabs) {
            if (tab != null) {
                newSize += tab.length;
            }
        }
        float[] newTab = new float[newSize];
        int idx = 0;
        for (float[] tab : tabs) {
            if (tab != null) {
                for (float val : tab) {
                    newTab[idx++] = val;
                }
            }
        }
        return newTab;
    }

    /**
     * Concaténation de tableaux d'objets
     * 
     * @param t1
     * @param t2
     * @return un nouveau tableau
     */
    public static int[] concat(int[] tab, int newObj) {
        if (tab == null) {
            return new int[] { newObj };
        } else {
            int[] newTab = (int[]) Array.newInstance(int.class, tab.length + 1);
            System.arraycopy(tab, 0, newTab, 0, tab.length);
            newTab[newTab.length - 1] = newObj;
            return newTab;
        }
    }

    /**
     * Concaténation de tableaux d'objets
     * 
     * @param t1
     * @param t2
     * @return un nouveau tableau
     */
    public static long[] concat(long[] tab, long newObj) {
        if (tab == null) {
            return new long[] { newObj };
        } else {
            long[] newTab = (long[]) Array.newInstance(long.class, tab.length + 1);
            System.arraycopy(tab, 0, newTab, 0, tab.length);
            newTab[newTab.length - 1] = newObj;
            return newTab;
        }
    }

    /**
     * Concaténation de tableaux d'objets
     * 
     * @param t1
     * @param t2
     * @return un nouveau tableau
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] concat(T[] tab1, T[] tab2) {
        if (tab1 == null) {
            return tab2;
        }
        if (tab2 == null) {
            return tab1;
        }
        T[] newTab = (T[]) Array.newInstance(tab1.getClass().getComponentType(), tab1.length + tab2.length);
        int idx = 0;
        if (tab1 != null) {
            for (T val : tab1) {
                newTab[idx++] = val;
            }
        }
        if (tab2 != null) {
            for (T val : tab2) {
                newTab[idx++] = val;
            }
        }
        return newTab;
    }

    /**
     * Concaténation de tableaux d'objets
     * 
     * @param t1
     * @param t2
     * @return un nouveau tableau
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] concat(T[] tab, T newObj) {
        T[] newTab;
        if (newObj == null) {
            newTab = tab;
        } else if (tab == null) {
            newTab = (T[]) Array.newInstance(newObj.getClass(), 1);
            newTab[0] = newObj;
        } else {
            newTab = (T[]) Array.newInstance(tab.getClass().getComponentType(), tab.length + 1);
            System.arraycopy(tab, 0, newTab, 0, tab.length);
            newTab[newTab.length - 1] = newObj;
        }
        return newTab;
    }

    public static <T> T[] concatUnique(T[] tab, T newObj) {
        boolean exists = false;
        if (tab != null) {
            for (int i = 0; i < tab.length; i++) {
                if (tab[i] == newObj) {
                    exists = true;
                }
            }
        }
        if (!exists) {
            return concat(tab, newObj);
        } else {
            return tab;
        }
    }

    public static int[] concatUnique(int[] tab, int newObj) {
        boolean exists = false;
        if (tab != null) {
            for (int i = 0; i < tab.length; i++) {
                if (tab[i] == newObj) {
                    exists = true;
                }
            }
        }
        if (!exists) {
            return concat(tab, newObj);
        } else {
            return tab;
        }
    }

    public static long[] concatUnique(long[] tab, long newObj) {
        boolean exists = false;
        if (tab != null) {
            for (int i = 0; i < tab.length; i++) {
                if (tab[i] == newObj) {
                    exists = true;
                }
            }
        }
        if (!exists) {
            return concat(tab, newObj);
        } else {
            return tab;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] concatUnique(T[] tab1, T[] tab2) {
        if (tab1 == null && tab2 == null) {
            return (T[]) null;
        } else if (tab1 == null) {
            tab1 = (T[]) Array.newInstance(tab2.getClass().getComponentType(), 0);
        } else if (tab2 == null) {
            tab2 = (T[]) Array.newInstance(tab1.getClass().getComponentType(), 0);
        }

        T[] large = tab1.length > tab2.length ? tab1 : tab2;
        T[] small = large == tab1 ? tab2 : tab1;
        T[] addToLarge = (T[]) Array.newInstance(small.getClass().getComponentType(), small.length);
        int addToLargeIdx = 0;
        for (int x = 0; x < small.length; x++) {
            boolean found = false;
            for (int y = 0; !found && y < large.length; y++) {
                if (large[y] == small[x]) {
                    found = true;
                }
            }
            if (!found) {
                addToLarge[addToLargeIdx++] = small[x];
            }
        }
        if (addToLargeIdx == 0) {
            return large;
        } else {
            T[] newLarge = (T[]) Array.newInstance(small.getClass().getComponentType(), large.length + addToLargeIdx);
            System.arraycopy(large, 0, newLarge, 0, large.length);
            for (int x = 0; x < addToLargeIdx; x++) {
                newLarge[large.length + x] = addToLarge[x];
            }
            return newLarge;
        }
    }

    public static int[] concatUnique(int[] tab1, int[] tab2) {
        if (tab1 == null) {
            return tab2;
        } else if (tab2 == null) {
            return tab1;
        } else {
            int[] large = tab1.length > tab2.length ? tab1 : tab2;
            int[] small = large == tab1 ? tab2 : tab1;
            int[] addToLarge = new int[small.length];
            int addToLargeIdx = 0;
            for (int x = 0; x < small.length; x++) {
                boolean found = false;
                for (int y = 0; !found && y < large.length; y++) {
                    if (large[y] == small[x]) {
                        found = true;
                    }
                }
                if (!found) {
                    addToLarge[addToLargeIdx++] = small[x];
                }
            }
            if (addToLargeIdx == 0) {
                return large;
            } else {
                int[] newLarge = new int[large.length + addToLargeIdx];
                System.arraycopy(large, 0, newLarge, 0, large.length);
                for (int x = 0; x < addToLargeIdx; x++) {
                    newLarge[large.length + x] = addToLarge[x];
                }
                return newLarge;
            }
        }
    }

    /**
     * Segmentation d'un tableau
     */
    @SuppressWarnings("unchecked")
    public static <T> T[][] segmentArray(T[] array, int maxLength) {
        int nbParts = (int) Math.ceil((float) array.length / maxLength);
        T[][] result = (T[][]) Array.newInstance(array.getClass().getComponentType(), new int[] { nbParts, 0 });
        if (nbParts == 1) {
            result[0] = array;
        } else {
            for (int p = 0; p < nbParts; p++) {
                int start = p * maxLength;
                int end;
                if (p == nbParts - 1) {
                    end = start + (array.length - start);
                } else {
                    end = start + maxLength;
                }
                T[] subResult = (T[]) Array.newInstance(array.getClass().getComponentType(), end - start);
                System.arraycopy(array, start, subResult, 0, subResult.length);
                result[p] = subResult;
            }
        }
        return result;
    }

    /**
     * Place les nbToMove plus petits items, situés entre fromIndex et toIndex,
     * à la position fromIndex
     * 
     * @param array
     * @param fromIndex
     * @param toIndex
     * @param nbToMove
     */
    @SuppressWarnings( { "rawtypes", "unchecked" })
    public static void reorderMinItems(Comparable[] array, int fromIndex, int toIndex, int nbToMove) {
        if (toIndex < fromIndex + nbToMove) {
            nbToMove = toIndex - fromIndex;
        }
        int[] smallests = new int[nbToMove];
        int sMaxIdx = 0;
        smallests[sMaxIdx] = fromIndex;
        // Trouve les nbToMove plus petits éléments, placés dans un tableau
        // ordonné
        for (int i = fromIndex + 1; i < toIndex; i++) {
            // Trouve la position dans le tableau
            int pos = -1;
            for (int j = sMaxIdx; j >= 0 && array[smallests[j]].compareTo(array[i]) > 0; j--) {
                pos = j;
            }
            if (sMaxIdx < nbToMove - 1) {
                sMaxIdx++;
                if (pos == -1) {
                    pos = sMaxIdx;
                }
            }
            // Insertion dans le tableau
            if (pos >= 0) {
                for (int j = sMaxIdx; j >= pos + 1; j--) {
                    smallests[j] = smallests[j - 1];
                }
                smallests[pos] = i;
            }
        }
        Comparable[] tmpInserts = (Comparable[]) Array.newInstance(array.getClass().getComponentType(), nbToMove);
        for (int i = 0; i < nbToMove; i++) {
            tmpInserts[i] = array[smallests[i]];
            array[smallests[i]] = null;
        }
        int idx = toIndex - 1;
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (array[i] != null) {
                if (i != idx) {
                    array[idx] = array[i];
                    array[i] = null;
                }
                idx--;
            }
        }
        for (int i = 0; i < nbToMove; i++) {
            array[i + fromIndex] = tmpInserts[i];
        }
    }

    /**
     * Retourne les valeurs partagés des deux tableaux
     * 
     * @param pers1
     * @param pers2
     * @return
     */
    public static int[] sharedValues(int[] ints1, int[] ints2) {
        if (ints1 == null || ints2 == null) {
            return new int[0];
        }
        int[] persMax = ints1.length > ints2.length ? ints1 : ints2;
        int[] persMin = persMax == ints1 ? ints2 : ints1;
        int[] work = new int[persMin.length];
        int idx = 0;
        for (int p1 : persMin) {
            boolean contains = false;
            for (int p2 : persMax) {
                if (p1 == p2) {
                    contains = true;
                    break;
                }
            }
            if (contains) {
                work[idx++] = p1;
            }
        }
        int[] result = new int[idx];
        if (idx > 0) {
            System.arraycopy(work, 0, result, 0, idx);
        }
        return result;
    }

    /**
     * Retourne true si les 2 tableaux partagent au moins 1 valeur
     * 
     * @param pers1
     * @param pers2
     * @return
     */
    public static boolean hasSharedValues(int[] ints1, int[] ints2) {
        if (ints1 == null || ints2 == null) {
            return false;
        }
        int[] persMax = ints1.length > ints2.length ? ints1 : ints2;
        int[] persMin = persMax == ints1 ? ints2 : ints1;
        for (int i1 = 0; i1 < persMin.length; i1++) {
            int p1 = persMin[i1];
            for (int i2 = 0; i2 < persMax.length; i2++) {
                int p2 = persMax[i2];
                if (p1 == p2) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retourne true si les 2 tableaux partagent au moins 1 valeur
     * 
     * @param pers1
     * @param pers2
     * @return
     */
    public static <T> boolean hasSharedValues(T[] t1, T[] t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        T[] persMax = t1.length > t2.length ? t1 : t2;
        T[] persMin = persMax == t1 ? t2 : t1;
        for (int i1 = 0; i1 < persMin.length; i1++) {
            T p1 = persMin[i1];
            for (int i2 = 0; i2 < persMax.length; i2++) {
                T p2 = persMax[i2];
                if (p1.equals(p2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retourne les périodes en commun entre les 2 tableaux
     */
    public static int[] sharedValues(boolean[] persHor, int[] pers) {
        int[] work = new int[pers.length];
        int idx = 0;
        for (int p : pers) {
            if (persHor[p]) {
                work[idx++] = p;
            }
        }
        int[] result = new int[idx];
        if (idx > 0) {
            System.arraycopy(work, 0, result, 0, idx);
        }
        return result;
    }

    public static boolean hasSharedValues(boolean[] persHor, int[] pers) {
        for (int p : pers) {
            if (persHor[p]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Conversion d'un tableau de périodes vers un tableau représentant
     * l'horaire
     * 
     * @param periodes
     * @return
     */
    public static boolean[] intsToBooleans(int[] ints, int size) {
        boolean[] result = new boolean[size];
        // Création du tableau de périodes
        if (ints != null) {
            for (int periode : ints) {
                result[periode] = true;
            }
        }
        return result;
    }

    /**
     * Conversion d'un tableau de boolean vers un tableau d'entiers contenant
     * les indices des périodes
     * 
     * @param horaire
     * @return
     */
    public static int[] booleansToInts(boolean[] bools) {
        int nbPeriodes = 0;
        for (boolean p : bools) {
            if (p) {
                nbPeriodes++;
            }
        }
        int[] result = new int[nbPeriodes];
        int idx = 0;
        for (int p = 0; p < bools.length; p++) {
            if (bools[p]) {
                result[idx++] = p;
            }
        }
        return result;
    }

    /**
     * Division des éléments de la liste source avec, pour chaque liste, le
     * nombre d'éléments spécifiés dans destSizes
     * 
     * @param source
     * @param destSizes
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T[][] splitSource(T[][] source, int[] destSizes) {
        source = source.clone();
        T[][] result = (T[][]) Array.newInstance(source.getClass().getComponentType(), destSizes.length);
        // 1. Matchs sans modifs
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < destSizes.length && source[i] != null; j++) {
                if (result[j] == null && source[i].length == destSizes[j]) {
                    result[j] = source[i];
                    source[i] = null;
                }
            }
        }
        // 2. Match des ensembles restants, en ordre de grandeur
        boolean complete = false;
        while (!complete) {
            int sourceId = -1;
            for (int i = 0; i < source.length; i++) {
                if (source[i] != null && (sourceId == -1 || source[i].length > source[sourceId].length)) {
                    sourceId = i;
                }
            }
            int destId = -1;
            for (int i = 0; i < destSizes.length; i++) {
                if (result[i] == null && (destId == -1 || destSizes[i] > destSizes[destId])) {
                    destId = i;
                }
            }
            if (sourceId != -1 && destId != -1) {
                result[destId] = source[sourceId];
                source[sourceId] = null;
            } else {
                complete = true;
            }
        }

        // 3. Complète les ensembles de destination non utilisés en séparant des
        // ensembles plus grands que souhaité
        complete = false;
        while (!complete) {
            int destId = -1;
            for (int i = 0; destId == -1 && i < destSizes.length; i++) {
                if (result[i] == null) {
                    destId = i;
                }
            }
            if (destId != -1) {
                int resultId = -1;
                for (int i = 0; i < result.length; i++) {
                    if (result[i] != null
                            && result[i].length > 1
                            && (resultId == -1 || result[i].length - destSizes[i] > result[resultId].length
                                    - destSizes[resultId])) {
                        resultId = i;
                    }
                }
                if (resultId != -1) {
                    int copySize = 1;
                    if (result[resultId].length > destSizes[resultId]) {
                        copySize = result[resultId].length - destSizes[resultId];
                    }
                    result[destId] = Arrays.copyOfRange(result[resultId], result[resultId].length - copySize,
                            result[resultId].length);
                    result[resultId] = Arrays.copyOfRange(result[resultId], 0, result[resultId].length - copySize);
                } else {
                    complete = true;
                }
            } else {
                complete = true;
            }
        }
        return result;
    }

    public static int[] toPrimitives(Collection<Integer> values) {
        int[] result = new int[values.size()];
        int idx = 0;
        for (Integer v : values) {
            result[idx++] = v;
        }
        return result;
    }

    /**
     * Compte le nombre de valeurs communes entre les 2 valeurs
     * 
     * @param <T>
     * @param t1
     * @param t2
     * @return
     */
    public static <T> int intersectCount(T[] t1, T[] t2) {
        int result = 0;
        for (int i = 0; i < t1.length; i++) {
            for (int j = 0; j < t2.length; j++) {
                if (t1[i] == t2[j]) {
                    result++;
                    break;
                }
            }
        }
        return result;
    }

    public static int intersectCount(int[] t1, int[] t2) {
        int result = 0;
        for (int i = 0; i < t1.length; i++) {
            for (int j = 0; j < t2.length; j++) {
                if (t1[i] == t2[j]) {
                    result++;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Retrait d'un item au tableau. Peut retirer plusieurs occurences de ce
     * dernier s'il y a lieu
     * 
     * @param <T>
     * @param table
     * @param item
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] removeItem(T[] table, T item) {
        if (table == null) {
            return table;
        }
        for (int i = 0; i < table.length; i++) {
            if ((item != null && item.equals(table[i])) || item == table[i]) {
                T[] newTable = (T[]) Array.newInstance(table.getClass().getComponentType(), table.length - 1);
                System.arraycopy(table, 0, newTable, 0, i);
                System.arraycopy(table, i + 1, newTable, i, newTable.length - i);
                table = newTable;
                i--;
            }
        }
        return table;
    }

    /**
     * Retrait d'un entier au tableau. Peut retirer plusieurs occurences de ce
     * dernier s'il y a lieu
     * 
     * @param <T>
     * @param table
     * @param item
     * @return
     */
    public static int[] removeItem(int[] table, int item) {
        for (int i = 0; i < table.length; i++) {
            if (item == table[i]) {
                int[] newTable = new int[table.length - 1];
                System.arraycopy(table, 0, newTable, 0, i);
                System.arraycopy(table, i + 1, newTable, i, newTable.length - i);
                table = newTable;
                i--;
            }
        }
        return table;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] removeItemAt(T[] table, int pos) {
        T[] newTable = (T[]) Array.newInstance(table.getClass().getComponentType(), table.length - 1);
        int idx = 0;
        for (int i = 0; i < table.length; i++) {
            if (i != pos) {
                newTable[idx++] = table[i];
            }
        }
        return newTable;
    }

    /**
     * Est-ce que items contient subItems
     * 
     * @param ids
     * @param ids2
     * @return
     */
    public static boolean containAll(int[] items, int[] subItems) {
        if (items == null && subItems != null) {
            return false;
        } else if (items != null && subItems == null) {
            return true;
        }
        for (int i = 0; i < subItems.length; i++) {
            boolean contain = false;
            for (int j = 0; j < items.length; j++) {
                if (subItems[i] == items[j]) {
                    contain = true;
                }
            }
            if (!contain) {
                return false;
            }
        }
        return true;
    }

    /**
     * Est-ce que items contient subItems
     * 
     * @param ids
     * @param ids2
     * @return
     */
    public static boolean containAll(Object[] items, Object[] subItems) {
        for (int i = 0; i < subItems.length; i++) {
            boolean contain = false;
            for (int j = 0; j < items.length; j++) {
                if (subItems[i].equals(items[j])) {
                    contain = true;
                }
            }
            if (!contain) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrait des SubItems
     * 
     * @param items
     * @param subItems
     * @return
     */
    public static int[] remSubItems(int[] items, int[] subItems) {
        int[] result = new int[items.length - subItems.length];
        if (result.length > 0) {
            int idx = 0;
            for (int i = 0; i < items.length; i++) {
                boolean found = false;
                for (int j = 0; !found && j < subItems.length; j++) {
                    if (items[i] == subItems[j]) {
                        found = true;
                    }
                }
                if (!found) {
                    result[idx++] = items[i];
                }
            }
        }
        return result;
    }

    /**
     * Aucune assertion nécessaire; l'algorithme est moins efficace, O(mn), mais
     * fonctionne toujours, même avec doublons et tableaux non triés.
     * 
     * @param items1
     * @param items2
     * @return
     */
    public static int[] removeIntersectFromFisrt(int[] items1, int[] items2) {
        if (items2 == null) {
            return items1;
        }
        if (items1 == null) {
            return null;
        }
        int[] result = new int[items1.length];
        int size = 0;
        for (int i = 0; i < items1.length; i++) {
            boolean found = false;
            for (int j = 0; j < items2.length; j++) {
                found |= items1[i] == items2[j];
            }
            if (!found) {
                result[size] = items1[i];
                ++size;
            }
        }
        if (size > 0) {
            int[] sizedResult = new int[size];
            System.arraycopy(result, 0, sizedResult, 0, size);
            return sizedResult;
        } else {
            return null;
        }
    }

    /**
     * Retourne true si tab contient value
     * 
     * @param brokenAgenIds
     * @param agenId
     * @return
     */
    public static boolean contains(int[] tab, int value) {
        if (tab == null) {
            return false;
        } else {
            for (int i = 0; i < tab.length; i++) {
                if (tab[i] == value) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Retourne true si tab contient value
     * 
     * @param brokenAgenIds
     * @param agenId
     * @return
     */
    public static <T> boolean contains(T[] tab, T value) {
        if (tab == null) {
            return false;
        } else {
            for (int i = 0; i < tab.length; i++) {
                if (tab[i] == value) {
                    return true;
                } else if (tab[i] != null && value != null && tab[i].equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Retourne la position de la valeur maximale
     * 
     * @param tab
     * @return
     */
    public static int getIdxMaxValue(int[] tab) {
        int maxIdx = -1;
        int maxValue = -1;
        for (int idx = 0; idx < tab.length; idx++) {
            if (tab[idx] > maxValue) {
                maxIdx = idx;
                maxValue = tab[idx];
            }
        }
        return maxIdx;
    }

    /**
     * Liste les éléments qui se retrouvent dans une des liste uniquement
     * 
     * Les 2 tableaux doivent êtres triés
     * 
     * @return
     */
    public static int[] findDifference(int[] tab1, int[] tab2) {
        int[] temp = new int[tab1.length + tab2.length];
        int tempIdx = 0;

        int idx1 = 0;
        int idx2 = 0;
        while (idx1 < tab1.length || idx2 < tab2.length) {
            if (idx1 >= tab1.length) {
                temp[tempIdx++] = tab2[idx2];
                idx2++;
            } else if (idx2 >= tab2.length) {
                temp[tempIdx++] = tab1[idx1];
                idx1++;
            } else if (tab1[idx1] > tab2[idx2]) {
                temp[tempIdx++] = tab2[idx2];
                idx2++;
            } else if (tab1[idx1] < tab2[idx2]) {
                temp[tempIdx++] = tab1[idx1];
                idx1++;
            } else {
                idx1++;
                idx2++;
            }
        }
        if (tempIdx >= 0) {
            return Arrays.copyOf(temp, tempIdx);
        } else {
            return null;
        }
    }

    public static Comparator<int[]> getIntLexicographicComparator() {
        /**
         * Comparaison de tableaux d'entiers selon l'ordre lexicographique
         * 
         * @param o1
         * @param o2
         * @return
         */
        return new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if ((o1 == null || o1.length == 0) && (o2 == null || o2.length == 0)) {
                    return 0;
                } else if ((o1 == null || o1.length == 0) && (o2 != null && o2.length > 0)) {
                    return 1;
                } else if ((o1 != null && o1.length > 0) && (o2 == null || o2.length == 0)) {
                    return -1;
                } else {
                    int[] arr1 = o1;
                    int[] arr2 = o2;
                    Arrays.sort(arr1);
                    Arrays.sort(arr2);
                    int len;
                    boolean diffLen;
                    if (arr1.length != arr2.length) {
                        len = (arr1.length < arr2.length ? arr1.length : arr2.length);
                        diffLen = true;
                    } else {
                        len = arr1.length;
                        diffLen = false;
                    }
                    int j = -1;
                    while (++j < len) {
                        if (arr1[j] < arr2[j]) {
                            return -1;
                        } else if (arr1[j] > arr2[j]) {
                            return 1;
                        }
                    }
                    if (diffLen) {
                        return (arr1.length < arr2.length ? -1 : 1);
                    }
                    return 0;
                }
            }
        };

    }

    public static void reverse(int[] b) {
        int left = 0; // index of leftmost element
        int right = b.length - 1; // index of rightmost element

        while (left < right) {
            // exchange the left and right elements
            int temp = b[left];
            b[left] = b[right];
            b[right] = temp;

            // move the bounds toward the center
            left++;
            right--;
        }
    }

    public static Integer[] wrapInts(int[] t) {
        Integer[] res = new Integer[t.length];
        for (int i = 0; i < t.length; i++) {
            res[i] = t[i];
        }
        return res;
    }

}

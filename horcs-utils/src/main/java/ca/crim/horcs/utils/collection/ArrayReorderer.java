package ca.crim.horcs.utils.collection;


/**
 * Outils pour le réordonnancement de tableaux
 * 
 * Utilisé lorsque plusieurs tableaux doivent conserver des éléments dans un
 * même ordre
 * 
 * @author bondst
 * 
 */
public class ArrayReorderer {

    /**
     * Création d'une table de correspondance aléatoire
     * 
     * @param size
     * @return
     */
    public static int[] rndTable(int size) {
        int[] table = new int[size];
        for (int i = 0; i < table.length; i++) {
            table[i] = i;
        }
        FastShuffle.shuffle(table);
        return table;
    }

    /**
     * Application de la table de correspondance au tableau
     * 
     * @param tab
     * @param tc
     */
    public static <T> void reorder(T[] tab, int[] tc) {
        T[] backTable = tab.clone();
        for (int i = 0; i < tc.length; i++) {
            tab[tc[i]] = backTable[i];
        }
    }

    /**
     * Application de la table de correspondance au tableau
     * 
     * @param tab
     * @param tc
     */
    public static void reorder(int[] tab, int[] tc) {
        int[] backTable = tab.clone();
        for (int i = 0; i < tc.length; i++) {
            tab[tc[i]] = backTable[i];
        }
    }

    /**
     * Application de la correspondance aux ids du tableau
     * 
     * @param idxTable
     * @param tc
     */
    public static void applyMapping(int[] idxTable, int[] tc) {
        for (int i = 0; i < idxTable.length; i++) {
            idxTable[i] = tc[idxTable[i]];
        }
    }

}

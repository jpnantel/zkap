package ca.crim.horcs.utils.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnsembleUtils {

    /**
     * Détermine si les 2 collections sont égales
     */
    public static <T> boolean equals(Collection<T> c1, Collection<T> c2) {
        if (c1.size() == c2.size()) {
            // c1.size() == c2.size() && containAll(c1, c2) && Collection (pas
            // nécessairement un Set)
            // c1: 1, 2, 3, 4, 5 && c2 :1, 1, 2, 3, 4 ---> alors Faux positif ->
            // vrai si un set et interdit doublons
            return containAll(c1, c2) && containAll(c2, c1);
        } else {
            return false;
        }
    }

    /**
     * Détermine si deux ensembles sont égaux
     */
    public static <T> boolean equals(Set<T> c1, Set<T> c2) {
        if (c1.size() == c2.size()) {
            return containAll(c1, c2);
        } else {
            return false;
        }
    }

    /**
     * Tri d'un map sur les valeurs
     * 
     * @param <K>
     * @param <V>
     * @param map
     * @return
     */
    public static <K, V extends Comparable<V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext();) {
            Map.Entry<K, V> entry = it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Compte le nombre de différences entre les deux collections
     * 
     * @param <T>
     * @param c1
     * @param c2
     * @return
     */
    public static <T> int getNbDifferents(Collection<T> c1, Collection<T> c2) {
        int nbSame = 0;
        for (T o1 : c1) {
            for (T o2 : c2) {
                if (o1.equals(o2)) {
                    nbSame++;
                }
            }
        }
        if (nbSame == c1.size() && nbSame == c2.size()) {
            return 0;
        } else {
            return (c1.size() >= c2.size() ? c1.size() - nbSame : c2.size() - nbSame);
        }
    }

    /**
     * Fusionne les ensembles qui se touchent
     * 
     * Version avec BitSets
     * 
     * @param <T>
     * @param ensList
     */
    public static <R> void fusion(List<Set<R>> ensList) {
        // R -> Bit

        // Ensemble de travail
        List<BitSet> workingList = new ArrayList<BitSet>();

        // Création de uBit : le id de bit par objet T
        Map<R, Integer> rBit = new HashMap<R, Integer>();
        List<R> rBitById = new ArrayList<R>();
        int nbits = 0;
        for (Set<R> set : ensList) {
            BitSet bSet = new BitSet();
            for (R r : set) {
                int rId;
                if (!rBit.containsKey(r)) {
                    rId = nbits++;
                    rBit.put(r, rId);
                    rBitById.add(r);
                } else {
                    rId = rBit.get(r);
                }
                bSet.set(rId);
            }
            workingList.add(bSet);
        }

        // Fusion des ensembles
        int length = -1;
        while (workingList.size() != length) {
            length = workingList.size();
            for (int i = 0; i < workingList.size(); i++) {
                for (int j = i + 1; j < workingList.size(); j++) {
                    BitSet workSet = (BitSet) workingList.get(i).clone();
                    workSet.and(workingList.get(j));
                    if (!workSet.isEmpty()) {
                        workingList.get(i).or(workingList.get(j));
                        workingList.remove(j);
                    }
                }
            }
        }

        // Compilation des résultats
        ensList.clear();
        for (BitSet bSet : workingList) {
            Set<R> rSet = new HashSet<R>();
            for (int i = 0; i < nbits; i++) {
                if (bSet.get(i)) {
                    rSet.add(rBitById.get(i));
                }
            }
            ensList.add(rSet);
        }
    }

    /**
     * Fusion avec tolerence
     * 
     * Besoin : spécifier un poids sur chaque ensemble (<ensemble, poids> dans
     * ensList)
     * 
     * @param <R>
     * @param ensList
     * @param tolerence
     * @return
     */
    public static <R> List<Set<R>> fusion(Map<Set<R>, Integer> ensList, int tolerence) {
        // R -> Bit

        // Création de uBit : le id de bit par objet T
        Map<R, Integer> rBit = new HashMap<R, Integer>();

        Map<int[], Integer> ensRBits = new HashMap<int[], Integer>();
        List<R> rBitById = new ArrayList<R>();
        int nbits = 0;
        for (Set<R> set : ensList.keySet()) {
            int[] setRBits = new int[set.size()];
            int idx = 0;
            for (R r : set) {
                int rId;
                if (!rBit.containsKey(r)) {
                    rId = nbits++;
                    rBit.put(r, rId);
                    rBitById.add(r);
                } else {
                    rId = rBit.get(r);
                }
                setRBits[idx++] = rId;
            }
            ensRBits.put(setRBits, ensList.get(set));
        }

        // Remplis le workTable
        int[] workMax = new int[nbits];
        int[][] workTable = (int[][]) Array.newInstance(int.class, new int[] { nbits, nbits });
        for (int[] setRBits : ensRBits.keySet()) {
            int weight = ensRBits.get(setRBits);
            for (int i = 0; i < setRBits.length; i++) {
                for (int j = i + 1; j < setRBits.length; j++) {
                    int r1 = setRBits[i];
                    int r2 = setRBits[j];
                    int value = workTable[r1][r2] + weight;
                    workTable[r1][r2] = value;
                    workTable[r2][r1] = value;
                    if (value > workMax[r1]) {
                        workMax[r1] = value;
                    }
                    if (value > workMax[r2]) {
                        workMax[r2] = value;
                    }
                }
            }
        }

        // Création de nouveaux ensembles en fonction des workTables
        List<BitSet> workingList = new ArrayList<BitSet>();
        // Gestion de la tolerence : retrait des valeurs < tolerence sauf si
        // workMax < tolerence, on conserve ceux qui sont = à workMax
        for (int i = 0; i < nbits; i++) {
            BitSet iBitSet = new BitSet();
            iBitSet.set(i);
            for (int j = 0; j < nbits; j++) {
                int value = tolerence;
                if (value >= tolerence || value == workMax[i] || value == workMax[j]) {
                    if (workTable[i][j] >= value) {
                        iBitSet.set(j);
                    }
                }
            }
            workingList.add(iBitSet);
        }

        // Fusion des ensembles
        int length = -1;
        while (workingList.size() != length) {
            length = workingList.size();
            for (int i = 0; i < workingList.size(); i++) {
                for (int j = i + 1; j < workingList.size(); j++) {
                    BitSet workSet = (BitSet) workingList.get(i).clone();
                    workSet.and(workingList.get(j));
                    if (!workSet.isEmpty()) {
                        workingList.get(i).or(workingList.get(j));
                        workingList.remove(j);
                    }
                }
            }
        }

        // Compilation des résultats
        List<Set<R>> result = new ArrayList<Set<R>>();
        ensList.clear();
        for (BitSet bSet : workingList) {
            Set<R> rSet = new HashSet<R>();
            for (int i = 0; i < nbits; i++) {
                if (bSet.get(i)) {
                    rSet.add(rBitById.get(i));
                }
            }
            result.add(rSet);
        }
        return result;
    }

    /**
     * Détermine si parentSet contient tous les éléments de childSet
     * 
     * @param <T>
     * @param parentSet
     * @param childSet
     * @return
     */
    public static boolean containAll(Collection<?> parentSet, Collection<?> childSet) {
        for (Object item : childSet) {
            if (!parentSet.contains(item)) {
                return false;
            }
        }
        return true;
    }

    public static <T> Set<T> union(Set<T> set1, Collection<T> set2) {
        Set<T> union = new HashSet<T>(set2);
        union.addAll(set1);
        return union;
    }

    public static <T> Set<T> union(Collection<T> set1, Collection<T> set2) {
        Set<T> union = new HashSet<T>(set1);
        union.addAll(new HashSet<T>(set2));
        return union;
    }

    /**
     * Détermine si set1 et set2 ont des éléments en commun
     * 
     * @param <T>
     * @param set1
     * @param set2
     * @return
     */
    public static boolean hasIntersection(Collection<?> set1, Collection<?> set2) {
        Collection<?> small, large;
        if (set1 == null || set2 == null) {
            return false;
        }
        if (set1.size() < set2.size()) {
            small = set1;
            large = set2;
        } else {
            small = set2;
            large = set1;
        }
        for (Object item : small) {
            if (large.contains(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Conserve uniquement les éléments présents dans les deux ensembles
     * 
     * @param <T>
     * @param set1
     * @param set2
     */
    public static <T> Set<T> intersection(Set<T> set1, Collection<T> set2) {
        Set<T> sum = new HashSet<T>();
        for (T item : set2) {
            if (set1.contains(item)) {
                sum.add(item);
            }
        }
        return sum;
    }

    /**
     * Trouve les élément communs de set1 et set2
     * 
     * @param <T>
     * @param c1
     * @param c2
     * @return
     */
    public static <T> List<T> intersection(Collection<T> c1, Collection<T> c2) {
        Collection<T> small, large;
        if (c1.size() < c2.size()) {
            small = c1;
            large = c2;
        } else {
            small = c2;
            large = c1;
        }
        List<T> temp = new ArrayList<T>();
        for (T item : small) {
            if (large.contains(item)) {
                temp.add(item);
            }
        }
        return temp;
    }

    /**
     * Trouve les élément communs de set1 et set2
     * 
     * @param <T>
     * @param set1
     * @param set2
     * @return
     */
    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> intersection = new HashSet<T>(set1);
        intersection.retainAll(set2);
        return intersection;
    }

    /**
     * Retrait des collections dont le contenu est dupliqué
     * 
     * @param <T>
     * @param collections
     */
    public static void removeDuplicates(Collection<? extends Collection<?>> collections) {
        List<Collection<?>> collToRemove = new ArrayList<Collection<?>>();
        Collection<?>[] tabCollections = collections.toArray(new Collection[0]);
        for (int i = 0; i < tabCollections.length; i++) {
            for (int j = i + 1; j < tabCollections.length; j++) {
                boolean equals = tabCollections[i].size() == tabCollections[j].size();
                if (equals) {
                    for (Object o : tabCollections[i]) {
                        if (!tabCollections[j].contains(o)) {
                            equals = false;
                        }
                    }
                }
                if (equals) {
                    collToRemove.add(tabCollections[j]);
                }
            }
        }
        for (Collection<?> c : collToRemove) {
            collections.remove(c);
        }
    }

    /**
     * Décomposition du contenu en nouveaux sets
     * 
     * Règles pour chaque nouveaux set / nouveaux ensembles
     * 
     * 1. Doivent se retrouver toujours ensemble dans tous les ensembles
     * originaux
     * 
     * @param remTaches
     * @return
     */
    public static <T> List<Set<T>> getAtomicSubSets(List<Set<T>> contentList) {
        Set<T> allItems = new HashSet<T>();
        for (Set<T> s : contentList) {
            allItems.addAll(s);
        }
        // Pour chaque item du contenu, trouver avec lesquels il est toujours
        // accompagné
        Set<T> trace = new HashSet<T>();
        List<Set<T>> result = new ArrayList<Set<T>>();
        for (T item : allItems) {
            if (!trace.contains(item)) {
                // Trouve un premier workSet
                Set<T> workSet = null;
                for (Set<T> set : contentList) {
                    if (set.contains(item)) {
                        if (workSet == null) {
                            workSet = new HashSet<T>(set);
                            break;
                        }
                    }
                }
                // Pour chaque item X du workSet, retirer tous les autres items
                // qu'on
                // retrouve dans les sets de contentList qui ne contiennent pas
                // X
                for (Object wItem : workSet.toArray()) {
                    if (workSet.contains(wItem)) {
                        for (Set<T> set : contentList) {
                            if (set.contains(item)) {
                                if (!set.contains(wItem)) {
                                    workSet.remove(wItem);
                                }
                            } else {
                                workSet.removeAll(set);
                            }
                        }
                    }
                }
                result.add(workSet);
                trace.addAll(workSet);
            }
        }
        return result;
    }

    /**
     * Calcul de clusters de type "maximum" : trouve le nombre maximum
     * d'occurence possibles pour chaque set de groupes
     * 
     * Ex: {A,A,A,AB,BC,C} => ABC->6, AB->5, A->4, B->2, C->2
     * 
     * @param <T>
     * @param contentList
     * @return
     */
    public static <T> Map<Set<T>, Integer> computeClusterMaxCombin(List<Set<T>> contentList) {
        Map<Set<T>, Integer> result = new HashMap<Set<T>, Integer>();

        // Clusters selon items partagés
        Set<T> allEns = new HashSet<T>();
        for (Set<T> ens : contentList) {
            allEns.addAll(ens);
            int nbEns = 0;
            for (Set<T> otherEns : contentList) {
                boolean hasJoint = false;
                for (T aItem : ens) {
                    if (otherEns.contains(aItem)) {
                        hasJoint = true;
                        break;
                    }
                }
                if (hasJoint) {
                    nbEns++;
                }
            }
            result.put(ens, nbEns);
        }

        // Chaque subset individuel

        for (Set<T> subset : getAtomicSubSets(contentList)) {
            int nbEns = 0;
            T anItem = subset.iterator().next();
            for (Set<T> otherEns : contentList) {
                if (otherEns.contains(anItem)) {
                    nbEns++;
                }
            }
            result.put(subset, nbEns);
        }

        // Cas de base, tous l'ensemble
        result.put(allEns, contentList.size());
        return result;
    }

    /**
     * Retourne le nombre d'éléments en commun entre set1 et set2
     * 
     * @param <T>
     * @param set1
     * @param set2
     * @return
     */
    public static <T> int sharedCount(Set<T> set1, Set<T> set2) {
        Set<T> smallSet = set1.size() < set2.size() ? set1 : set2;
        Set<T> bigSet = smallSet == set1 ? set2 : set1;
        int result = 0;
        for (T item : smallSet) {
            if (bigSet.contains(item)) {
                result++;
            }
        }
        return result;
    }

    /**
     * Concaténation de toutes les collections
     * 
     * @param <T>
     * @param cList
     * @return
     */
    public static <T> Collection<T> concat(Collection<T>... cList) {
        List<T> result = new ArrayList<T>();
        for (Collection<T> c : cList) {
            result.addAll(c);
        }
        return result;
    }

}

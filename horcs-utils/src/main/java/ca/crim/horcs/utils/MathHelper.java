package ca.crim.horcs.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathHelper {

    /**
     * Décompose le nombre de périodes en une série de nombres présents dans
     * "facteurs"
     * 
     * Choisis le minimum de combinaisons
     */
    public static int[] decomposeForBest(int nombre, int[] facteurs) {
        List<int[]> result = decomposeForList(nombre, facteurs);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    /**
     * Décompose le nombre de périodes en une série de nombres présents dans
     * "facteurs"
     * 
     * Doit retourner le minimum de combinaisons
     */
    public static List<int[]> decomposeForList(int nombre, int[] facteurs) {
        List<int[]> result = new ArrayList<int[]>();
        if (nombre == 0) {
            // Nothing
        } else {
            decomposeRec(nombre, facteurs, new int[nombre], 0, 0, result);
            // Tri par longueur pour donner priorité aux plus courts
            Collections.sort(result, new Comparator<int[]>() {
                public int compare(int[] arg0, int[] arg1) {
                    int thisVal = arg0.length;
                    int anotherVal = arg1.length;
                    return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
                }
            });
        }
        return result;
    }

    /**
     * Calcul de l'équart type sur un tableau de int
     * 
     * @param values
     * @return
     */
    public static float standardDeviation(int[] values) {
        int count = 0;
        int sum = 0;
        for (int v : values) {
            sum += v;
            count++;
        }
        float avg = ((float) sum) / count;
        float sumEqAvg = 0.0f;
        for (int v : values) {
            sumEqAvg += Math.abs(avg - v);
        }
        return sumEqAvg / count;
    }

    /**
     * Calcul de l'équart type sur un tableau de float
     * 
     * @param values
     * @return
     */
    public static float standardDeviation(float[] values) {
        int count = 0;
        float sum = 0;
        for (float v : values) {
            sum += v;
            count++;
        }
        float avg = ((float) sum) / count;
        float sumEqAvg = 0.0f;
        for (float v : values) {
            sumEqAvg += Math.abs(avg - v);
        }
        return sumEqAvg / count;
    }

    /**
     * Évaluation de l'uniformité de la répartition de valeurs
     * 
     * Critère : max "tolerence" d'équart entre le min et le max de l'ensemble
     * 
     * @param values
     * @return
     */
    public static boolean evalUniformity(int[] values, int tolerence) {
        if (values.length == 0) {
            return true;
        }
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int v : values) {
            if (v < min) {
                min = v;
            }
            if (v > max) {
                max = v;
            }
        }
        return (max - min <= tolerence);
    }

    /**
     * Retroune la différence maximale entre les valeurs
     * 
     * @param values
     * @return
     */
    public static int evalDiffUniformity(int[] values) {
        if (values.length == 0) {
            return 0;
        }
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int v : values) {
            if (v < min) {
                min = v;
            }
            if (v > max) {
                max = v;
            }
        }
        return (max - min);
    }

    /**
     * Partie récursive pour la décomposition d'un nombre en facteur (pour
     * addition)
     * 
     * @param nombre
     * @param facteurs
     * @param current
     * @param posCurrent
     * @param result
     */
    private static void decomposeRec(int nombre, int[] facteurs, int[] current, int posCurrent, int startPos,
            List<int[]> result) {
        if (nombre == 0) {
            int[] value = new int[posCurrent];
            for (int i = 0; i < value.length; i++) {
                value[i] = current[i];
            }
            result.add(value);
        } else {
            for (int i = startPos; i < facteurs.length; i++) {
                if (nombre >= facteurs[i]) {
                    current[posCurrent] = facteurs[i];
                    decomposeRec(nombre - facteurs[i], facteurs, current, posCurrent + 1, i, result);
                }
            }
        }
    }

    public static int sum(int[] ints) {
        int result = 0;
        for (int i = 0; i < ints.length; i++) {
            result += ints[i];
        }
        return result;
    }

    public static int[] createLogarithmicRefTable(int size) {
        if (size == 0) {
            return new int[0];
        }
        int[] logTable = new int[size * 2];
        double maxLog = Math.log(logTable.length);
        for (int i = 0; i < logTable.length; i++) {
            logTable[i] = (int) (size - Math.ceil(size * (Math.log(logTable.length - i) / maxLog)));
        }
        int pos = -1;
        for (int i = 0; pos == -1 && i < logTable.length - 1; i++) {
            if (logTable[i] != logTable[i + 1] && logTable[i] != logTable[i + 1] - 1) {
                pos = i;
            }
        }
        if (pos == -1) {
            pos = logTable.length - 1;
        }
        int refTableSize = pos + (size - logTable[pos]);
        int[] refTable = new int[refTableSize];
        System.arraycopy(logTable, 0, refTable, 0, pos);
        for (int i = 0; i < refTableSize - pos; i++) {
            refTable[pos + i] = logTable[pos] + i;
        }
        return refTable;
    }

    public static <T> T findMod(Collection<T> values) {
        int maxVal = -1;
        T maxObj = null;
        Map<T, Integer> compile = new HashMap<T, Integer>();
        for (T obj : values) {
            int val = 1;
            if (compile.containsKey(obj)) {
                val = compile.get(obj) + 1;
            }
            compile.put(obj, val);
            if (val > maxVal) {
                maxVal = val;
                maxObj = obj;
            }
        }
        return maxObj;
    }

    public static int round(int val, int precision) {
        int remainder = val % precision;
        double threshold = ((double) precision) / 2d;
        if (remainder == 0) {
            return val;
        } else if (remainder < threshold) {
            return val - remainder;
        } else {
            return val + precision - remainder;
        }
    }

    /**
     * Arrondi d'un double à un certain nombre de décimales.
     * 
     * @see <a
     *      href="http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places">http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places</a>
     * 
     * 
     * @param value
     * @param precision
     * @return
     */
    public static double round(double value, int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(precision, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Arrondi d'un float à un certain nombre de décimales.
     * 
     * @see <a
     *      href="http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places">http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places</a>
     * 
     * 
     * @param value
     * @param precision
     * @return
     */
    public static float round(float value, int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(precision, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}

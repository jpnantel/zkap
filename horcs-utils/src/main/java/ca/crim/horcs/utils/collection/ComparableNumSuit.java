package ca.crim.horcs.utils.collection;

import java.util.Arrays;

public class ComparableNumSuit implements Comparable<ComparableNumSuit> {

    private final String content;

    private final String comparableStr;
    
    private final static String[] PADDINGS = new String[] {
        "", "0", "00", "000", "0000", "00000", "000000", "0000000", "00000000", "000000000", "0000000000", "00000000000", "000000000000"
    };

    /**
     * Constructeur
     * 
     * @param content
     */
    public ComparableNumSuit(String content) {
        this.content = content;
        String[] tab = content.split("[,]");
        if (tab.length > 1) {
            Arrays.sort(tab);
        }
        for (int i = 0; i < tab.length; i++) {
            String[] numSplit = tab[i].split("[.]");
            String numL = numSplit[0];
            if (numL.length() < 12) {
                numL = PADDINGS[12 - numL.length()] + numL;
            }
            String numR = numSplit.length > 1 ? numSplit[1] : "";
            if (numR.length() > 10) {
                numR = numR.substring(0, 10);
            }
            if (numR.length() < 10) {
                numR = numR + PADDINGS[10 - numR.length()];
            }
            tab[i] = numL + "." + numR;
        }
        this.comparableStr = Arrays.toString(tab);
    }

    @Override
    public int compareTo(ComparableNumSuit o) {
        return this.comparableStr.compareTo(o.comparableStr);
    }

    @Override
    public String toString() {
        return content;
    }

}

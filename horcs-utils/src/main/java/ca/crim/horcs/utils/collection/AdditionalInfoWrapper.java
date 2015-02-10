package ca.crim.horcs.utils.collection;

import org.apache.commons.lang3.StringUtils;

import ca.crim.horcs.utils.i18n.I18n;

/**
 * Classe pour ajouter une information supplémentaires à un composant. L'objet
 * se comporte comme s'il était seul lors du tri, mais s'affiche avec
 * l'information complémentaire entre parenthèses.
 * 
 * @author nanteljp
 * 
 */
public class AdditionalInfoWrapper implements Comparable<AdditionalInfoWrapper> {

    private static final I18n i18n = I18n.getInstance(AdditionalInfoWrapper.class);

    private Comparable<?> mainInfo;

    private Comparable<?> additionalInfo;

    public AdditionalInfoWrapper(Comparable<?> mainInfo, Comparable<?> additionalInfo) {
        if (mainInfo == null) {
            throw new IllegalArgumentException(i18n.tr("Argument principal invalide; null non autorisé"));
        }
        this.mainInfo = mainInfo;
        this.additionalInfo = additionalInfo;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public int compareTo(AdditionalInfoWrapper o) {
        if (o.mainInfo.getClass().equals(mainInfo.getClass())) {
            Comparable c1 = this.mainInfo;
            Comparable c2 = o.mainInfo;
            int mainComp = c1.compareTo(c2);
            if (mainComp == 0) {
                if (o.additionalInfo != null && this.additionalInfo == null) {
                    return -1;
                } else if (o.additionalInfo == null && this.additionalInfo != null) {
                    return 1;
                } else if (o.additionalInfo != null && this.additionalInfo != null
                        && o.additionalInfo.getClass().equals(additionalInfo.getClass())) {
                    Comparable cc1 = this.additionalInfo;
                    Comparable cc2 = o.additionalInfo;
                    return cc1.compareTo(cc2);
                }
            } else {
                return mainComp;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        if (additionalInfo == null || StringUtils.isEmpty(additionalInfo.toString())) {
            return mainInfo.toString();
        }
        return mainInfo + " (" + additionalInfo + ")";
    }

}

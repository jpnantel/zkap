package ca.jp.secproj.utils.crypto.zka;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * Représente un ensemble de challenges ZKP échangés entre le client et le
 * serveur
 * 
 * @author nanteljp
 * 
 */
public class ZKPServerChallengeSet implements Serializable {

    /**
     * Eclipse generated.
     */
    private static final long serialVersionUID = -9214156620141983180L;

    private String id;

    private List<ZKPChallenge> challengesList;

    private List<Boolean> challengeSelection;

    private List<BigInteger> answers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ZKPChallenge> getChallengesList() {
        return challengesList;
    }

    public void setChallengesList(List<ZKPChallenge> challengesList) {
        this.challengesList = challengesList;
    }

    public List<Boolean> getChallengeSelection() {
        return challengeSelection;
    }

    public void setChallengeSelection(List<Boolean> challengeSelection) {
        this.challengeSelection = challengeSelection;
    }

    public List<BigInteger> getAnswers() {
        return answers;
    }

    public void setAnswers(List<BigInteger> answers) {
        this.answers = answers;
    }

}

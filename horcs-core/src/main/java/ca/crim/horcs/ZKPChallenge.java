package ca.crim.horcs;

import java.io.Serializable;
import java.math.BigInteger;

public class ZKPChallenge implements Serializable {

    /**
     * Eclipse generated
     */
    private static final long serialVersionUID = 6766145942635354176L;

    private static int CURRENT_ID = 0;

    private int id;

    protected BigInteger t1;

    protected BigInteger t2;

    public ZKPChallenge(BigInteger t1, BigInteger t2) {
        this.id = ++CURRENT_ID;
        this.t1 = t1;
        this.t2 = t2;
    }

    public int getId() {
        return id;
    }

    public BigInteger getT1() {
        return t1;
    }

    public BigInteger getT2() {
        return t2;
    }
}

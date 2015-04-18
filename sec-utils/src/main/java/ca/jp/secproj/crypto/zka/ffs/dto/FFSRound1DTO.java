package ca.jp.secproj.crypto.zka.ffs.dto;

import java.math.BigInteger;
import java.text.ParseException;

import org.codehaus.jackson.annotate.JsonIgnore;

import ca.jp.secproj.utils.math.BigIntegerUtils;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class FFSRound1DTO extends FFSDTOBase {

    private String[] w;

    public FFSRound1DTO() {
	super();
    }

    public FFSRound1DTO(String proverId, String validatorId, BigInteger[] w) {
	super(proverId, validatorId);
	this.w = BigIntegerUtils.BigIntArrayToString(w);
    }

    @JsonIgnore
    public BigInteger[] getWAsBigInt() throws ParseException {
	return BigIntegerUtils.StringArrayToBigInt(w);
    }

    public String[] getW() {
	return w;
    }

    public void setW(String[] w) {
	this.w = w;
    }

}

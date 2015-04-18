package ca.jp.secproj.crypto.zka.ffs.dto;

import java.math.BigInteger;
import java.text.ParseException;

import ca.jp.secproj.utils.math.BigIntegerUtils;

public class FFSRound3DTO extends FFSDTOBase {

    private String[] r;

    public FFSRound3DTO() {
	super();
    }

    public FFSRound3DTO(String proverId, String validatorId, BigInteger[] r) {
	super(proverId, validatorId);
	this.r = BigIntegerUtils.BigIntArrayToString(r);
    }

    public BigInteger[] getResponse() throws ParseException {
	return BigIntegerUtils.StringArrayToBigInt(r);
    }

    public String[] getR() {
	return r;
    }

    public void setR(String[] r) {
	this.r = r;
    }

}

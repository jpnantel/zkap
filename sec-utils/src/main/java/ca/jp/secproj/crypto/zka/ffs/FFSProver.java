package ca.jp.secproj.crypto.zka.ffs;

import java.math.BigInteger;
import java.security.SecureRandom;

import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound1DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound2DTO;
import ca.jp.secproj.crypto.zka.ffs.dto.FFSRound3DTO;
import ca.jp.secproj.utils.math.BigIntegerUtils;

/**
 * Class responsible of executing all the required computation on the prover
 * side for Feige-Fiat-Shamir
 * 
 * @author Jean-Philippe
 *
 */
public class FFSProver {

    private static final BigInteger TWO = new BigInteger("2");

    /**
     * Identifier of the prover
     */
    private String proverId;

    /**
     * Identifier of the validator
     */
    private String validatorId;

    /**
     * Prover's privateKey
     */
    private BigInteger privateKey;

    /**
     * Number of rounds of FFS to execute (essentially the lenght of all
     * vectors: commitment, witness, challenge and response). There is a chance
     * = 1 / (2^nbRounds) that the prover can fool the validator without the
     * valid secret.
     */
    private int nbRounds;

    /**
     * Large number such that n= p*q, p and q being large prime numbers
     */
    private BigInteger n;

    /**
     * Commitment
     */
    private BigInteger[] m;

    /**
     * Witness
     */
    private BigInteger[] w;

    /**
     * Response
     */
    private BigInteger[] r;

    private SecureRandom random;

    /**
     * Constructor requiring a FFSSetup parameter. Builds the commitment and the
     * witness.
     * 
     * @param setup
     */
    public FFSProver(String proverId, String validatorId, BigInteger n, int nbRounds, BigInteger privateKey) {
	this.proverId = proverId;
	this.validatorId = validatorId;
	this.n = n;
	this.nbRounds = nbRounds;
	this.privateKey = privateKey;
	this.m = new BigInteger[nbRounds];
	this.w = new BigInteger[nbRounds];
	this.random = new SecureRandom();
    }

    /**
     * Get the first round 
     * @return
     */
    public FFSRound1DTO getWitness() {
	// compute n-1
	BigInteger maxVal = n.add(BigInteger.ONE.negate());
	// loop over the m array and generate a random biginteger m_i
	for (int i = 0; i < nbRounds; i++) {
	    m[i] = BigIntegerUtils.generateRandomBigInt(maxVal, random);
	    w[i] = m[i].modPow(TWO, n);
	}
	return new FFSRound1DTO(proverId, validatorId, w);
    }

    /**
     * Given a challenge obtained by the validator, produces the response (r)
     * based on the prover's secret and negotiated parameters.
     * 
     * @param challengeDTO
     * @param secret
     * @return
     */
    public FFSRound3DTO getResponse(FFSRound2DTO challengeDTO) {
	if (r != null) {
	    return new FFSRound3DTO(proverId, validatorId, r);
	}
	if (challengeDTO == null || challengeDTO.getChallenge() == null
		|| challengeDTO.getChallenge().length != m.length) {
	    throw new IllegalArgumentException("Challenge size must not be null and match commitment's size. ");
	}
	this.r = new BigInteger[nbRounds];
	for (int i = 0; i < nbRounds; i++) {
	    if (challengeDTO.getChallenge()[i]) {
		r[i] = m[i].multiply(privateKey).mod(n);
	    } else {
		r[i] = m[i].mod(n);
	    }
	}
	return new FFSRound3DTO(proverId, validatorId, r);
    }

}

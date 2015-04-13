package ca.jp.secproj.utils.crypto.zka;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import ca.jp.secproj.utils.math.MathHelper;

public class ZKPClientChallengeSet {

	private String id;

	private BigInteger n;

	private BigInteger r;

	private List<ZKPClientChallenge> challengesList;

	public ZKPClientChallengeSet(int nbChallenges, BigInteger n, BigInteger r) {
		this.id = MathHelper.generateUniqueToken();
		this.n = n;
		this.r = r;

		for (int i = 0; i < nbChallenges; i++) {
			challengesList.add(ZKPClientChallenge
					.generateNewChallengePair(r, n));
		}
	}

	public ZKPServerChallengeSet deriveServerChallengeSet() {
		ZKPServerChallengeSet scs = new ZKPServerChallengeSet();
		List<ZKPChallenge> serverChallenges = new ArrayList<>();
		for (ZKPClientChallenge ccp : challengesList) {
			serverChallenges.add(ccp.deriveServerChallenge());
		}
		scs.setChallengesList(serverChallenges);
		return scs;
	}

}

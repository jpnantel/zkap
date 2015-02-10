package ca.usherbrooke.crypto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class SimpleStreamCipher {

	private Deque<BigInteger> key;

	private List<BigInteger> message;

	private boolean verbose;

	public SimpleStreamCipher(byte[] message, BigInteger key, boolean verbose) {

		this.message = new ArrayList<>();
		for (int i = 0; i < message.length; i++) {
			this.message.add(new BigInteger(new byte[] { message[i] }));
		}
		this.key = new LinkedList<>();
		for (byte b : key.toByteArray()) {
			this.key.push(new BigInteger(new byte[] { b }));
		}
		if (verbose) {
			printKeyAsBinary();
		}

	}

	public String getStreamCipheredMessage() {
		List<BigInteger> transformedMessage = new ArrayList<BigInteger>();
		int index = 0;
		for (BigInteger m : message) {
			BigInteger mask = getMask();
			transformedMessage.add(mask.xor(m));
			if (verbose) {
				System.out.println("-------------------------------");
				System.out.println("Block :" + index++);
				System.out.println("Message byte: " + m.toString(2));
				System.out.println("Mask byte: " + mask.toString(2));
				System.out.println("XOR: " + mask.xor(m).toString(2));
			}
		}
		StringBuilder sb = new StringBuilder();
		for (BigInteger bi : transformedMessage) {
			sb.append((char) bi.intValue());
		}
		return sb.toString();
	}

	private BigInteger getMask() {
		BigInteger m = key.pop();
		key.addLast(m);
		return m;
	}

	public void printKeyAsBinary() {
		for (BigInteger b : key) {
			System.out.println(b.toString(2));
		}
	}
}

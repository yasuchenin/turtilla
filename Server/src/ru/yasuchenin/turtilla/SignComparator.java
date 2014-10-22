package ru.yasuchenin.turtilla;

import java.util.HashMap;

public class SignComparator {
	private byte[] sampleBuffer;
	private byte[] signByte;
	private HashMap<Integer, Boolean> zvezdochkaMap;
	private static HashMap<Character, Integer> hexMap;
	private int signLength;

	static {
		hexMap = new HashMap<Character, Integer>();
		hexMap.put('0', 0);
		hexMap.put('1', 1);
		hexMap.put('2', 2);
		hexMap.put('3', 3);
		hexMap.put('4', 4);
		hexMap.put('5', 5);
		hexMap.put('6', 6);
		hexMap.put('7', 7);
		hexMap.put('8', 8);
		hexMap.put('9', 9);
		hexMap.put('A', 10);
		hexMap.put('B', 11);
		hexMap.put('C', 12);
		hexMap.put('D', 13);
		hexMap.put('E', 14);
		hexMap.put('F', 15);
	}

	private int stringToHex(String str) throws IllegalArgumentException {
		Integer valueFirst;
		Integer valueSecond;
		signByte = new byte[str.length() / 2];
		zvezdochkaMap = new HashMap<Integer, Boolean>();
		try {
			for (int i = 0; i < str.length(); i += 2) {
				if (str.charAt(i) == '*' && str.charAt(i + 1) == '*') {
					zvezdochkaMap.put(i / 2, true);
					signByte[i / 2] = 0;
					continue;
				}
				valueFirst = hexMap.get(str.charAt(i));
				valueSecond = hexMap.get(str.charAt(i + 1));
				signByte[i / 2] = (byte) (valueFirst * 16 + valueSecond);
			}
		} catch (NullPointerException x) {
			throw new IllegalArgumentException("in stringToHex() - wrong sign!");
		}
		return str.length() / 2;
	}

	public boolean searchSign(String sign) throws IllegalArgumentException {
		int j;
		signLength = stringToHex(sign);
		for (int i = 0; i < sampleBuffer.length; i++) {
			j = 0;
			while (j < signLength
					&& (signByte[j] == sampleBuffer[i + j] || zvezdochkaMap
							.containsKey(j) == true))
				j++;
			if (j == signLength)
				return true;
		}
		return false;
	}

	public SignComparator(byte[] sample) {
		this.sampleBuffer = sample;
	}
}

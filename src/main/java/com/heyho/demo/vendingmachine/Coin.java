package com.heyho.demo.vendingmachine;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * Represents a Coin. When adding more, keep them in numerically ascending order.
 * <p>
 * Warning: This class uses ordinals for iteration purposes.
 */
public enum Coin {
	NICKLE(0.05), DIME(0.10), QUARTER(0.25), HALFDOLLAR(0.50),
	DOLLARCOIN(1.00);
	
	private double value;
	public static final Coin values[] = values();
	   
	private Coin(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public static boolean isCoin(String str) {
		return Coin.toCoin(str) != null;
	}
	
	public static Coin toCoin(int ord) {
		if (ord >= 0 && ord<values.length)
			return values[ord];
		return null;
	}
	
	public static Coin toCoin(String text) {
		try {
			return Coin.valueOf(text.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
		
	public static final Coin MAX = Coin.toCoin(values.length-1);
	
	public Coin pred() {
		return Coin.toCoin(this.ordinal()-1);
	}
	
	public static String allToString() {
		return "[" + Arrays.stream(Coin.values())
		.map(c -> c.toString() + ":" + c.value)
		.collect(Collectors.joining(", "))
		+ "]";
	}
}

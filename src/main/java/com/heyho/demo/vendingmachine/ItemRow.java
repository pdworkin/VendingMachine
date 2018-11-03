package com.heyho.demo.vendingmachine;


/**
 * A ItemRow represents a group of the same Item with of a certain size along
 * with what label to file it under.  You cannot store Items of different types
 * under he same label.  The only method of interest is {@code vendItem()} which is called
 * to actually dispense an item from some row.
 */
public class ItemRow implements Comparable<ItemRow> {
	Item item;
	int count = 0;
	String label;
	
	ItemRow(String name, String type, double price,
				int count, String label) {
		this.item = new Item(name, type, price);
		this.count = count;
		this.label = label;
	}
	
	public Item getItem() {
		return item;
	}
	public int getCount() {
		return count;
	}
	public String getLabel() {
		return label;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label + ": " + item;
	}
	
	@Override
	public int compareTo(ItemRow other) {
		if (other instanceof ItemRow) {
			return this.label.compareTo(((ItemRow) other).label);
		}
		return -1;	
	}
}

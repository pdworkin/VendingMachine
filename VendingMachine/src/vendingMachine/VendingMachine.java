package vendingMachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* <h1>VendingMachine</h1>
* This is a sample project that implements a vending machine-- a common coding 
* exercise from the web.
* <p>
* The machine is implemented by this class and tested with {@link VendingMachineTest}.
* JUnit rates the code coverage about 93%.
* <p>
* The model is one of those glass fronted machines with a different item in each row, 
* selected by a label tag. The machine processes coins correctly and can be reloaded.
* <p>
* <b>UI:</b> You can enter a kind of money, such as "quarter", or the label of an item to vend, 
* or "refund", "restock', or "quit".
* <p>
* The data for the restock list is generated by {@code retrieveRestockData()}. It is currently 
* hard-wired, but it could be made to get data from a file or database.  When making  
* change during vending, the optimal combination of coins is returned, favoring larger 
* coins over smaller.
* 
* @author  Paul Dworkin
* @version 1.1
* @since   2018-08=26 
*/
public class VendingMachine {

	// These hold the machine state
	Map<Coin, Integer> coinsInMachine;		
	Map<Coin, Integer> coinsInPurchase;
	List<ItemRow> machineContents;

	/**
	 * Generates a List of items to restock the machine with.
	 * <p>
	 * This is up-top for accessibility.
	 * 
	 * @return List of items for restocking
	 */
	List<ItemRow> retrieveRestockGoods() {
		// normally we would get the restock data from a file or service
		return  new ArrayList<>(Arrays.asList(
				new ItemRow("Abar", "Chocoate", 0.75, 3, "A1"),
				new ItemRow("Bbar", "Chocoate", 1.50, 3, "A2"),
				new ItemRow("Cbar", "Peanut", 1.25, 3, "B1"),
				new ItemRow("Dbar", "Peanut", 0.50, 3, "B2"),
				new ItemRow("Ebar", "Mint", 0.50, 3, "C1")
				));
	}

	/**
	 * Generates a Map of how to reset the coins during restocking.
	 * <p>
	 * This is up-top for accessibility.
	 * 
	 * @param number   Takes an optional int which specifies the number of each coin.  Otherwise
	 * a default number is used.
	 * @return Map telling how many of each kind of Coin to use
	 */
	Map<Coin, Integer> retrieveRestockMoney(int number) {
		Map<Coin, Integer> map = new HashMap<>();
		Arrays.stream(Coin.values()).forEach(c -> map.put(c, number));
		return map;
	}
	Map<Coin, Integer> retrieveRestockMoney() {
		return retrieveRestockMoney(3);
	}
	
	
	public VendingMachine() {
		restockMachine(retrieveRestockGoods(), retrieveRestockMoney());
		coinsInPurchase = new HashMap<>();
	}
	
	
	// Awful little utility I had to write as penance for using doubles to 
	// represent money.  Addition works OK, but subtraction of doubles is unstable,
	// so we have to do it carefully.
	// TODO: convert over to integer cents with a standard formatting method.
	double subtractDouble(double first, double second) {
		return ((double) (Math.round(first*100) - Math.round(second*100))) / 100.0;
	}

	// Utility method that returns how much money is in the machine.  Mostly used
	// for testing.
	double valueInMachine()
	{		
		return coinsInMachine.keySet().stream()
				.map((Coin k) -> k.getValue()*coinsInMachine.get(k))
				.mapToDouble(d -> d)
				.sum();
		
	}
	// Utility method that returns how much money is in the current pre-purchase
	// buffer.  Mostly used for testing.
	double valueInPurchase()
	{		
		return coinsInPurchase.keySet().stream()
				.map((Coin k) -> k.getValue()*coinsInPurchase.get(k))
				.mapToDouble(d -> d)
				.sum();
		
	}
	
	// Utility method that returns a List of all the ItemRows in the machine that share
	// the same label.  If there's more than one of them, the machine is broken.
	List<ItemRow> getLabelsThatMatch(String label) {
		return machineContents.stream()
			.filter(i -> i.getLabel().equalsIgnoreCase(label))
			.collect(Collectors.toList());
	}

	
	// Recursive entry point for the {@code makeChange()} method below.  
	// Tries to use as many of a particular large coin to fill in required amount and then
	// recurses to the next lower coin to fill in the remainder.  Then iIterates on fewer
	// and fewer of the big coin until using 0 of them to get to 0.00 remaining.
	boolean makeChangeHelper(double remaining, Coin coin) {
		// We have recursed down to a Coin that's too small.  This is a dead end.
		if (coin == null)
			return false;
		
		// don't use more of the coin than can fit
		int max = (int) Math.floor(remaining/coin.getValue());	

		// use both the coins in the machine and the purchase buffer to make change
		max = Math.min(max, coinsInMachine.getOrDefault(coin, 0)  
					+ coinsInPurchase.getOrDefault(coin, 0));   

		// start with a lot of the coin and work your way down to fewer, including 0 of them
		for (int i=max; i>=0; i--) {						
			double tmp = subtractDouble(remaining, i*coin.getValue());       
			
			// if we have satisfied the remainder or any recurse does it, report success upward
			if (tmp == 0.00 || makeChangeHelper(tmp, coin.pred())) {  
				
				// deduct the used quantity of this coin from the machine
				if (i>0){
					coinsInMachine.put(coin, coinsInMachine.getOrDefault(coin, 0)-i);
					System.out.print("" + i + " " + coin + ", ");
				}
				return true;
			}
		}
		
		// if you fall off the end, then this is a dead end.  Tell parent to go on to
		// the next iteration
		return false;
	}
	
	/**
	 * Generates a configuration of change that satisfies a {@code amount}.  Returns false if it
	 * can't find any.  If successful, the amounts of each type of coin are deducted from
	 * the machine on the recursion's way back up.
	 * <p>
	 * The chosen result in optimal in that it will choose more of a large coin in 
	 * preference to smaller ones. That gives the machine more flexibility in making 
	 * change in the future.
	 * 
	 * @param amount  Amount of change to make
	 * @return        True if a combination of change was found
	 */
	boolean makeChange(double amount) {		
		System.out.print("Change: ");
		if (makeChangeHelper(amount, Coin.MAX)) {
			if (amount == 0.00)
				System.out.print("None");
			System.out.println();
			
			// now that we've deducted what was needed for the refund from the machine,
			// we put all of the money in the purchase buffer into the machine
			for (Coin coin: coinsInPurchase.keySet()) {
				coinsInMachine.put(coin, coinsInMachine.getOrDefault(coin, 0)
						+ coinsInPurchase.get(coin));
				coinsInPurchase.put(coin, 0);
			}
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * An output method that displays what's for sale in the machine.  0 or more labeled
	 * rows, the items they contain and their prices.
	 * 
	 * @return The contents of the machine for testing purposes
	 */
	List<ItemRow> listGoods() {
		System.out.format("Vending machine contains:%n%n");
		if (this.machineContents.size() == 0)
			System.out.println("Empty");
		else
			machineContents.stream()
				.sorted()
				.forEach(System.out::println);
		return machineContents;
	}
	
	
	/**
	 * Takes a List of new goods and adds them to the machine's contents, also takes
	 * a Map to reset the quantity of each kind of Coin.  Remember: the goods ADD, the
	 * money REPLACES.  I assume this is the desired behavior for servicing a vending 
	 * machine.
	 * <p>
	 * There are two methods that typically generate the data to pass to this 
	 * method. They are {@code retrieveRestockGoods()} and {@code retrieveRestockMoney()}.
	 * 
	 * @param goods  The ItemRows to add to the machine.  If there are tags that duplicate 
	 * what's already in the machine, the ItemRow's quantity will either be added to it
	 * or rejected depending on whether the Items they contain are equal).  If null, 
	 * no action will be taken on the machine contents.
	 * @param money  Maps Coins to the quantity that there should be in the machined.
	 * If null, no action will be taken on the machine's money.
	 * @return       The updated contents of the machine.  For testing purposes.
	 */
	List<ItemRow> restockMachine(List<ItemRow> goods, Map<Coin, Integer> money) {
		if (money != null)
			coinsInMachine = money;   //TODO: should clone

		if (machineContents == null)
			machineContents = new ArrayList<>();

		if (goods == null) {
			return null;
		}
		for(ItemRow itemc: goods) {
			List<ItemRow> matches = getLabelsThatMatch(itemc.getLabel());
			if (matches.size() == 0) {
				machineContents.add(itemc);  	//TODO: should clone
			} else if (matches.size() == 1){
				if (itemc.getItem().equals(matches.get(0).getItem()))
					matches.get(0).count += itemc.getCount();
				else
					System.out.println("Warning: " + itemc.getLabel()
						+ " already contins " + matches.get(0).getItem().getName()
						+ ".  Can't put in " + itemc.getItem().getName());
			} else {
				System.out.println("Machine is corrupt!");
				throw new RuntimeException("Corrupt machine");
			}
		}
		return machineContents;
	}
	
	
	/**
	 * Process one coin inserted into the machine. 
	 * 
	 * @param coin   The coin to add
	 * @return       The total now in the machine.  For testing purposes.
	 */
	double doCoin(Coin coin) {
		coinsInPurchase.merge(coin, 1, (current, valnew) -> current+1);
		
		System.out.format("Adding credit: $%.2f%n", coin.getValue());
		return valueInPurchase();
	}
	
	
	/**
	 * Refunds all Coins in the purchase buffer.  Clears the purchase buffer.
	 * 
	 * @return The total refunded.  For testing purposes.
	 */
	double doRefund() {
		double tmp = valueInPurchase();
		System.out.format("Refunding: %.2f%n", tmp);
		coinsInPurchase = new HashMap<>();
		return tmp;
	}
	
	
	// Actually vend item.
	Item vendItem(ItemRow ir) {		
		System.out.println("Vending: " + ir.getItem());
		
		if (--ir.count <= 0) {
			machineContents.remove(ir);
		}
		
		return ir.getItem();
	}

	/**
	 * Finds what Item the given label points to and if the user can afford it, and 
	 * we can make change for it, vend the item and return change.  Any other
	 * condition generates the appropriate advisory message.  
	 * 
	 * @param label   The label of the Item we wish to purchase
	 * @return        For testing purposes: if successfully vending, the item vended; if 
	 * the price is too high, the extra amount needed; otherwise null.
	 */
	Object doLabel(String label) {
 		List<ItemRow> matches = getLabelsThatMatch(label);
				
 		if (matches.size() == 1) {
			ItemRow ic = matches.get(0);
			//@ have to do this because subtraction on double isn't stable
			double deficit = subtractDouble(valueInPurchase(), ic.getItem().getPrice()); 
			if (deficit >= 0.00) {
				if (makeChange(deficit)) {
					vendItem(ic);
					return ic.getItem();
				} else {
					System.out.println("Machine can not make change with the cash on hand.  Insert more money or ask for refund");
					return null;
				}
			} else {
				System.out.format("You need $%.2f more to buy item in %s%n", -deficit, label);
				return -deficit;
			}
		} else if (matches.size() > 1){
			System.out.println("Machine is corrupt)");
			throw new RuntimeException("Corrupt machine");
		}
 		return null;
	}

	
	/**
	 * Main loop for exercising functionality in class VendingMachine.  Displays 
	 * machine state and then prompts for an command.  Repeats until it receives "quit".
	 */
	void start() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		
		try {
			while (true) {
				// display machine state
				listGoods();
				System.out.println("In machine: " + coinsInMachine);
				System.out.println("In purachse: " + coinsInPurchase);
				System.out.println();
				System.out.format("Credit=%.2f: Enter type of cash, label of item, refund, restock, or quit: ", valueInPurchase());

				// read a command
				if ((line=reader.readLine()) == null || line.trim().equalsIgnoreCase("quit"))
					break;
				line = line.trim().toLowerCase();
				
				// Look it up
				if (Coin.isCoin(line)) {
					doCoin(Coin.toCoin(line));
				} else if (line.equals("refund")) {
					doRefund();
				} else if (line.equals("restock")) {
					restockMachine(retrieveRestockGoods(), retrieveRestockMoney());
				} else if (getLabelsThatMatch(line).size() > 0) {
					doLabel(line);
				} else {
					System.out.println("Unrecognized item label: " + line);
				}
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("I/O error", e);
		}
		System.out.format("Money remining in machine: %.2f%n", valueInMachine());
		System.out.format("Money remining in purchase: %.2f%n", valueInPurchase());
	}  	
	
	
	// Main() for testing.  Creates a VendingMachiine object and starts it.
	public static void main(String... args) {
		new VendingMachine().start();
	}
	}
	

/**
 * Represents a Coin. When adding more, keep them in numerically ascending order.
 * <p>
 * Warning: This class uses ordinals for iteration purposes.
 */
enum Coin {
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

/**
 * An Item holds a name, type, and price for one object for sale.  Two Item's are
 * {@code equal()} if their contents match.
 */
class Item {
	private String name;
	private String type;
	private double price;
	
	Item(String name, String type, double price) {
		this.name = name;
		this.type = type;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public String toString() {			
		return String.format("%s - %s ($%.2f)", name, type, price);
	}
}

/**
 * A ItemRow represents a group of the same Item with of a certain size along
 * with what label to file it under.  You cannot store Items of different types
 * under he same label.  The only method of interest is {@code vendItem()} which is called
 * to actually dispense an item from some row.
 */
class ItemRow implements Comparable<ItemRow> {
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

package com.heyho.demo.vendingmachine;


import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
* <h1>VendingMachineTest</h1>
* This a JUnit test file for {@link VendingMachine}.
* <p>
* Exercises all non-trivial method and classes.  The tests are documented by their
* error strings.  Each one is a statement of something that should be true with the word
* "fails" on the end of it.
* 
* @author  Paul Dworkin
* @version 1.1
* @since   2018-08=26
*/
class VendingMachineTest {
	VendingMachine vm = new VendingMachine();

	
    @BeforeClass
    public static void setUpClass() {
    }
     
    @Before
    public void setUp() {
//		vm = new VendingMachine();  didn't work for me
    }
     
	@Test
	void testVendingMachineClass() {
		assertNotNull(vm, "VendingMachine() new fails");
		assertNotNull(vm.machineContents, "VendingMachine() machineContents not null fails");
		assertNotNull(vm.coinsInMachine, "VendingMachine() coinsInMachine not null fails");
		assertNotNull(vm.coinsInPurchase, "VendingMachine() coinsInPurchase not null fails");
		assertEquals(0.00, vm.valueInPurchase(), "VendingMachine() purchase value set to 0.00 fails");
	}

    @Test
	void testRetrieveRestockGoods() {
		assertNotNull(vm.retrieveRestockGoods(), "retrieveRestockGoods() does not return null fails");
	}
 
    @Test
    void testRetrieveRestockMoney() {
		Collection<Integer> values = vm.retrieveRestockMoney(4).values();
		values.removeIf(q -> q==4);
		assertTrue(values.isEmpty(), "retrieveRestockMoney(int) quantity of coins is correct in all cases fails");

		assertNotNull(vm.retrieveRestockMoney(), "retrieveRestockMoney() does not return null fails");
    }
	

    @Test
	void testCoinClass() {
		assertEquals(0.05, Coin.NICKLE.getValue(), "Coin getValue() fails");
		assertTrue(Coin.isCoin(Coin.values()[0].toString()), "Coin.isCoin(() good input fails");
		assertFalse(Coin.isCoin("DIMEE"), "Coin.isCoin() bad input fails");
		assertEquals("QUARTER", Coin.toCoin("QUARTER").toString(), "Coin.toCoin() good input fails");
		assertNull(Coin.toCoin("DOLLARBILLL"), "Coin.toCoin() bad input fails");
	}
	
	@Test
	void testItemClass() {		
		Item item = new Item("Name", "Type", 0.75);
		
		assertNotNull(item, "Item() new fails");
		assertEquals(0.75, item.getPrice(), "Item getPrice() fails");
	}

	@Test
	void testItemRowClass() {		
		Item item = new Item("Name", "Type", 0.75);
		ItemRow itemc = new ItemRow("Name", "Type", 0.75, 1, "@1");
		assertNotNull(itemc, "ItemRow() new fails");
		assertEquals(1, itemc.getCount(), "ItemRow getCount() fails");

		vm.restockMachine(Arrays.asList(itemc), vm.retrieveRestockMoney());

		vm.doCoin(Coin.DOLLARCOIN);
		double before = vm.machineContents.size();
		ItemRow match = vm.getLabelsThatMatch(itemc.getLabel()).get(0);
		assertEquals(item, vm.vendItem(match), "ItemCount vendItem() vends correct item fails");
		assertEquals(-1, vm.machineContents.size()-before, "ItemCount vendItem() machine is empty fails");
	}
	
	@Test
	void testValueInMachine() {
		ItemRow itemc = new ItemRow("Name", "Type", 0.75, 1, "@1");
		vm.restockMachine(Arrays.asList(itemc), vm.retrieveRestockMoney(0));
		assertEquals(0.00, vm.valueInMachine(), "valuesInMachine() machine ccorrectly contains 0.00 fails");

		vm.doCoin(Coin.HALFDOLLAR);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.DIME);
		double before = vm.valueInMachine();
		Object result = vm.doLabel(itemc.getLabel());
		assertTrue(result instanceof Item, "valuesInMachine() object returned fails");
		assertEquals(itemc.getItem(), (Item) result, "valuesInMachine() object returned fails");
		assertEquals(0.75, vm.valueInMachine()-before, "valuesInMachine() machine increased by correct amount fails");
	}

	@Test
	void testValueInPuchase() {
		assertEquals(0.00, vm.valueInPurchase(), "valueInPurchase() correct valuation of initial purchase fails");

		vm.doCoin(Coin.DOLLARCOIN);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.DIME);
		assertEquals(1.35, vm.valueInPurchase(), "valueInPurchase() correct valuation of coins fails");
		
		vm.doRefund();
		assertEquals(0.00, vm.valueInPurchase(), "valueInPurchase() final evaluation of final purchase fails");
	}
	
	@Test
	void testMakeChange() {
		double before;
		
		vm.restockMachine(null, vm.retrieveRestockMoney(0));
		before = vm.valueInMachine();
		assertFalse(vm.makeChange(0.25), "makeChange() doesn't find 0.25 in [] fails");
		assertEquals(0.00, vm.valueInMachine()-before, "makeChange() machine empty fails");
		assertEquals(0.00, vm.valueInPurchase(), "makeChange() purchse empty fails");
		
		vm.restockMachine(null, vm.retrieveRestockMoney(0));
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		before = vm.valueInMachine();
		assertTrue(vm.makeChange(0.25), "makeChange() finds $0.25 to refund in [QQ] fails");
		assertEquals(0.25, vm.valueInMachine()-before, "makeChange() machine increased by correct amount fails 1");
		assertEquals(0.00, vm.valueInPurchase(), "makeChange() purchase retains correct amount fails 1");
		
		vm.restockMachine(null, vm.retrieveRestockMoney(0));
		vm.doCoin(Coin.HALFDOLLAR);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.DIME);
		vm.doCoin(Coin.DIME);
		vm.doCoin(Coin.DIME);
		vm.doCoin(Coin.NICKLE);
		before = vm.valueInMachine();
		assertTrue(vm.makeChange(1.00), "makeChange() finds 1.00 to refund in [HQDDDN] fails");
		assertEquals(0.10, vm.valueInMachine()-before, "makeChange() machine increased by correct amount fails 2");
		assertEquals(0.00, vm.valueInPurchase(), "makeChange() purchase retains correct amount fails 2");
		
		vm.restockMachine(null, vm.retrieveRestockMoney(0));
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.DIME);
		vm.doCoin(Coin.DIME);
		vm.doCoin(Coin.DIME);
		before = vm.valueInMachine();
		assertFalse(vm.makeChange(0.50), "makeChange() doesn't find $0.50 to refund in [QDDD] fails");
		assertEquals(0.00, vm.valueInMachine()-before, "makeChange() machine not increased fails 3");
		assertEquals(0.55, vm.valueInPurchase(), "makeChange() purchase not reduced fails 3");
		vm.coinsInPurchase = new HashMap<>();  // have to wipe money left over in the purchase

		vm.restockMachine(null, vm.retrieveRestockMoney(0));
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		before = vm.valueInMachine();
		assertTrue(vm.makeChange(1.75), "makeChange() finds $1.75 to refund in [QQQQQQQQ] fails");
		assertEquals(0.25, vm.valueInMachine()-before, "makeChange() machine increased by correct amount fails 4");
		assertEquals(0.00, vm.valueInPurchase(), "makeChange() purchase retains correct amount fails 4");

		vm.restockMachine(null, vm.retrieveRestockMoney(0));
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		before = vm.valueInMachine();
		assertFalse(vm.makeChange(2.50), "makeChange() doesn't find $2.50 to refund in [QQQQQQQQ] fails");
		assertEquals(0.00, vm.valueInMachine()-before, "makeChange() machine not increased fails 5");
		assertEquals(2.00, vm.valueInPurchase(), "makeChange() purchase not reduced fails 5");
		vm.coinsInPurchase = new HashMap<>();  // have to wipe money left over in the purchase
	}
	
	@Test
	void testListGoods() {
		assertEquals(vm.listGoods(), vm.machineContents, "listGoods() correct return fails");
	}

	@Test
	void testDoCoin() {
		assertEquals(0.00, vm.valueInPurchase(), "doCoin() purchase starts with no money fails");
		double result = vm.doCoin(Coin.HALFDOLLAR);
		assertEquals(0.50, vm.valueInPurchase(), "doCoint() correct purchase money count fails 1");
		assertEquals(0.50, result, "doCoint() correct purchase money count fails 2");
	}

	@Test
	void testDoChange() {
		assertEquals(0.00, vm.valueInPurchase(), "doChange() purchase starts with no money fails");
	
		vm.doCoin(Coin.DOLLARCOIN);
		double change = vm.doRefund();
		
		assertEquals(1.00, change, "doChange() correct change returned fails");
		assertEquals(0.00, vm.valueInPurchase(), "doChange() purchase ends with no money fails");
	}

	@Test
	void testRestockMachine() {
//@ do null tests
		int before = vm.machineContents.size();
		ItemRow ic1 = new ItemRow("Name", "Type", 0.25, 1, "@1");
		ItemRow ic2 = new ItemRow("Name2", "Type", 0.25, 1, "@1");
		ItemRow ic3 = new ItemRow("Name3", "Type", 0.50, 1, "@2");
		ItemRow ic4 = new ItemRow("Name3", "Type", 0.50, 1, "@2");  // duplicate row
		vm.restockMachine(Arrays.asList(ic1, ic2, ic3,ic4), vm.retrieveRestockMoney(1));
		assertEquals(2, vm.machineContents.size()-before, "restockMachine() mechine contents size correct before fails");
		
		vm.doCoin(Coin.QUARTER);
		Object item = vm.doLabel(ic1.getLabel());
		assertTrue(item instanceof Item, "restockMachine() restocked item exists fails");
		assertEquals(ic1.getItem(), (Item) item, "restockMachine() restocked correct item fails");
		assertEquals(1, vm.machineContents.size()-before, "restockMachine() mechine contents size correct after fails");
	}
	
	@Test
	void testDoLabel() {
		ItemRow ic1 = new ItemRow("Name", "Type", 0.25, 1, "@1");
		ItemRow ic2 = new ItemRow("Name2", "Type", 0.50, 1, "@2");
		ItemRow ic3 = new ItemRow("Name3", "Type", 0.50, 1, "@3");
		vm.restockMachine(Arrays.asList(ic1, ic2, ic3), vm.retrieveRestockMoney(0));

		vm.doCoin(Coin.QUARTER);
		int before = vm.machineContents.size();
		Object result = vm.doLabel(ic2.getLabel());
		assertTrue(result instanceof Double, "doLabel() extra money required exists fails");
		assertEquals(0.25, (double) result, "doLabel() extra money required is correct fails");
		assertEquals(0, vm.machineContents.size()-before, "doLabel() contents unmodified after unsuccessful purchase fails");

		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		int beforeMC = vm.machineContents.size();
		double beforeVM = vm.valueInMachine();
		result = vm.doLabel(ic2.getLabel());
		assertTrue(result instanceof Item, "doLabel() vended item exists fails");
		assertEquals(ic2.getItem(), (Item) result, "doLabel() vended correct item fails");			
		assertEquals(-1, vm.machineContents.size()-beforeMC, "doLabel() machine contents reduced correctly fails");
		assertEquals(0.00, vm.valueInPurchase(), "doLabel() purchase contains no money fails");
		assertEquals(0.50, vm.valueInMachine()-beforeVM, "doLabel() machine gained corect amount fails");

		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.QUARTER);
		result = vm.doLabel(ic2.getLabel());
		assertNull(result, "doLabel() can't buy something that is non-existent fails");
		assertEquals(0.50, vm.valueInPurchase(), "doLabel() purchase contains correct money after non-existent fails");

		vm.coinsInPurchase = new HashMap<>();   // Have to clear out the purchase left over
		vm.doCoin(Coin.QUARTER);
		vm.doCoin(Coin.DIME);
		vm.doCoin(Coin.DIME);
		vm.doCoin(Coin.DIME);
		result = vm.doLabel(ic3.getLabel());
		assertNull(result, "doLabel() correctly can't make change for purchase fails");
		assertEquals(0.55, vm.valueInPurchase(), "doLabel() purchase contains correct money after can't make change fails");
	}

	
	@Test
	void testMainLoop() {
		// TTODO: This just exercises the code.  Should use a IO package that can interact with it
		System.setIn(new ByteArrayInputStream("quarter\nrefund\nrestock\na1\nXXX\nquit\n".getBytes()));
		vm.mainLoop();
	}

	
    @After
    public void tearDown() {
		vm = null;
    }
     
    @AfterClass
    public static void tearDownClass() {
    }


}

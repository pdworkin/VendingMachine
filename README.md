# VendingMachine

Paul Dworkin, v1.1, 08-26-2018

This is a sample project that implements a vending machine-- a common coding exercise from the web.

The machine is implemented by class VendingMachine and tested with VendingMachineTest. JUnit rates the code coverage about 93%.

The model I'm using is one of those glass fronted machines with a different item in each row, selected by a label tag. The machine processes coins correctly and can be reloaded.

## To Run
A precompiled jar file is included in the repository so you can just do:
```java -jar target/vendingmachine-1.1.jar
```

To remake from source do:
```
mvn clean; mvn package
```

To run the JUnit tests do:
```
mvn test
```

Javadocs are in <code><target/site/apidocs/>code>.  To remake them, do:
```
mvn javadoc:javadoc
```

## Operation
In this text based application, enter a kind of money, such as "quarter", or the label of an item to vend, or "refund", "restock', or "quit".

The data for the restock list is generated by retrieveRestockData(). It is currently hard-wired, but it could be made to get data from a file or database.  

When making change during vending, the optimal combination of coins is returned, favoring larger coins over smaller.

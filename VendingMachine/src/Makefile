

SRC=src
DEST=bin
DOC=doc
JARFILE= wm.jar    

BUILDHOME := $(shell pwd)
#JAVA_HOME := $(JAVA_HOME) \
#                             || ($(wildcard /usr/libexec/java_home) && $(shell /usr/libexec/java_home)) \
#                             || $(wildcard /usr/bin/java)
JAVA_HOME := $(shell /usr/libexec/java_home)
JAVAC= $(JAVA_HOME)/bin/javac
PATH := $(JAVA_HOME)/bin:$(PATH)
CLASSPATH := .:$(JAVA_HOME)/lib/classes.zip:$(JSDKHOME)/lib/classes.zip:/Users/adminuser/.p2/pool/plugins/org.junit_4.12.0.v201504281640/junit.jar:$(BUILDHOME)/$(DEST)
JAVA=$(JAVA_HOME)/bin/java
JAVACFLAGS=-deprecation
JUNITPATH=../../../../junit
JAVAFILES = $(wildcard *.java)

default : build

vendingMachine/%.class : vendingMachine/%.java
#.java.class:
	$(JAVAC) -classpath "$(CLASSPATH):$(JUNITPATH)/unit-4.10.jar:hamcrest-core-1.3.jar:./vendingMachine/*" $(JAVACFLAGS) $<

MANIFEST="*.class\\n$(for a in *.class; do echo \\"Name: $a\\nJava-Bean: False\\n\\n"; done)"

$(JARFILE) : vendingMachine/VendingMachine.class vendingMachine/VendingMachineTest.class Makefile
	jar cf $(JARFILE) $(wildcard *.class) \
	echo "\nTo run type 'make run'"
#	echo $(JAVAFILES) \
#	echo $(MANIFEST) \
#	echo "$(MANIFEST)" > ./manifest.tmp \
#	jar cfm0 $(JARFILE) ./manifest.tmp $(wildcard *.class) \
#	rm ./manifest.tmp \

build : $(JARFILE)

run : 
	$(JAVA) -cp vendingMachine/vendingMachine.jar vendingMachine.VendingMachine

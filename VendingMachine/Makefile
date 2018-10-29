

SRC=src
DEST=bin
DOC=doc
JARFILE= app.jar    

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

build : 
	$(MAKE) -C src build
	
run : 
	$(MAKE) -C src run

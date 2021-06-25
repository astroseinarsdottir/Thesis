
# Need to link to a PRISM distribution
PRISM_DIR = ../prism/prism

# For compilation, just need access to classes/jars in the PRISM distribution
# We look in both the top-level and the prism sub-directory
# (currently svn/git repos and downloaded distributions differ in structure)
PRISM_CLASSPATH = "./classes:$(PRISM_DIR)/classes:$(PRISM_DIR)/lib/*:$(PRISM_DIR)/prism/classes:$(PRISM_DIR)/prism/lib/*:./lib/*"

# This Makefile just builds all java files in src and puts the class files in classes

JAVA_FILES := $(shell cd src && find . -name '*.java')
CLASS_FILES = $(JAVA_FILES:%.java=classes/%.class)

CODEGENERATOR_FILES := $(shell cd src && find . -name 'CodeGenerator.java')
CLASS_CODEGENERATOR_FILES = $(CODEGENERATOR_FILES:%.java=classes/%.class)

MODEL_FILES := $(shell cd src && find . -name 'Model*.java')
CLASS_MODEL_FILES = $(MODEL_FILES:%.java=classes/%.class)

IMPL_FILES := $(shell cd src && find . -name 'Implementation.java')
CLASS_IMPL_FILES = $(IMPL_FILES:%.java=classes/%.class)

DTMC_FILES := $(shell cd src && find . -name 'DTMCGenerator.java')
CLASS_DTMC_FILES = $(DTMC_FILES:%.java=classes/%.class)

default: all

all: init $(CLASS_MODEL_FILES) init $(CLASS_IMPL_FILES) init $(CLASS_CODEGENERATOR_FILES) init $(CLASS_DTMC_FILES) init $(CLASS_FILES)

init:
	@mkdir -p classes

classes/%.class: src/%.java
	(javac -classpath $(PRISM_CLASSPATH) -d classes $<)

# Test execution

test:
	PRISM_DIR=$(PRISM_DIR) PRISM_MAINCLASS=project.Main bin/run

# Clean up

clean:
	@rm -f $(CLASS_FILES)

celan: clean

CLASSPATH = acm.jar:yahtzeelib.jar
CFLAGS = -cp .:$(CLASSPATH)

JAVA_FILES = YahtzeeConstants.java Yahtzee.java
CLASS_FILES = $(JAVA_FILES:.java=.class)

all: yahtzee

yahtzee: $(CLASS_FILES)

%.class: %.java
	javac $(CFLAGS) $<

clean:
	rm -f *.class

submit: pset4.zip

pset4.zip: $(JAVA_FILES) acm.jar yahtzeelib.jar Makefile
	zip $@ $^

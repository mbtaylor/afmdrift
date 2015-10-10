
JSRC = *.java

jar: drift.jar

drift.jar: $(JSRC)
	rm -rf tmp
	mkdir -p tmp
	javac -Xlint:unchecked -d tmp $(JSRC) \
           && jar cf $@ -C tmp .
	rm -rf tmp

clean:
	rm -rf drift.jar tmp/ s.csv

test: drift.jar
	java -classpath drift.jar SynthFrame

s.csv: drift.jar
	java -classpath drift.jar SynthFrame > s.csv


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

s.csv: drift.jar
	java -classpath drift.jar SynthFrame > s.csv

display: s.csv
	 stilts plot2plane auxmap=sron \
                           layer1=mark shading1=aux \
                           in1=s.csv ifmt1=csv x1=index y1=z aux1=phase


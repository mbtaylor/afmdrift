
JSRC = *.java

build: drift.jar

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
	 stilts plot2plane xpix=1000 ypix=300 navaxes=x \
                           auxmap=sron \
                           layer1=mark shading1=aux \
                           in1=s.csv ifmt1=csv x1=index y1=z aux1=phase

test: drift.jar
	java -classpath drift.jar GridTest


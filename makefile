
JSRC = *.java

build: drift.jar

drift.jar: $(JSRC)
	rm -rf tmp
	mkdir -p tmp
	javac -Xlint:unchecked -d tmp $(JSRC) \
           && jar cf $@ -C tmp .
	rm -rf tmp

clean:
	rm -rf drift.jar tmp/ samples.csv pixels.csv

data: samples.csv pixels.csv

samples.csv pixels.csv: drift.jar
	java -classpath drift.jar Drifter

drift.csv: drift.jar
	java -classpath drift.jar Drifter >$@

display: samples.csv
	 stilts plot2plane xpix=1000 ypix=300 navaxes=x \
                           auxmap=sron \
                           in=samples.csv ifmt=csv x=t \
                           layer1=mark y1=z shading1=aux aux1=phase \
                           layer2=line y2=drift color2=black \

test: drift.jar
	java -classpath drift.jar GridTest


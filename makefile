
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
	java -ea -classpath drift.jar FitFrame

display: samples.csv
	 stilts plot2plane xpix=1000 ypix=300 navaxes=x \
                           auxmap=rainbow auxvisible=false \
                           legend=false grid=true \
                           in=samples.csv ifmt=csv x=t \
                           layer0=mark y0=surface size0=1 color0=grey \
                           layer1=mark y1=z shading1=aux aux1=phase \
                           layer2=line y2=drift color2=black \
                           layer3=line y3=out color3=green \

test: drift.jar
	java -ea -classpath drift.jar GridTest


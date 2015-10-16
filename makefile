.SUFFIXES: .csv .fits

JSRC = *.java

build: drift.jar

drift.jar: $(JSRC)
	rm -rf tmp
	mkdir -p tmp
	javac -Xlint:unchecked -d tmp $(JSRC) \
           && jar cf $@ -C tmp .
	rm -rf tmp

clean:
	rm -rf drift.jar tmp/ samples.{csv,fits} pixels.{csv,fits}

data: samples.csv pixels.csv

samples.csv pixels.csv: drift.jar
	java -ea -classpath drift.jar FitFrame

.csv.fits:
	stilts tpipe in=$< ifmt=csv out=$@ ofmt=fits

display: samples.fits
	 stilts plot2plane \
             xpix=1000 ypix=300 navaxes=x \
             auxmap=paired auxvisible=false \
             legend=true grid=true legpos=0.99,0.95 \
             in=samples.fits x=t shading=flat \
             layer1=mark y1=z shape1=cross shading1=aux aux1=phase \
                         leglabel1=measured \
             layer3=line y3=out color3=green leglabel3=corrected \
             layer2=line y2=drift color2=red leglabel2='fitted drift' \
             layer0=mark y0=surface size0=1 color0=grey \
                         leglabel0='true surface'\

test: drift.jar
	java -ea -classpath drift.jar GridTest


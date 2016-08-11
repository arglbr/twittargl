#!/bin/bash
cd /home/arglbr/IdeaProjects/SparkCassandra02/out/artifacts/sparkcassandra02_jar
mkdir tmp
cp *jar tmp/
cd tmp/
unzip sparkcassandra02.jar
rm -f sparkcassandra02.jar 
rm -rf META-INF
cd ..
rm -f sparkcassandra02.jar
cd tmp/
zip -r ../sparkcassandra02.jar *
cd ..
rm -rf tmp
scp sparkcassandra02.jar root@192.168.0.14:/root/


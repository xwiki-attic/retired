#!/bin/sh
java -classpath ".:../lib/commons-cli-1.1.jar:../lib/wikimodel.2.0.bin.jar:../lib/ws-commons-util-1.0.1.jar:../lib/xmlrpc-client-3.0.jar:../lib/xmlrpc-common-3.0.jar" org.xwiki.util.Texifier --wikiurl=$1 --username=$2 --password=$3 --pagelist-document=$4 --output=./output/main.tex
cd ./output
./make.sh
cd ..

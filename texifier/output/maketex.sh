#!/bin/sh
java -classpath "../bin:../lib/commons-cli-1.1.jar:../lib/org.wikimodel.wem-2.0.7-SNAPSHOT.jar:../lib/ws-commons-util-1.0.1.jar:../lib/xmlrpc-client-3.0.jar:../lib/xmlrpc-common-3.0.jar" org.xwiki.util.Texifier --wikiurl=http://emcdocwiki.xwiki.com/ --username=latex --password=ll2008 --pagelist-document=Report.PageList --output=./main.tex

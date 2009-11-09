From Stephane Lauriere (2008):

"
With Mikhail we created this generic GWT annotation tool that we may
consider integrating into XWiki since it's bringing handy annotation
features. The tool works fine only on static documents though for now.
Annotation on evolving documents will require additional work.

Project: http://code.google.com/p/adnotatio/
"

Change pom.xml and web.xml in the standard module then run mvn install -Pjettyrun,mysql
Access http://localhost:8080/xwiki/adnotatio.HtmlAnnotatorClient/HtmlAnnotatorService.html

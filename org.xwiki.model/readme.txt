This project is a first attempt to model the core XWiki data model. It consists of three
packages:

1) org.xwiki.model: business layer

This package comprises the classes Farm, Wiki, Space, Document, Session, Space, User, XWikiException. 
All these objects have a separate persistent representation available in the persistence layer. The classes contain methods 
for dealing with wiki entities: a wiki can create spaces, a Space can create documents etc. Later on, the Document class
could contain methods like these ones: String getXHTML(), String getTableOfContent(), Document[] getAllReferers() etc. 

A Wiki represents a "view" of an actual wiki as seen through the authorization settings of its associated Session 
(similarly to what happens with the "Workspace" class as defined by the JCR API). This avoids passing the Context
to each method.

2) org.xwiki.model.dao: persistence layer. 

This package follows the Data Access Object pattern [1], which provides a technique for separating object persistence and 
data access logic from any particular persistence mechanism. The IWikiDao interface defines the essential methods for 
loading and storing wikis, spaces, documents and users from the database.  

[1] DAO: http://www.codefutures.com/data-access-object/
 
3) org.xwiki.model.dao.memory:

This package provides an in-memory implementation of the persistence layer.

This code was created based on the input provided by Vincent, Sergiu & al. on the xwiki-dev mailing-list in the thread 
about XWiki v2 architecture and the code itself was written by by Mikhail (mkotelnikov@cogniumsystems.com) 
and Stéphane (slauriere@xwiki.com). 
 

 
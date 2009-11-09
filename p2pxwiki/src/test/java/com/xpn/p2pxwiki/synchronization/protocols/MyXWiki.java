package com.xpn.p2pxwiki.synchronization.protocols;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

public class MyXWiki extends XWiki {

	private XWikiDocument doc;
	private static MyXWiki instance;
	private boolean saved = false;

	public MyXWiki(XWikiConfig config, XWikiContext context) throws XWikiException {
		super(config, context);
		this.doc = new XWikiDocument("Main","WebHome");
		instance = this;
	}

	public XWikiDocument getDocument(String fullname, XWikiContext context) throws XWikiException {
	    return new XWikiDocument() {
	    	public XWikiDocument getTranslatedDocument(String language, XWikiContext context) throws XWikiException {
	    	    return doc;
	    	}
	    };
	}

	public static MyXWiki getInstance() {
    	return instance;
    }

	public XWikiDocument getDoc() {
    	return doc;
    }
	
	public void saveDocument(XWikiDocument newDoc, XWikiDocument olddoc, XWikiContext context) throws XWikiException {
		if (olddoc == this.doc) {
			saved = true;
		}
	}

	public boolean isSaved() {
    	return saved;
    }
}

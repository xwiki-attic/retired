package com.xpn.p2pxwiki.examples;

import com.xpn.xwiki.XWikiContext;

public class ClientBaseExamples {
	String peerAddress; 
	XWikiContext context = null;

	public void initJXTA()  { 
		peerAddress = "http://testclient.p2pxwiki.com"; 
		if (context == null) { 
			context = new XWikiContext(); 
		} 
	}
}

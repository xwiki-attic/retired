package com.xpn.p2pxwiki.synchronization.protocols;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.ConnectionFactory;
import com.xpn.p2pxwiki.communication.HandlerStub;
import com.xpn.p2pxwiki.communication.Network;
import com.xpn.p2pxwiki.mocks.MockConnectionFactory;
import com.xpn.p2pxwiki.mocks.MockHandlerStub;
import com.xpn.p2pxwiki.mocks.MockNetwork;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

public class MyNetwork extends MockNetwork implements Network {

	public MyNetwork() throws P2PXWikiException {
	    super();
    }
	
	protected ConnectionFactory createConnectionFactory(Network network) throws P2PXWikiException {
	    return new MockConnectionFactory(network) {
	    	public HandlerStub getConnection(String peer) throws P2PXWikiException {
	    	    return new MockHandlerStub(peer, this) {
	    	    	public Object execute(String function, Object[] params) throws P2PXWikiException {
	    	    	    if (function.equals("synchronization.sync")) {
	    	    	    	return new Object[] {"1", "2"};
	    	    	    } else if (function.equals("synchronization.merge")) {
	    	    	    	try {
	    	    	    		return new Object[] {(new XWikiDocument()).toXML(new XWikiContext())};
	    	    	    	} catch (XWikiException e) {
	    	    	    		throw new P2PXWikiException(0, e);
	    	    	    	}
	    	    	    } else {
	    	    	    	throw new P2PXWikiException(0, "Mock not that smart");
	    	    	    }
	    	    	}
	    	    };
	    	}	    	
	    };
	}
}

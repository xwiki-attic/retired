package com.xpn.p2pxwiki.mocks;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.AbstractHandlerStub;
import com.xpn.p2pxwiki.communication.ConnectionFactory;
import com.xpn.p2pxwiki.communication.HandlerStub;

public class MockHandlerStub extends AbstractHandlerStub implements HandlerStub {
	private String peerName;
	private ConnectionFactory factory;
	private boolean closed = false;

	public MockHandlerStub(String peerName, ConnectionFactory factory) {
		this.peerName = peerName;
		this.factory = factory;
	}
	
	public void close() throws P2PXWikiException {
		closed = true;
	}

	public Object execute(String function, Object[] params)
	        throws P2PXWikiException {
		return null;
	}

	public String getPeerName() {
		return peerName;
	}

	public ConnectionFactory getConnectionFactory() {
	    return factory;
    }

	public boolean isClosed() {
    	return closed;
    }
}

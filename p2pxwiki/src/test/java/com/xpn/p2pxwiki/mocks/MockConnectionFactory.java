package com.xpn.p2pxwiki.mocks;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.ConnectionFactory;
import com.xpn.p2pxwiki.communication.HandlerStub;
import com.xpn.p2pxwiki.communication.Network;

public class MockConnectionFactory implements ConnectionFactory {
	private static MockConnectionFactory instance = null;
	private Network network;

	public MockConnectionFactory(Network network) throws P2PXWikiException {
		this.network = network;
		instance = this;
	}
	
	public static MockConnectionFactory getInstance() {
		return instance;
	}
	
	public static void resetInstance() {
		instance = null;
	}
	
	public HandlerStub getConnection(String peer) throws P2PXWikiException {
		return new MockHandlerStub(peer, this);
	}
	
	public void closeConnection(HandlerStub stub) throws P2PXWikiException {
	    ((MockHandlerStub)stub).close();
	}

	public Network getNetwork() {
	    return network;
    }
}

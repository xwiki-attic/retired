package com.xpn.p2pxwiki.mocks;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.ConnectionFactory;
import com.xpn.p2pxwiki.communication.Network;

public class MockNetwork implements Network {
	private static MockNetwork instance = null;
	private String localName;
	private ConnectionFactory factory;
	private int serverPort;
	private String peerName;
	
	public MockNetwork() throws P2PXWikiException {
		this.factory = createConnectionFactory(this);
		this.serverPort = 0;
		this.peerName = "unnamed";
		this.localName = "";
		instance = this;
	}
	
	protected ConnectionFactory createConnectionFactory(Network network) throws P2PXWikiException {
		return new MockConnectionFactory(network);
	}
	
	public static MockNetwork getInstance() {
		return instance;
	}
	
	public static void resetInstance() {
		instance = null;
	}
	
	public void setLocalName(String localName) {
		this.localName = localName;
	}
	
	public String getCanonicalLocalName() {
		return localName;
	}

	public String getCanonicalRemoteName(String peer) {
		return peer;
	}

	public String getPeerName() {
	    return peerName;
    }

	public int getServerPort() {
	    return serverPort;
    }

	public void setServerPort(int serverPort) {
    	this.serverPort = serverPort;
    }


	public void setPeerName(String peerName) {
    	this.peerName = peerName;
    }


	public String getLocalName() {
    	return localName;
    }


	public ConnectionFactory getConnectionFactory() {
	    return factory;
    }

	public void cleanup() throws P2PXWikiException {
	    // do nothing
    }

	public void init() throws P2PXWikiException {
	    // do nothing
    }
}

package com.xpn.p2pxwiki.communication;

import com.xpn.p2pxwiki.communication.jxtarpc.JxtaRpcConnectionFactory;
import com.xpn.p2pxwiki.communication.jxtarpc.JxtaRpcNetwork;

public class JxtaRpcLocalTest extends AbstractLocalTest {

	public JxtaRpcLocalTest(String arg0) {
		super(arg0);
	}

	public String getConnectionFactory() {
		return JxtaRpcConnectionFactory.class.getName();
	}

	public String getLocalPeer() {
		return "localPeer";
	}

	public String getRemotePeer() {
		return "localPeer";
	}
	
	public String getNetwork() {
		return JxtaRpcNetwork.class.getName();
	}
}

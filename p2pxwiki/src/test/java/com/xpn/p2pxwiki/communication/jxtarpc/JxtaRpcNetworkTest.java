package com.xpn.p2pxwiki.communication.jxtarpc;

import com.xpn.p2pxwiki.communication.jxtarpc.JxtaRpcNetwork;
import com.xpn.p2pxwiki.communication.xmlrpc.XmlRpcNetworkTest;

public class JxtaRpcNetworkTest extends XmlRpcNetworkTest {

	protected String getNetworkType() {
		return JxtaRpcNetwork.class.getName();
	}
	
	protected String getPeerName() {
		return "bubulici";
	}
	
	public void testGetCanonicalLocalName() {
		assertEquals("http://www.bubulici.peer:5555", network.getCanonicalLocalName());
	}
	
	public void testGetCanonicalRemoteName() {
		assertEquals("http://www.bubulina.peer:5555", network.getCanonicalRemoteName("bubulina"));
	}
}

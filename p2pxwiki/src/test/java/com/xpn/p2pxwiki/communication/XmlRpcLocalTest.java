package com.xpn.p2pxwiki.communication;

import com.xpn.p2pxwiki.communication.xmlrpc.XmlRpcConnectionFactory;
import com.xpn.p2pxwiki.communication.xmlrpc.XmlRpcNetwork;

public class XmlRpcLocalTest extends AbstractLocalTest {

	public XmlRpcLocalTest(String arg0) {
		super(arg0);
	}

	public String getConnectionFactory() {
		return XmlRpcConnectionFactory.class.getName();
	}

	public String getLocalPeer() {
		return "127.0.0.1";
	}

    public String getRemotePeer() {
	    return "127.0.0.1";
    }
    
    public String getNetwork() {
		return XmlRpcNetwork.class.getName();
	}
}

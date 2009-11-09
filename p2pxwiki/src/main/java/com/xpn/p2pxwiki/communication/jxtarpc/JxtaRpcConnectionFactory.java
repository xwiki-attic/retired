package com.xpn.p2pxwiki.communication.jxtarpc;

import org.apache.xmlrpc.client.P2PXmlRpcLiteHttpTransportFactory;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.ConnectionFactory;
import com.xpn.p2pxwiki.communication.Network;
import com.xpn.p2pxwiki.communication.xmlrpc.XmlRpcConnectionFactory;

public class JxtaRpcConnectionFactory extends XmlRpcConnectionFactory implements ConnectionFactory {

	public JxtaRpcConnectionFactory(Network network) throws P2PXWikiException {
		super(network);
	}

    protected String defaultTransportType() {
	    return P2PXmlRpcLiteHttpTransportFactory.class.getName();
    }
}

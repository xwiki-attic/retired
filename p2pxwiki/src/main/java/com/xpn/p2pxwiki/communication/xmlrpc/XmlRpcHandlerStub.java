package com.xpn.p2pxwiki.communication.xmlrpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.AbstractHandlerStub;
import com.xpn.p2pxwiki.communication.ConnectionFactory;
import com.xpn.p2pxwiki.communication.HandlerStub;

public class XmlRpcHandlerStub extends AbstractHandlerStub implements HandlerStub {
	protected static Log log = LogFactory.getFactory().getInstance(XmlRpcHandlerStub.class);
	
	private String peer;
	private XmlRpcClient client;
	private XmlRpcConnectionFactory factory;

	public XmlRpcHandlerStub(String peer, XmlRpcClient client,
			XmlRpcConnectionFactory factory) throws P2PXWikiException {
		this.peer = peer;
		this.factory = factory;
		this.client = client;
    }

	public Object execute(String function, Object[] params) throws P2PXWikiException {
		try {
			return client.execute(function, params);
		} catch (XmlRpcException e) {
			throw new P2PXWikiException(P2PXWikiException.REMOTE_CALL, e);
		}
	}
	
	public String getPeerName() {
	    return peer;
	}

	public ConnectionFactory getConnectionFactory() {
	    return factory;
    }
}

package com.xpn.p2pxwiki.communication.xmlrpc;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.AbstractConnectionFactory;
import com.xpn.p2pxwiki.communication.ConnectionFactory;
import com.xpn.p2pxwiki.communication.HandlerStub;
import com.xpn.p2pxwiki.communication.Network;
import com.xpn.p2pxwiki.utils.P2PUtil;

public class XmlRpcConnectionFactory extends AbstractConnectionFactory implements ConnectionFactory {
	private static Log log = LogFactory.getFactory().getInstance(XmlRpcConnectionFactory.class);
	
	private XmlRpcClientConfigImpl config;

	public XmlRpcConnectionFactory(Network network) throws P2PXWikiException {
		super(network);
		config = new XmlRpcClientConfigImpl();
		// TODO get properties and add them to the config
	}
	
	// TODO make transport type customizable via properties
	protected String defaultTransportType() {
		return XmlRpcCommonsTransportFactory.class.getName();
	}

    public HandlerStub openConnection(String peer) throws P2PXWikiException {
		try {
			log.debug("getting xmlrpc connection");
			
			URL url = new URL(getNetwork().getCanonicalRemoteName(peer));
			config.setServerURL(url);
			
			XmlRpcClient client = new XmlRpcClient();
			XmlRpcTransportFactory transport = (XmlRpcTransportFactory)
					P2PUtil.getInstance(defaultTransportType(),
					new Object[] {client}, new Class[] {XmlRpcClient.class});

			client.setTransportFactory(transport);
			client.setConfig(config);
			return new XmlRpcHandlerStub(peer, client, this);
		} catch (MalformedURLException e) {
			throw new P2PXWikiException(0, e);
		}
    }
}

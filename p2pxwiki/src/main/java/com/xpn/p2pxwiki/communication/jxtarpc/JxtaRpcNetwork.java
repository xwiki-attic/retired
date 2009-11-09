package com.xpn.p2pxwiki.communication.jxtarpc;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.webserver.P2PServletWebServer;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.p2psockets.P2PNetwork;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.Network;
import com.xpn.p2pxwiki.communication.xmlrpc.XmlRpcNetwork;

public class JxtaRpcNetwork extends XmlRpcNetwork implements Network {
	private static final Log log = LogFactory.getFactory().getInstance(JxtaRpcNetwork.class);
	private static final String NETWORK_NAME = "JXTA:P2PXWikiNetwork";
	
	public JxtaRpcNetwork() throws P2PXWikiException {
		log.debug("constructor");
	}
	
	public void init() throws P2PXWikiException {
		log.debug("init");
		try {
			P2PNetwork.autoSignin(getPeerName(), NETWORK_NAME);
			log.debug("Signed in into the JXTA network with name " + getPeerName());
		}
		catch (Exception ex) {
			log.error("Signin error", ex);
			throw new P2PXWikiException(P2PXWikiException.INIT_JXTA_NETWORK, ex);
		}
		super.init();
	}
	
	protected ServletWebServer createServletWebServer(XmlRpcServlet servlet)
			throws ServletException, IOException {
		log.debug("Creating P2PServletWebServer at port:"+getServerPort());
		ServletWebServer newServer = new P2PServletWebServer(servlet, getServerPort());
		log.debug("Starting P2PServletWebServer");
		newServer.start();
		log.debug("P2PServletWebServer running");
		return newServer;
	}
	
    public String defaultConnectorFactory() {
	    return JxtaRpcConnectionFactory.class.getName();
    }

    public int defaultServerPort() {
	    return 8081;
    }

	// TODO This could be written differently - http://p2psockets.jxta.org/docs/tutorials/1.html (The Loopback)
	public String getCanonicalLocalName() {
		return "http://www." + getPeerName() + ".peer:" + getServerPort();
	}

	// TODO This could be written differently - http://p2psockets.jxta.org/docs/tutorials/1.html
	public String getCanonicalRemoteName(String peer) {
		return "http://www." + peer + ".peer:" + getServerPort();
	}
}

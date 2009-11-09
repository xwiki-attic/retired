package com.xpn.p2pxwiki.communication.xmlrpc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.AbstractNetwork;
import com.xpn.p2pxwiki.communication.Network;
import com.xpn.xwiki.xmlrpc.XWikiXmlRpcServlet;

public class XmlRpcNetwork extends AbstractNetwork implements Network {
	private static Log log = LogFactory.getFactory().getInstance(XmlRpcNetwork.class);
	protected ServletWebServer webServer;
	protected XmlRpcServlet servlet;

	public XmlRpcNetwork() throws P2PXWikiException {
		log.debug("constructor");
	}
	
	/**
	 * test setting - creates it's own web server
	 */
	public void init() throws P2PXWikiException {
		try {
			log.debug("Creating new P2PXWikiXmlRpcServlet");
			servlet = new XWikiXmlRpcServlet();
			// TODO this does not work with ServletWebServer
			// servlet.getServletContext().setAttribute("enabledForExtensions", "true"); 
			webServer = createServletWebServer(servlet);
		}
		catch (ServletException e) {
			throw new P2PXWikiException(P2PXWikiException.INIT_JXTA_NETWORK, e);
		}
		catch (IOException e) {
			throw new P2PXWikiException(P2PXWikiException.INIT_JXTA_NETWORK, e);			
		}
	}
	
	protected ServletWebServer createServletWebServer(XmlRpcServlet servlet)
			throws ServletException, IOException {
		log.debug("Creating ServletWebServer at port:"+getServerPort());
		ServletWebServer newServer = new ServletWebServer(servlet, getServerPort());
		log.debug("Starting ServletWebServer");
		newServer.start();
		log.debug("ServletWebServer running");
		return newServer;
	}
	
	/**
	 * test setting - uses it's own web server
	 */
	public void cleanup() throws P2PXWikiException {
		log.debug("Shuting down ServletWebServer");	
		webServer.shutdown();
		log.debug("ServletWebServer shut down");	
    }

	// TODO add path to servlet
	public String getCanonicalLocalName() {
		try{
			return "http://" + InetAddress.getLocalHost().getCanonicalHostName() + ":" + getServerPort();
		}
		catch(UnknownHostException ex){
			return "http://" + getPeerName() + ".xwiki.com:" + getServerPort();
		}
	}

	// TODO add path to servlet 
	public String getCanonicalRemoteName(String peer) {
		return "http://" + peer + ":" + getServerPort();
	}

    public String defaultConnectorFactory() {
	    return XmlRpcConnectionFactory.class.getName();
    }

    public int defaultServerPort() {
	    return 9083;
    }
}

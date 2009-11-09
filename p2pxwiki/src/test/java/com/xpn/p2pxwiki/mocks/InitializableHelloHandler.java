package com.xpn.p2pxwiki.mocks;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.xwiki.xmlrpc.InitializableHandler;


public class InitializableHelloHandler implements InitializableHandler, Hello {
	private static final Log log = LogFactory.getFactory().getInstance(InitializableHelloHandler.class);
	private boolean initialized = false;

	public InitializableHelloHandler() {
		log.debug("c'tor");
	}
	
	public void init(Servlet servlet) throws P2PXWikiException {
		initialized = true;
		log.debug("init");
		log.debug("info: "+servlet.getServletInfo());
		ServletConfig cfg = servlet.getServletConfig();
		log.debug("config: "+cfg);
		log.debug("xmlrpc_server: "+((XmlRpcServlet)servlet).getXmlRpcServletServer());
		if (cfg != null) {
			log.debug("cfg.init_parameters: "+cfg.getInitParameterNames());
			log.debug("cfg.servlet_name: "+cfg.getServletName());
			try {
				log.debug("cfg.context: "+cfg.getServletContext());
			} catch (Throwable dog) {
				log.debug("cfg.getServletContext() threw "+dog);
			}
		}
	}

	public boolean isInitialized() {
		log.debug("initialized: "+initialized);
    	return initialized;
    }

	public String hello(String world) {
		log.debug("hello: "+world);
		return "hello "+world; 
    }
}

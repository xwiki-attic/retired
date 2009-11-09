/*
 * Copyright 2006, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * Allows initilization of OSCache in the proper way to use JGroups for
 * clustering. Basically this involves reading the oscache.properties file
 * located in WEB-INF/classes. This contains a listener setting for JGroups (and
 * other settings we could use if desired).
 * 

 * @author sdumitriu
 * 
 */

package com.xpn.p2pxwiki.communication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.xmlrpc.XmlRpcNetwork;
import com.xpn.p2pxwiki.utils.P2PUtil;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

/**
 * A Connector has the role of hidding the inter-peer communication technology from
 * the main peer code, by acting as an intermediator. 
 *
 * @author sdumitriu
 */
public class ConnectorPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface{
//	 TODO: XWikiProperties dynamic network type switcher + PropertyChangedNotifier?
	public static final String PLUGIN_NAME = "p2pxwikiconnector";
	public static final String NETWORK_TYPE = "p2pxwiki.connector.network";
	public static final String RETRY_WITH_DEFAULT_NETWORK = "p2pxwiki.connector.retry_with_default_network";
	public static final String DEFAULT_NETWORK_TYPE = XmlRpcNetwork.class.getName();

	private static final Log log = LogFactory.getFactory().getInstance(ConnectorPlugin.class);
	private Network network;
	private XWiki xwiki;
	protected static ConnectorPlugin instance; 
	
	public static ConnectorPlugin getInstance() {
		return instance;
	}

	/**
	 * Obtain the name by which this plugin can be retrieved.
	 * @return The plugin name.
	 */
	public String getName() {
		return PLUGIN_NAME;
	}

	/**
	 * Standard XWiki plugin constructor, called by the plugin manager at startup.
	 * @param name The standard name guessed by the plugin manager.
	 * @param className The class name as found in the xwiki.cfg file.
	 * @param context The XWikiContext available when plugins are initialized.
	 */
	public ConnectorPlugin(String name, String className, XWikiContext context) {
		super(name, className, context);
		log.debug("constructor");
		instance = this;
		init(context);
	}

	/**
	 * Initializes the plugin.
	 * @param context The XWikiContext available when plugins are initialized.
	 */
	public void init(XWikiContext context) {
		log.debug("constructor");
		super.init(context);
		xwiki = context.getWiki();

		log.debug("Configuring network type");
		String networkType = xwiki.Param(NETWORK_TYPE, DEFAULT_NETWORK_TYPE);
		try {
			network = (Network)P2PUtil.getInstance(networkType);
			network.init();
			log.debug("Obtained network: " + network.getClass().getCanonicalName());
		} catch(P2PXWikiException ex) {
			log.error("Cannot load network", ex);
			ex.printStackTrace();	
			if (!networkType.equals(DEFAULT_NETWORK_TYPE) &&
				xwiki.Param(RETRY_WITH_DEFAULT_NETWORK, "false") == "true") {
				log.debug("Retrying with default network type");
				try {
					network = (Network)P2PUtil.getInstance(DEFAULT_NETWORK_TYPE);
					network.init();
					log.debug("Obtained network: " + network.getClass().getCanonicalName());
				} catch(P2PXWikiException ex2) {						
					log.error("Cannot load default network", ex2);
					ex2.printStackTrace();
					// we continue and hope for the best
				}
			}
		}
	}

	/**
	 * Returns a stub through which a remote handler can be accessed.
	 * @param peer The name of the peer to access.
	 * @return The handler stub for this peer.
	 * @throws XWikiException If no connection could be established.
	 */
	public HandlerStub getConnection(String peer) throws P2PXWikiException {
		return network.getConnectionFactory().getConnection(peer);
	}

	/**
	 * Closes a connection to a remote host. This should be called only when
	 * there will be no need for another communication with that host, as a
	 * connection can be reused many times.  
	 * @param stub The connection being closed.
	 * @throws XWikiException When the connection could not be closed.
	 */
	void closeConnection(HandlerStub stub) throws P2PXWikiException {
		network.getConnectionFactory().closeConnection(stub);
	}
	
	public String getCanonicalLocalName() {
		return network.getCanonicalLocalName();
	}
	
	public String getCanonicalRemoteName(String peer) {
		return network.getCanonicalRemoteName(peer);
	}

	public String getPeerName() {
	    return network.getPeerName();
    }

	/**
	 * Returns an API for using this plugin from scripts.
	 * @see com.xpn.xwiki.plugin.XWikiPluginInterface#getPluginApi(com.xpn.xwiki.plugin.XWikiPluginInterface, com.xpn.xwiki.XWikiContext) Overridden method.
	 * @return Scripting API for this plugin.
	 */
	public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
		if(!this.equals(plugin)){
			log.warn("getPluginApi: different objects! (plugin != this)");
		}
		return new ConnectorApi(context, this);
	}

	public XWiki getXWiki() {
    	return xwiki;
    }
	
	/**
	 * Useful for testing. Package access. 
	 * @throws P2PXWikiException 
	 */
	void cleanup() throws P2PXWikiException {
		network.cleanup();
		network = null;
	}
}

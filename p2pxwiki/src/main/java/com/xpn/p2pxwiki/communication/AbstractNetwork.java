package com.xpn.p2pxwiki.communication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.utils.P2PUtil;
import com.xpn.xwiki.XWiki;

public abstract class AbstractNetwork implements Network {
	public static final String CONNECTOR_FACTORY = "p2pxwiki.network.connector_factory";
	public static final String SERVER_PORT = "p2pxwiki.network.server_port";
	public static final String PEER_NAME = "p2pxwiki.network.peer_name";
	public static final String RETRY_WITH_DEFAULT_CONNECTOR_FACTORY
			= "p2pxwiki.network.retry_with_default_connector_factory";

	private static final Log log = LogFactory.getFactory().getInstance(AbstractNetwork.class);
	
	private String peerName;
	private int serverPort;
	private ConnectionFactory factory;

	public abstract String getCanonicalLocalName();
	public abstract String getCanonicalRemoteName(String peer);
	public abstract String defaultConnectorFactory();
	public abstract int defaultServerPort();

	public AbstractNetwork() throws P2PXWikiException {
		XWiki xwiki = ConnectorPlugin.getInstance().getXWiki();
		
		log.debug("Configuring peer name");
		peerName = xwiki.Param(PEER_NAME);
		log.debug("Peer name: " + peerName);
		log.debug("Configuring server port");
		serverPort = (int)xwiki.ParamAsLong(SERVER_PORT, defaultServerPort());
		log.debug("Configured server port: " + serverPort);
		
		log.debug("Configuring connector factory");
		String connectorType = xwiki.Param(CONNECTOR_FACTORY, defaultConnectorFactory());
		try {
			factory = (ConnectionFactory)P2PUtil.getInstance(connectorType,
					new Object[] {this}, new Class[] {Network.class});
		} catch(P2PXWikiException ex){
			log.error("Cannot load connector factory", ex);
			if (connectorType != defaultConnectorFactory() &&
					xwiki.Param(RETRY_WITH_DEFAULT_CONNECTOR_FACTORY, "false") == "true") {
				log.debug("Retrying with default connector factory");
				factory = (ConnectionFactory)P2PUtil.getInstance(defaultConnectorFactory(),
						new Object[] {this}, new Class[] {Network.class});
			} else {
				throw ex;
			}
		}
		log.debug("Obtained factory: " + factory.getClass().getCanonicalName());
	}
	
	public ConnectionFactory getConnectionFactory() {
	    return factory;
    }

	/**
	 * Returns the network name of the local host.
	 * @return The peerName.
	 */
	public String getPeerName() {
		return peerName;
	}

	/**
	 * @return the serverPort
	 */
	public int getServerPort() {
		return serverPort;
	}

// Cannot change server port at runtime ... it is fixed
//	/**
//	 * @param serverPort the serverPort to set
//	 */
//	public void setServerPort(int serverPort) {
//		this.serverPort = serverPort;
//	}
}

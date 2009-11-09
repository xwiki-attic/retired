package com.xpn.p2pxwiki.communication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.cache.api.XWikiCache;
import com.xpn.xwiki.cache.api.XWikiCacheNeedsRefreshException;

public abstract class AbstractConnectionFactory implements ConnectionFactory {	
	public static final String CACHETIME = "p2pxwiki.connector.cachetime";
	public static final int    DEFAULT_CACHETIME = -1;
	
	private static final Log log = LogFactory.getFactory().getInstance(AbstractConnectionFactory.class);
	private Network network;
	private XWikiCache cache;
	private int cacheTime;
	private XWiki xwiki;
	
	public abstract HandlerStub openConnection(String peer) throws P2PXWikiException;

	public AbstractConnectionFactory(Network network) {
		this.network = network;
		xwiki = ConnectorPlugin.getInstance().getXWiki();
		log.debug("Creating connection cache");
		try{
			cache = xwiki.getCacheService().newLocalCache();
		}
		catch(XWikiException ex){
			// TODO: Handle this better, e.g. use a WeakHashMap
			log.error("Cannot create cache!", ex);
		}
		
		log.debug("Configuring cache time");
		cacheTime = (int)xwiki.ParamAsLong(CACHETIME, DEFAULT_CACHETIME);
		log.debug("Configured cache time: " + cacheTime);
	}
	
	public Network getNetwork() {
		return network;
	}

	/**
	 * Usually you don't need to override this and just implement openConnection.
	 * @see com.xpn.p2pxwiki.communication.ConnectionFactory#getConnection(java.lang.String)
	 */
	public HandlerStub getConnection(String peer) throws P2PXWikiException {
		log.debug("Get connection for " + peer);
		HandlerStub connection = null;
		boolean updated = false;
		if (cache != null){
			try{
				connection = (HandlerStub)cache.getFromCache(peer, cacheTime);
				if(connection == null){
					log.debug("Connection not found in cache; creating a new connection");
					connection = openConnection(peer);
					cache.putInCache(peer, connection);
				}
				else{
					log.debug("Connection found in cache");
				}
			}
			catch(XWikiCacheNeedsRefreshException ex){
				log.debug("Expired connection found in cache; creating a new connection");
				try {
					connection = openConnection(peer);
					cache.putInCache(peer, connection);
					updated = true;
				} finally {
					if (!updated) {
						cache.cancelUpdate(peer);
					}
				}
			}
		}
		else{
			connection = openConnection(peer);
		}
		return connection;
	}

	/**
	 * Might need to override this to actually close the stub.
	 * In that case remember to call parent closeConnection first.
	 */
	public void closeConnection(HandlerStub stub) throws P2PXWikiException {
		log.debug("Closing connection for "+stub.getPeerName());
		cache.flushEntry(stub.getPeerName());
		log.debug("Connection closed for "+stub.getPeerName());
	}
}

package com.xpn.p2pxwiki.communication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;

public class ConnectorApi extends Api{
	private static final Log log = LogFactory.getFactory().getInstance(ConnectorApi.class);
	private ConnectorPlugin plugin;

	public ConnectorApi(XWikiContext context, ConnectorPlugin plugin) {
		super(context);
		this.context = context;
		this.plugin = plugin;
	}
	
	public HandlerStub getConnection(String peer){
		try{
			return plugin.getConnection(peer);
		}
		catch(XWikiException ex){
			log.error(ex);
			return null;
		}
	}
	
	public void closeConnection(HandlerStub stub){
		try{
			plugin.closeConnection(stub);
		}
		catch(XWikiException ex){
			log.error(ex);
		}
	}
	
	public String getCanonicalLocalName() {
		return plugin.getCanonicalLocalName();
	}
	
	public String getCanonicalRemoteName(String peer) {
		return plugin.getCanonicalRemoteName(peer);
	}

	public String getPeerName() {
	    return plugin.getPeerName();
    }
}

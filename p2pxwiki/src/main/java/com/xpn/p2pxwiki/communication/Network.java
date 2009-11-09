package com.xpn.p2pxwiki.communication;

import com.xpn.p2pxwiki.P2PXWikiException;

/**
 * 
 */
public interface Network {
	public void init() throws P2PXWikiException;	
	public void cleanup() throws P2PXWikiException;	
	public String getCanonicalLocalName();
	public String getCanonicalRemoteName(String peer);
	public String getPeerName();
	public int getServerPort();
	public ConnectionFactory getConnectionFactory();
}

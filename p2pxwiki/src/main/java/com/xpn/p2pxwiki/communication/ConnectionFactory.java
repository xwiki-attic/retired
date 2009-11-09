package com.xpn.p2pxwiki.communication;

import com.xpn.p2pxwiki.P2PXWikiException;

public interface ConnectionFactory {
	public Network getNetwork();
	public HandlerStub getConnection(String peer) throws P2PXWikiException;
	public void closeConnection(HandlerStub stub) throws P2PXWikiException;
}

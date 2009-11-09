package com.xpn.p2pxwiki.communication;


import com.xpn.p2pxwiki.P2PXWikiException;

public interface HandlerStub {
	public String getPeerName();
	public Object execute(String function, Object[] params) throws P2PXWikiException;
	public Object getDynamicProxy(String handlerName, Class interf, boolean localObjectMethods);
}

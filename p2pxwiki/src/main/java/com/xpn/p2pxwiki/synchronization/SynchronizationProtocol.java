package com.xpn.p2pxwiki.synchronization;

import com.xpn.xwiki.XWikiContext;

public interface SynchronizationProtocol {
	void processRequest(SynchronizationRequest request);
	void init(XWikiContext context);
}

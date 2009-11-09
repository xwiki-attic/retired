package com.xpn.p2pxwiki.synchronization;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;

public interface SynchronizationPolicy {
	boolean isUpdateNeeded(XWikiDocument doc, String action, XWikiContext context);
	SynchronizationRequest getSyncDocuments(XWikiDocument doc, String action, XWikiContext context);
	boolean isAsynchronous(XWikiDocument doc, String action, XWikiContext context);
	void updateContext(SynchronizationRequest elements, XWikiDocument doc, String action, XWikiContext context);
}

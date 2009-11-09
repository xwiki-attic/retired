package com.xpn.p2pxwiki.services;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;

public class P2PServicesApi extends Api {
	public P2PServicesApi(XWikiContext context, P2PServicesPlugin plugin) {
		super(context);
		this.context = context;
	}

	public WikiGroupServices getGroupServices() {
		return P2PServicesPlugin.getGroupServices();
	}

	public P2PServer getP2PServerServices() {
		return P2PServicesPlugin.getP2PServerServices();
	}
}

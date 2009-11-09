package com.xpn.p2pxwiki.registry;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;

public class RegistryClientApi extends Api {
	private RegistryClient regClient = null;

	public RegistryClientApi(XWikiContext context) {
		super(context);
		regClient = new RegistryClient(context);
	}

	/* public apis which can be accessed through a webpage */
	public boolean existsWikiapp(String wikiapp) {
		return regClient.existsWikiapp(wikiapp);
	}

	public boolean updateRegistry(String token, String wikiapp, String IP) {
		return regClient.updateRegistry(token, wikiapp, IP);
	}

	public String queryRegistry(String wikiname) {
		return regClient.queryRegistry(wikiname);
	}

	/* TODO: add apis to create a user and create a wiki */

	public String login(String username, String password) {
		return regClient.login(username, password);
	}

	public boolean logout(String token) {
		return regClient.logout(token);
	}
}

package com.xpn.p2pxwiki.client;

import com.xpn.p2pxwiki.communication.ConnectorPlugin;
import com.xpn.p2pxwiki.communication.HandlerStub;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;

public class BaseClient {
	protected XWikiContext context;
	public BaseClient(XWikiContext context){
		this.context = context;
	}

	public HandlerStub getConnection(String peer){
		try{
			return ((ConnectorPlugin)context.getWiki().getPlugin("p2pxwikiconnector", this.context)).getConnection(peer);
		}
		catch(XWikiException ex){
			return null;
		}
	}

	public String login(String username, String password, String wikiserver,
			String serverHandlerClass) {
		HandlerStub client = getConnection(wikiserver);
		try {
			String database = ""; /* default database to use on the server */
			String result = (String) client.execute(serverHandlerClass
					+ ".login", new Object[] {username, password, database});
			if (result.length() == 0){
				return null;
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean logout(String token, String wikiserver, String serverHandlerClass) {
		HandlerStub client = getConnection(wikiserver);
		try {
			String database = ""; /* default database to use on the server */
			Boolean result = (Boolean) client.execute(serverHandlerClass
					+ ".logout", new Object[] {token, database});
			return result.booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}

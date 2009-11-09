package com.xpn.p2pxwiki.rpc;

import java.util.Hashtable;
import java.util.Vector;

import com.xpn.p2pxwiki.client.BaseClient;
import com.xpn.p2pxwiki.communication.HandlerStub;
import com.xpn.xwiki.XWikiContext;

/* This class always contacts the wikiserver to fetch the content. caching can be done to serve the client 
 * faster. 
 * 
 * All the calls in this class have an extra argument taking the wikiserver address on which to make the call.
 */
public class RpcClient extends BaseClient {
	private final static String HANDLERCLASS = "xwikirpc";

	public RpcClient(XWikiContext context){
		super(context);
	}

	/*
	 * @EXTENSION: Change from plain text password transmission on the network
	 * 
	 * login to a wikiserver with a username and password. get a token in return
	 */
	public String login(String username, String password, String wikiserver) {
		return super.login(username, password, wikiserver, HANDLERCLASS);
	}

	public boolean logout(String token, String wikiserver) {
		return super.logout(token, wikiserver, HANDLERCLASS);
	}

	public Vector getSpaces(String token, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Vector result = (Vector) client.execute(
					HANDLERCLASS + ".getSpaces", new Object[] {token});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Hashtable getSpace(String token, String spaceKey, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Hashtable result = (Hashtable) client.execute(HANDLERCLASS
					+ ".getSpace", new Object[] {token, spaceKey});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Vector getPages(String token, String spaceKey, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Vector result = (Vector) client.execute(HANDLERCLASS + ".getPages",
					new Object[] {token, spaceKey});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Hashtable getPage(String token, String pageId, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Hashtable result = (Hashtable) client.execute(HANDLERCLASS
					+ ".getPage", new Object[] {token, pageId});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Vector getPageHistory(String token, String pageId, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Vector result = (Vector) client.execute(HANDLERCLASS
					+ ".getPageHistory", new Object[] {token, pageId});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Vector search(String token, String query, int maxResults,
			String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Vector result = (Vector) client.execute(HANDLERCLASS + ".search",
					new Object[] {token, query});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String renderContent(String token, String spaceKey, String pageId,
			String content, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			String result = (String) client.execute(HANDLERCLASS
					+ ".renderContent", new Object[] {token, spaceKey, pageId, content});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public Vector getAttachments(String token, String pageId, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Vector result = (Vector) client.execute(HANDLERCLASS
					+ ".getAttachments", new Object[] {token, pageId});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Vector getComments(String token, String pageId, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Vector result = (Vector) client.execute(HANDLERCLASS
					+ ".getComments", new Object[] {token, pageId});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Hashtable storePage(String token, Hashtable pageht, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Hashtable result = (Hashtable) client.execute(HANDLERCLASS
					+ ".storePage", new Object[] {token, pageht});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deletePage(String token, String pageId, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			client.execute(HANDLERCLASS + ".deletePage", new Object[] {token, pageId});
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public Hashtable getUser(String token, String username, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Hashtable result = (Hashtable) client.execute(HANDLERCLASS
					+ ".getUser", new Object[] {token, username});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addUser(String token, Hashtable user, String password,
			String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			client.execute(HANDLERCLASS + ".addUser", new Object[] {token, user, password});
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public void addGroup(String token, String group, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			client.execute(HANDLERCLASS + ".addGroup", new Object[] {token, group});
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public Vector getUserGroups(String token, String username, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Vector result = (Vector) client.execute(HANDLERCLASS
					+ ".getUserGroups", new Object[] {token, username});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addUserToGroup(String token, String username, String groupname,
			String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			// FIXME Integer result = (Integer) 
			client.execute(HANDLERCLASS
					+ ".addUserToGroup", new Object[] {token, username, groupname});
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public Vector getAllPages(String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			Vector result = (Vector) client.execute(HANDLERCLASS
					+ ".getAllPages", new Object[] {});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Vector();
	}

	public byte[] getPage(String name, String wikiserver) {
		HandlerStub client = getConnection(wikiserver);
		try {
			byte[] result = (byte[]) client.execute(HANDLERCLASS + ".getPage",
					new Object[] {name});
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
}

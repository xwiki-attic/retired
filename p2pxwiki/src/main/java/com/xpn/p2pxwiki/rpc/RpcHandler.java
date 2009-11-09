package com.xpn.p2pxwiki.rpc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.xmlrpc.ConfluenceRpcHandler;
import com.xpn.xwiki.xmlrpc.RequestInitializableHandler;

/* all calls in this class are so far wrappers around confluencerpchandler 
 * taking care of exceptions and void return values for jxta-xmlrpc to work. 
 * 
 * some of the methods in this class are made protected and have the same name in ConfluenceRpcHandler, so that 
 * other classes can use these methods. Use a wrapper around the RPCHandler classes so that these methods are 
 * not exposed as APIs. 
 */
public class RpcHandler extends ConfluenceRpcHandler implements RequestInitializableHandler {
	private static Log log = LogFactory.getFactory().getInstance(
			RpcHandler.class);	
	/*
	 * @TODO: Note: the ConfluenceRPCHandler object relied on IP of the client
	 * to calculate Md5 hash. Because of that changes were made to the
	 * P2PXWikiRPC Handler and it was not simple extension of confluenceRPC
	 * handler.
	 * 
	 * Also, the p2pxmlrpchandler doesn't have a context, request,
	 * response objects to work with like the xml-rpc handler - at least not
	 * till struts port is done using p2psockets. so currently, it uses fake
	 * JXTAContext, JXTARequest and so on.
	 * 
	 * Fixed this!
	 */
	
	protected Map getTokens(XWikiContext context) {
		Map tokens = (Map) context.getEngineContext().getAttribute("xmlrpc_tokens");
		if (tokens == null) {
			tokens = new HashMap();
			context.getEngineContext().setAttribute("xmlrpc_tokens", tokens);
		}
		return tokens;
	}

	protected boolean checkToken(String token, XWikiContext context) {
		RemoteUser user = null;
		String ip = context.getRequest().getRemoteAddr();
		if (token != null)
			user = (RemoteUser) getTokens(context).get(token);
		if ((user == null) || (!user.ip.equals(ip))) {
			log.error("for token " + token + " user =" + user
					+ " is null  or user's ip doesn't match " + ip);
			return false;
		}
		context.setUser(user.username);
		return true;
	}

	protected String getValidationHash(String username, String password,
			String clientIP) {
		String validationKey = "xmlrpcapi";
		MessageDigest md5 = null;
		StringBuffer sbValueBeforeMD5 = new StringBuffer();

		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error: " + e);
			return "";
		}

		try {
			sbValueBeforeMD5.append(username.toString());
			sbValueBeforeMD5.append(":");
			sbValueBeforeMD5.append(password.toString());
			sbValueBeforeMD5.append(":");
			sbValueBeforeMD5.append(clientIP.toString());
			sbValueBeforeMD5.append(":");
			sbValueBeforeMD5.append(validationKey.toString());

			String valueBeforeMD5 = sbValueBeforeMD5.toString();
			md5.update(valueBeforeMD5.getBytes());

			byte[] array = md5.digest();
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < array.length; ++j) {
				int b = array[j] & 0xFF;
				if (b < 0x10)
					sb.append('0');
				sb.append(Integer.toHexString(b));
			}
			String valueAfterMD5 = sb.toString();
			return valueAfterMD5;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String login(String username, String password) {
		try {
			String token = super.login(username, password);
			if (token == null)
				return "";
			return token;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public boolean logout(String token) {
		try {
			return super.logout(token);
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	public Object[] getSpaces(String token) {
		try {
			return super.getSpaces(token);
		} catch (Exception e) {
			e.printStackTrace();
			return new Object[0];
		}
	}

	public Map getSpace(String token, String spaceKey) {
		try {
			return super.getSpace(token, spaceKey);
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap();
		}
	}

	public Object[] getPages(String token, String spaceKey) {
		try {
			return super.getPages(token, spaceKey);
		} catch (Exception e) {
			e.printStackTrace();
			return new Object[0];
		}
	}

	public Map getPage(String token, String pageId) {
		try {
			return super.getPage(token, pageId);
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap();
		}
	}

	public Object[] getPageHistory(String token, String pageId) {
		try {
			return super.getPageHistory(token, pageId);
		} catch (Exception e) {
			e.printStackTrace();
			return new Object[0];
		}
	}

	public Object[] search(String token, String query, int maxResults) {
		try {
			return super.search(token, query, maxResults);
		} catch (Exception e) {
			e.printStackTrace();
			return new Object[0];
		}
	}

	public String renderContent(String token, String spaceKey, String pageId, String content) {
		try {
			return super.renderContent(token, spaceKey, pageId, content);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public Object[] getAttachments(String token, String pageId) {
		try {
			return super.getAttachments(token, pageId);
		} catch (Exception e) {
			e.printStackTrace();
			return new Object[0];
		}
	}

	public Object[] getComments(String token, String pageId) {
		try {
			return super.getComments(token, pageId);
		} catch (Exception e) {
			e.printStackTrace();
			return new Object[0];
		}
	}

	public Map storePage(String token, Map pageht) {
		try {
			return super.storePage(token, pageht);
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap();
		}
	}

	/*
	 * @TODO: The confluence rpc handler method should return something
	 * 
	 */
	/*
	 * public boolean deletePage(String token, String pageId) { try {
	 * super.deletePage(token, pageId); return true; } catch (Exception e) {
	 * e.printStackTrace(); return false; }
	 */
	public Map getUser(String token, String username) {
		try {
			return super.getUser(token, username);
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap();
		}
	}

	/*
	 * @TODO: The confluence rpc handler method should return something
	 * 
	 */
	/*
	 * public void addUser(String token, Map user, String password) { }
	 * 
	 * public void addGroup(String token, String group) { }
	 */

	public Object[] getUserGroups(String token, String username) {
		try {
			return super.getUserGroups(token, username);
		} catch (Exception e) {
			e.printStackTrace();
			return new Object[0];
		}
	}

	/*
	 * @TODO: The confluence rpc handler method should return something
	 * 
	 */
	/*
	 * public void addUserToGroup(String token, String username, String
	 * groupname) { }
	 */
}

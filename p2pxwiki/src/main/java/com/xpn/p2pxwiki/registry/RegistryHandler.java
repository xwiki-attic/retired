package com.xpn.p2pxwiki.registry;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;

import com.xpn.p2pxwiki.rpc.RpcHandler;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.store.XWikiHibernateStore;
import com.xpn.xwiki.xmlrpc.RequestInitializableHandler;

public class RegistryHandler extends RpcHandler implements RequestInitializableHandler {
	private static Log log = LogFactory.getFactory().getInstance(
			RegistryHandler.class);

	private final int DEFAULT_DOMAIN_ID = 1;

	private final int DEFAULT_TTL = 120;

	private final String DEFAULT_TYPE = "A";
	private final String PLUGIN_NAME = "registry";

	private static Map virtualWikiMap = new HashMap();


	public String getName(){
		return PLUGIN_NAME; 
	}

	/* registry by default is maintained in the xwiki database, so 
	 * init method is passed "" as argument 
	 */
	public boolean existsWikiapp(String wikiapp) {
		try {
			XWikiContext context = getXWikiContext();
			return existsWikiapp(wikiapp, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/* modelled after XWiki.findWikiServer */
	private static String findWikiServer(String host, XWikiContext context) {
		String wikiserver = (String) virtualWikiMap.get(host);
		if (wikiserver != null)
			return wikiserver;

		String hql = ", BaseObject as obj, StringProperty as prop where obj.name="
				+ context.getWiki().getFullNameSQL()
				+ " and obj.className='XWiki.XWikiServerClass' and prop.id.id = obj.id "
				+ "and prop.id.name = 'server' and prop.value='" + host + "'";
		try {
			List list = context.getWiki().getStore().searchDocumentsNames(hql,
					context);
			if ((list == null) || (list.size() == 0))
				return null;
			String docname = (String) list.get(0);
			if (!docname.startsWith("XWiki.XWikiServer"))
				return null;
			wikiserver = docname.substring("XWiki.XWikiServer".length())
					.toLowerCase();
			virtualWikiMap.put(host, wikiserver);
			return wikiserver;
		} catch (XWikiException e) {
			return null;
		}
	}

	private XWikiDocument checkDocument(String wikiapp, XWikiContext context) {
		try {
			XWiki xwiki = context.getWiki();
			if (!xwiki.isVirtual()) {
				log.error("Not using Virtual Wiki !!!");
				return null;
			}
			if (xwiki.isVirtual()) {
				String host = "";
				try {
					String fullapp = wikiapp;
					if (fullapp.length() > 0 && fullapp.indexOf(":") == -1)
						fullapp = "http://" + fullapp;
					URL requestURL = new URL(fullapp);
					host = requestURL.getHost();
				} catch (Exception e) {
				}

				log.error("looking for a server document for host: " + host);
				if (host.equals(""))
					return null;

				String appname = findWikiServer(host, context);
				log.error("Checking for the existance of appname:" + appname);
				if (appname == null) {
					return null;
				}

				// Check if this appname exists in the Database
				String serverwikipage = XWiki.getServerWikiPage(appname);
				XWikiDocument doc = xwiki.getDocument(serverwikipage, context);
				if (doc.isNew()) {
					/* document doesn't exist */
					log.error("there is no document corresponding to appname: "
							+ appname);
					return null;
				}
				return doc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public BaseClass getServerClass(XWikiContext context) {
		XWikiDocument doc;
		boolean needsUpdate = false;
		try {
			doc = context.getWiki().getDocument("XWiki.XWikiServerClass",
					context);
		} catch (Exception e) {
			doc = new XWikiDocument();
			doc.setWeb("XWiki");
			doc.setName("XWikiServerClass");
			needsUpdate = true;
		}
		BaseClass bclass = doc.getxWikiClass();
		if (context.get("initdone") != null)
			return bclass;
		log.error("Creating a new Server Class document.");
		bclass.setName("XWiki.XWikiServerClass");
		needsUpdate |= bclass.addTextField("owner", "Owner", 30);
		needsUpdate |= bclass.addTextField("server", "Server", 30);
		String content = doc.getContent();
		if ((content == null) || (content.equals(""))) {
			needsUpdate = true;
			doc.setContent("1 XWiki Servers");
		}
		try {
			if (needsUpdate)
				context.getWiki().saveDocument(doc, context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bclass;
	}

	public boolean createServerClass(String wikiName, String wikiAdmin,
			String wikiUrl, String description, String wikilanguage,
			XWikiContext context) {
		XWiki wiki = context.getWiki();
		String wikiServerPage = "XWikiServer"
				+ wikiName.substring(0, 1).toUpperCase()
				+ wikiName.substring(1);
		try {
			XWikiDocument serverdoc = wiki.getDocument("XWiki", wikiServerPage,
					context);
			if (serverdoc.isNew()) {
				serverdoc.setStringValue("XWiki.XWikiServerClass", "server",
						wikiUrl);
				serverdoc.setLargeStringValue("XWiki.XWikiServerClass",
						"owner", wikiAdmin);
				if (description != null)
					serverdoc.setStringValue("XWiki.XWikiServerClass",
							"description", description);
				if (wikilanguage != null)
					serverdoc.setStringValue("XWiki.XWikiServerClass",
							"language", wikilanguage);
				serverdoc
						.setContent("#includeForm(\"XWiki.XWikiServerForm\")\n");
				serverdoc.setParent("XWiki.XWikiServerClass");
				wiki.saveDocument(serverdoc, context);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/*
	 * check whether the user is the owner of the wiki at the wikiapp : modelled
	 * after XWiki.getXWiki method
	 * 
	 * wikiapp is the name of the host. @TODO: Return appropriate error codes
	 * (document is new/not virtual wiki etc)
	 */

	private boolean checkWikiMaster(String wikiapp, String user,
			XWikiContext context) {
		XWikiDocument doc = checkDocument(wikiapp, context);
		if (doc == null)
			return false;
		String wikiOwner = doc
				.getStringValue("XWiki.XWikiServerClass", "owner");
		if (wikiOwner.indexOf(":") != -1)
			wikiOwner = wikiOwner.substring(wikiOwner.indexOf(":") + 1);
		log
				.error("found the owner of wiki " + wikiapp + " to be: "
						+ wikiOwner);
		if (user.startsWith("XWiki"))
			user = user.substring(user.indexOf(".") + 1);
		if (wikiOwner.startsWith("XWiki"))
			wikiOwner = wikiOwner.substring(wikiOwner.indexOf(".") + 1);
		log.error("comparing wikiowner:" + wikiOwner + " with user:" + user);
		if (wikiOwner.equalsIgnoreCase(user))
			return true;
		return false;
	}

	public boolean updateRegistry(String token, String wikiapp, String IP) {
		try {
			XWikiContext context = getXWikiContext();
			return updateRegistry(token, wikiapp, IP, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean existsWikiapp(String wikiapp, XWikiContext context) {
		boolean bTransaction = true;
		XWikiHibernateStore hibstore = null;
		try {
			hibstore = context.getWiki().getHibernateStore();
			bTransaction = bTransaction && hibstore.beginTransaction(context);
			Session session = hibstore.getSession(context);
			Query existQuery = session
					.createQuery("select r.name from DNSRecords as r "
							+ "where r.name = :name and r.type = :type and r.domainId = :domainId");
			existQuery.setString("name", wikiapp);
			existQuery.setString("type", DEFAULT_TYPE);
			existQuery.setInteger("domainId", DEFAULT_DOMAIN_ID);

			Iterator it = existQuery.list().iterator();
			while (it.hasNext()) {
				String name = (String) it.next();
				if (wikiapp.equals(name)) {
					log.error("found Wikiapp:" + wikiapp
							+ " in dnsrecords, yeah..");
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bTransaction && hibstore != null)
					hibstore.endTransaction(context, false);
			} catch (Exception e) {
			}
		}
		return false;
	}

	/*
	 * @TODO: This function updates all the ips with a given wikiapp, if the
	 * wikiapp exists. The reason is that the registry is maintaining a list of
	 * masters only which is unique at present per wiki.
	 * 
	 * 
	 * @TODO: Return more appropriate return codes (like authentication failure,
	 * wiki already exists etc)
	 */

	public boolean updateRegistry(String token, String wikiapp, String IP,
			XWikiContext context) {
		boolean bTransaction = true;
		XWikiHibernateStore hibstore = null;
		boolean result = false;

		// Verify authentication token
		if (!checkToken(token, context)) {
			log.error("Authentication token verification failed for token "
					+ token + "\n");
			return false;
		}
		String username = context.getUser();
		// check that user is the owner of the wiki before updating the ip
		log.error("checking if user: " + username
				+ " is the owner of the wiki: " + wikiapp);
		XWikiDocument serverDoc = checkDocument(wikiapp, context);
		if (serverDoc != null && (!checkWikiMaster(wikiapp, username, context))) {
			log.error("user: " + username
					+ " is not the owner of the wikiapp: " + wikiapp
					+ " , update failed");
			return false;
		}
		try {
			hibstore = context.getWiki().getHibernateStore();
			bTransaction = bTransaction && hibstore.beginTransaction(context);
			Session session = hibstore.getSession(context);
			Query existQuery = session
					.createQuery("select r from DNSRecords as r "
							+ "where r.name = :name and r.type = :type and r.domainId = :domainId");
			existQuery.setString("name", wikiapp);
			existQuery.setString("type", DEFAULT_TYPE);
			existQuery.setInteger("domainId", DEFAULT_DOMAIN_ID);
			log.error("about to perform sql query");
			Iterator it = existQuery.list().iterator();
			log.error("performed query, checking results");
			if (!it.hasNext()) {
				log
						.error("updating the registry with a *new record* for wikiapp"
								+ wikiapp + " and ip = " + IP);
				DNSRecords newrecord = new DNSRecords();
				newrecord.setDomainId(new Integer(DEFAULT_DOMAIN_ID));
				newrecord.setName(wikiapp);
				newrecord.setContent(IP);
				newrecord.setType(DEFAULT_TYPE);
				newrecord.setTtl(new Integer(DEFAULT_TTL));
				session.save(newrecord);
				/* create a new serverclass too */
				result = createServerClass(username, username, wikiapp,
						username + "'s p2pxwiki", null, context);
				if (result)
					log.error("successfully created server class for wiki "
							+ wikiapp);
				else
					log.error("failed to create server class for wiki"
							+ wikiapp);
			} else {
				while (it.hasNext()) {
					DNSRecords record = (DNSRecords) it.next();
					if (wikiapp.equals(record.getName())) {
						log
								.error("updating the registry with an *Existing record* for wikiapp"
										+ wikiapp + " and ip = " + IP);
						record.setContent(IP);
						session.update(record);
					}
				}
			}
			session.flush();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bTransaction && hibstore != null) {
					hibstore.endTransaction(context, true);
				}
			} catch (Exception e) {
			}
		}
		return result;
	}

	public String queryRegistry(String wikiname) {
		try {
			XWikiContext context = getXWikiContext();
			return queryRegistry(wikiname, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String queryRegistry(String wikiname, XWikiContext context) {
		boolean bTransaction = true;
		XWikiHibernateStore hibstore = null;

		try {
			hibstore = context.getWiki().getHibernateStore();
			bTransaction = bTransaction && hibstore.beginTransaction(context);
			Session session = hibstore.getSession(context);
			Query selectQuery = session
					.createQuery("select r.content from DNSRecords as r "
							+ "where r.name = :name and r.type =:type and r.domainId=:domainId");
			selectQuery.setString("name", wikiname);
			selectQuery.setString("type", DEFAULT_TYPE);
			selectQuery.setInteger("domainId", DEFAULT_DOMAIN_ID);
			Iterator it = selectQuery.list().iterator();
			/*
			 * @TODO : Load balancing for multiple IPs for same wiki : right now
			 * return the first one
			 */
			if (it.hasNext())
				return (String) it.next();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bTransaction && hibstore != null)
					hibstore.endTransaction(context, false);
			} catch (Exception e) {
			}
		}
		return "";
	}
}

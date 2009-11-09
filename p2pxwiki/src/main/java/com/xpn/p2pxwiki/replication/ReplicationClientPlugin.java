package com.xpn.p2pxwiki.replication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

public class ReplicationClientPlugin extends XWikiDefaultPlugin
		implements XWikiPluginInterface {
	private static String NAME = "p2pxwikireplication";
	private static Log log = LogFactory.getFactory().getInstance(
			ReplicationClientPlugin.class);

	public ReplicationClientPlugin(String name, String className,
			XWikiContext context) {
		super(NAME, className, context);
		init(context);
	}

	public String getName() {
		return super.getName();
	}

	public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
		try {
			return new ReplicationClientApi(context);
		} catch (Exception e) {
			return null;
		}
	}

	public void flushCache() {
	}
	
	public void init(XWikiContext context) {
		super.init(context);
		log.debug("initializing");
	}
}

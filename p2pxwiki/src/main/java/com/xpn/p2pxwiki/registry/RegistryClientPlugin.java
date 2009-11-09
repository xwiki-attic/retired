package com.xpn.p2pxwiki.registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

/**
 * Created by IntelliJ IDEA. User: bikash Date: Aug 19, 2005 Time: 12:31:43 AM
 */

public class RegistryClientPlugin extends XWikiDefaultPlugin implements
		XWikiPluginInterface {
	private static Log log = LogFactory.getFactory().getInstance(
			com.xpn.p2pxwiki.registry.RegistryClientPlugin.class);

	/*
	 * virtual address of the registry server, this is used to search in the p2p
	 * network by p2psockets -- FIXME Comment for what? 
	 */
	public RegistryClientPlugin(String name, String className,
			XWikiContext context) {
		super(name, className, context);
		init(context);
	}

	public String getName() {
		return "p2pxwikiregistry";
	}

	public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
		try {
			return new RegistryClientApi(context);
		} catch (Exception e) {
			return null;
		}
	}

	public void flushCache() {
	}

	public void init(XWikiContext context) {
		log.debug("initializing");
		super.init(context);
//		RegistryClientExamples example = new RegistryClientExamples(); 
//		example.run(); 
	}
}

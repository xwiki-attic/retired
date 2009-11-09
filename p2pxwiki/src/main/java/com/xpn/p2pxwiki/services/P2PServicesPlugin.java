/**
 * @author sdumitriu
 */
package com.xpn.p2pxwiki.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.services.file.FileP2PServer;
import com.xpn.p2pxwiki.services.file.FileWikiGroupServices;
import com.xpn.p2pxwiki.utils.P2PUtil;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

/**
 * 
 * @author sgm
 *
 */

public class P2PServicesPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface {
	public static final String PLUGIN_NAME = "p2pxwikiservices";
	public static final String SERVER_TYPE = "p2pxwiki.services.server";
	public static final String DEFAULT_SERVER_TYPE = FileP2PServer.class.getName();
	public static final String GROUP_TYPE = "p2pxwiki.services.groupinfo";
	public static final String DEFAULT_GROUP_TYPE = FileWikiGroupServices.class.getName();

	private static Log log = LogFactory.getFactory().getInstance(P2PServicesPlugin.class);
	private static P2PServer p2pserverServices;
	private static WikiGroupServices groupServices;
	private static String p2pserverServicesType;
	private static String groupServicesType;

	/**
	 * Standard XWiki plugin constructor, called by the plugin manager at startup.
	 * @param name The standard name guessed by the plugin manager.
	 * @param className The class name as found in the xwiki.cfg file.
	 * @param context The XWikiContext available when plugins are initialized.
	 */
	public P2PServicesPlugin(String name, String className, XWikiContext context) {
		super(name, className, context);
		log.debug("constructor");
		init(context);
	}

	/**
	 * Obtain the name by which this plugin can be retrieved.
	 */
	public String getName() {
		log.debug("getName");
		return PLUGIN_NAME;
	}

	/**
	 * Initializes the plugin.
	 * 
	 * @param context
	 *            The XWikiContext available when plugins are initialized.
	 */
	public void init(XWikiContext context) {
		log.debug("-- init --");
		log.debug("Calling parent init");
		super.init(context);
		XWiki xwiki = context.getWiki();

		log.debug("Configuring");
		log.debug("Configuring p2pserver services");
		p2pserverServicesType = xwiki.Param(SERVER_TYPE, DEFAULT_SERVER_TYPE);
		// TODO: Retry with default server services type on failure, if this isn't already the default
		try{
			p2pserverServices = (P2PServer)P2PUtil.getSingletonInstance(p2pserverServicesType);
		}
		catch(P2PXWikiException ex){
			log.error("Cannot load p2pserver services", ex);
			// we continue and hope for the best
		}
		log.debug("Obtained p2pserver services: " + p2pserverServices.getClass().getCanonicalName());

		log.debug("Configuring group services");
		groupServicesType = xwiki.Param(GROUP_TYPE, DEFAULT_GROUP_TYPE);
		// TODO: Retry with default group services type on failure, if this isn't already the default
		try{
			groupServices = (WikiGroupServices)P2PUtil.getSingletonInstance(groupServicesType);
		}
		catch(P2PXWikiException ex){
			log.error("Cannot load group services", ex);
			// we continue and hope for the best
		}
		log.debug("Obtained group services: " + groupServices.getClass().getCanonicalName());
	}

	/**
	 * Returns an API for using this plugin from scripts.
	 * @see com.xpn.xwiki.plugin.XWikiPluginInterface#getPluginApi(com.xpn.xwiki.plugin.XWikiPluginInterface, com.xpn.xwiki.XWikiContext) Overridden method.
	 * @return Scripting API for this plugin.
	 */
	public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
		if(!this.equals(plugin)){
			log.warn("getPluginApi: different objects! (plugin != this)");
		}
		return new P2PServicesApi(context, this);
	}

	/**
	 * @return the groupServices
	 */
	public static WikiGroupServices getGroupServices() {
		return groupServices;
	}

	/**
	 * @return the p2pserverServices
	 */
	public static P2PServer getP2PServerServices() {
		return p2pserverServices;
	}
}

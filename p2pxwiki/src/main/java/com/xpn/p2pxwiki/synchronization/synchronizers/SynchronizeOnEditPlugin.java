package com.xpn.p2pxwiki.synchronization.synchronizers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.synchronization.SynchronizationPlugin;
import com.xpn.p2pxwiki.synchronization.SynchronizationPolicy;
import com.xpn.p2pxwiki.synchronization.SynchronizationRequest;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.notify.XWikiActionNotificationInterface;
import com.xpn.xwiki.notify.XWikiActionRule;
import com.xpn.xwiki.notify.XWikiNotificationRule;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

public class SynchronizeOnEditPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface, XWikiActionNotificationInterface {
	private static Log log = LogFactory.getFactory().getInstance(com.xpn.p2pxwiki.synchronization.synchronizers.SynchronizeOnEditPlugin.class);
	protected SynchronizationPolicy policy;

	public SynchronizeOnEditPlugin(String name, String className, XWikiContext context) {
		super(name, className, context);
		log.debug("constructor");
		init(context);
	}

	public String getName() {
		log.debug("getName");
		return "p2pxwikisynchronizeonedit";
	}

	public void init(XWikiContext context) {
		log.debug("init");
		super.init(context);
		// Get a notification on each action
		context.getWiki().getNotificationManager().addGeneralRule(new XWikiActionRule(this, true, false));
	}

	public void notify(XWikiNotificationRule rule, XWikiDocument doc, String action, XWikiContext context) {
		log.debug("notification. doc = " + doc.getFullName() + " action = " + action);
		// See if the action is "edit"
		if (!("edit".equals(action) || "inline".equals(action))) {
			// Only sync on edit and inline
			log.debug("skip synchronization");
			return;
		}
		log.debug("begin synchronization");
		if(!policy.isUpdateNeeded(doc, action, context)){
			return;
		}
		SynchronizationRequest elements = this.policy.getSyncDocuments(doc, action, context);
		if(elements.getElements().size() > 0){
			SynchronizationPlugin.getInstance().synchronize(elements);
		}
		policy.updateContext(elements, doc, action, context);
	}
}

package com.xpn.p2pxwiki.synchronization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.synchronization.protocols.SimpleSynchronization;
import com.xpn.p2pxwiki.utils.P2PUtil;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

public class SynchronizationPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface {
	public static final String PLUGIN_NAME = "p2pxwikisynchronization";
	public static final String PROTOCOL_TYPE = "p2pxwiki.synchronization.protocol";
	public static final String RETRY_WITH_DEFAULT_PROTOCOL = "p2pxwiki.synchronization.retry_with_default_protocol";
	public static final String DEFAULT_PROTOCOL_TYPE = SimpleSynchronization.class.getName();

	private static Log log = LogFactory.getFactory().getInstance(com.xpn.p2pxwiki.synchronization.SynchronizationPlugin.class);

	protected XWiki xwiki;
	protected XWikiContext context;
	protected static SynchronizationPlugin instance;
	protected SynchronizationProtocol protocol;

	public static SynchronizationPlugin getInstance() {
		return instance;
	}

	public SynchronizationPlugin(String name, String className, XWikiContext context) {
		super(name, className, context);
		log.debug("constructor");
		instance = this;
		init(context);
	}

	public String getName() {
		log.debug("getName");
		return PLUGIN_NAME;
	}

	public void init(XWikiContext context) {
		log.debug("init");
		super.init(context);
		this.xwiki = context.getWiki();
		this.context = context;
		log.debug("Configuring protocol type");
		String protocolType = xwiki.Param(PROTOCOL_TYPE, DEFAULT_PROTOCOL_TYPE);
		try {
			protocol = (SynchronizationProtocol)P2PUtil.getInstance(protocolType);
			protocol.init(context);
			log.debug("Obtained protocol: " + protocol.getClass().getCanonicalName());
		} catch(P2PXWikiException ex) {
			log.error("Cannot load protocol", ex);
			ex.printStackTrace();	
			if (!protocolType.equals(DEFAULT_PROTOCOL_TYPE) &&
				xwiki.Param(RETRY_WITH_DEFAULT_PROTOCOL, "false") == "true") {
				log.debug("Retrying with default protocol type");
				try {
					protocol = (SynchronizationProtocol)P2PUtil.getInstance(DEFAULT_PROTOCOL_TYPE);
					protocol.init(context);
					log.debug("Obtained protocol: " + protocol.getClass().getCanonicalName());
				} catch(P2PXWikiException ex2) {						
					log.error("Cannot load default protocol", ex2);
					ex2.printStackTrace();
					// We continue and hope for the best
				}
			}
		}
	}

	public void synchronize(SynchronizationRequest request) {
		instance.protocol.processRequest(request);
	}
}

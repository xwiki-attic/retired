package com.xpn.p2pxwiki.synchronization.policies;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.communication.ConnectorPlugin;
import com.xpn.p2pxwiki.services.P2PServicesPlugin;
import com.xpn.p2pxwiki.synchronization.SynchronizationElement;
import com.xpn.p2pxwiki.synchronization.SynchronizationPolicy;
import com.xpn.p2pxwiki.synchronization.SynchronizationRequest;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.render.XWikiVelocityRenderer;

public class SyncEditPolicy implements SynchronizationPolicy {
	public static final String EDIT_ACTION = "edit";
	public static final String INLINE_ACTION = "inline";
	private static Log log = LogFactory.getFactory().getInstance(SyncEditPolicy.class);

	/**
	 * <ul>
	 * <li>edit action: only synchronize the document. Included documents are edited separately.</li>
	 * <li>inline action: synchronize the included documents also.</li>
	 * </ul>
	 */
	public SynchronizationRequest getSyncDocuments(XWikiDocument doc, String action, XWikiContext context) {
		String master = "";
		try{
			master = P2PServicesPlugin.getGroupServices().getWikiGroup(doc.getDatabase()).getMaster();
		}
		catch(P2PXWikiException ex){
			log.error("Error obtaining master for group " + doc.getDatabase(), ex);
		}
		if(EDIT_ACTION.equals(action)){
			SynchronizationElement[] result;
			if(context.getLanguage().equals(doc.getLanguage()) || context.getLanguage().equals(doc.getDefaultLanguage())){
				result = new SynchronizationElement[1];
			}
			else{
				result = new SynchronizationElement[2];
				result[1] = new SynchronizationElement(doc.getDatabase() + ":" + doc.getFullName(), master, context.getLanguage(), SynchronizationElement.MAX_PRIORITY);
			}
			result[0] = new SynchronizationElement(doc.getDatabase() + ":" + doc.getFullName(), master, doc.getLanguage(), SynchronizationElement.MAX_PRIORITY);
			return new SynchronizationRequest(result, false);
		}
		else if(INLINE_ACTION.equals(action)){
			List included = doc.getIncludedPages(context);
			int i = 1;
			SynchronizationElement[] result;
			if(context.getLanguage().equals(doc.getLanguage()) || context.getLanguage().equals(doc.getDefaultLanguage())){
				result = new SynchronizationElement[included.size() + 1];
			}
			else{
				result = new SynchronizationElement[included.size() + 2];
				result[1] = new SynchronizationElement(doc.getDatabase() + ":" + doc.getFullName(), master, context.getLanguage(), SynchronizationElement.MAX_PRIORITY);
				i = 2;
			}
			result[0] = new SynchronizationElement(doc.getDatabase() + ":" + doc.getFullName(), master, doc.getLanguage(), SynchronizationElement.MAX_PRIORITY);
			String includedName;
			for(Iterator it = included.iterator(); it.hasNext(); ++i){
				includedName = (String)it.next();
				if(includedName.indexOf(":") == -1){
					includedName = doc.getDatabase() + ":" + includedName;
				}
				result[i] = new SynchronizationElement(includedName, master, context.getLanguage(), SynchronizationElement.MAX_PRIORITY);
			}
			return new SynchronizationRequest(result, false);
		}
		return new SynchronizationRequest(new SynchronizationElement[0], false);
	}

	public boolean isUpdateNeeded(XWikiDocument doc, String action, XWikiContext context) {
		try{
			String master = P2PServicesPlugin.getGroupServices().getWikiGroup(doc.getDatabase()).getMaster();
			return !ConnectorPlugin.getInstance().getPeerName().equals(master);
		}
		catch(P2PXWikiException ex){
			log.error("Error obtaining master for group " + doc.getDatabase(), ex);
			return false;
		}
	}
	public boolean isAsynchronous(XWikiDocument doc, String action, XWikiContext context) {
		return false;
	}
	
	public void updateContext(SynchronizationRequest request, XWikiDocument doc, String action, XWikiContext context){
		String cdoc = doc.getDatabase() + ":" + doc.getFullName();
		request.getElements().size();
		SynchronizationElement element;
		for(Iterator it = request.getElements().iterator(); it.hasNext(); ){
			element = (SynchronizationElement)it.next();
			if(element.isUpdated() && element.getDocumentName().equals(cdoc) && (element.getDocumentLanguage().equals(doc.getLanguage()) || element.getDocumentLanguage().equals(doc.getDefaultLanguage()))){
				try{
					XWiki xwiki = XWiki.getXWiki(context);
					VelocityContext vcontext = XWikiVelocityRenderer.prepareContext(context);
					xwiki.prepareDocuments(context.getRequest(), context, vcontext);
				}
				catch(XWikiException ex){}
				return;
			}
		}
	}
}

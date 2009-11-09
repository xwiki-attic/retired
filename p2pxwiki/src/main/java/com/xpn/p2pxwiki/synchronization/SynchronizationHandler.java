package com.xpn.p2pxwiki.synchronization;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.xmlrpc.BaseRpcHandler;
import com.xpn.xwiki.xmlrpc.RequestInitializableHandler;

public class SynchronizationHandler extends BaseRpcHandler
		implements RequestInitializableHandler, SynchronizationInterface {
	public Object[] sync(String docName, String language, String remoteVersion, String remoteHash){
		Object[] result;
		try{
			XWikiContext context = getXWikiContext();
			XWikiDocument doc = context.getWiki().getDocument(docName, context)
					.getTranslatedDocument(language, context);

			if (!doc.getVersion().equals(remoteVersion)
					|| !(doc.getVersionHashCode(context)).equals(remoteHash)) {
				result = new Object[1];
				result[0] = "ok";
				return result;
			}
			int maxVersion = Integer.parseInt(doc.getVersion().substring(doc.getVersion().indexOf(".") + 1));
			result = new Object[maxVersion + 1];
			result[0] = "nok";
			for (int i = 1; i <= maxVersion; ++i){
				result[i] = context.getWiki().getDocument(doc, "1." + i, context).getVersionHashCode(context);
			}
		}
		catch(XWikiException ex){
			return null;
		}
		return result;
	}

	public Object[] merge(String docName, String language,
			Object[] hashHistory){
		XWikiDocument[] documents = null;
		Object[] result;
		// TODO: documents = merger.mergeBranches()
		result = new Object[documents.length];
		for(int i = 0; i < documents.length; ++i){
			try{
				result[i] = documents[i].toXML(true, false, true, false, getXWikiContext());
			}
			catch(XWikiException ignored){}
		}
		return result;
	}
}

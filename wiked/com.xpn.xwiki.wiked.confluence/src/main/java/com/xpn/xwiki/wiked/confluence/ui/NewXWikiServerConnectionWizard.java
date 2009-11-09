
package com.xpn.xwiki.wiked.confluence.ui;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.NewServerConnectionWizard;

/**
 * XWiki connection wizard 
 * @author Psenicka_Ja
 */
public class NewXWikiServerConnectionWizard extends
	NewServerConnectionWizard {

	public NewXWikiServerConnectionWizard() {
		super(WikedPlugin.getInstance().getRepositoryManager(), "xwiki");
	}

    /**
	 * @see com.xpn.xwiki.wiked.internal.ui.NewServerConnectionWizard#createURL(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	protected Object createURL(String host, int port, String usr, String pwd) {
        return "http://"+host+":"+port+"/xwiki/bin/xmlrpc/confluence";
	}
}

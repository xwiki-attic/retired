package org.semanticdesktop.wiki.core;

import org.semanticdesktop.wiki.core.WikiResource;

/**
 * @author kotelnikov
 */
public abstract class WikiResourceAdapter {

    protected WikiResource fWikiResource;

    /**
     * @param wikiResource
     */
    public WikiResourceAdapter(WikiResource wikiResource) {
        fWikiResource = wikiResource;
    }

    /**
     * @return the name of this adapter
     */
    public abstract String getAdapterName();
}

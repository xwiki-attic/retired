package org.semanticdesktop.wiki.core.adapter.source;

import org.semanticdesktop.wiki.core.WikiResource;
import org.semanticdesktop.wiki.core.WikiResourceAdapter;
import org.semanticdesktop.wiki.core.WikiResourceAdapterFactory;

/**
 * @author kotelnikov
 */
public class SourceWikiPageFactory extends WikiResourceAdapterFactory {

    /**
     * 
     */
    public SourceWikiPageFactory() {
        super();
    }

    /**
     * @see org.semanticdesktop.common.adapter.IObjectAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        return new Class[] { SourceWikiPage.class };
    }

    /**
     * @see org.semanticdesktop.wiki.core.adapter.WikiResourceAdapterFactory#newAdapter(org.semanticdesktop.wiki.core.WikiResource)
     */
    @Override
    protected WikiResourceAdapter newAdapter(WikiResource resource) {
        return new SourceWikiPage(resource);
    }

}

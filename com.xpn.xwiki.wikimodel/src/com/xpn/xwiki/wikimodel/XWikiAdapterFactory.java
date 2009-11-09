package com.xpn.xwiki.wikimodel;

import org.semanticdesktop.rdf.RdfException;
import org.semanticdesktop.wiki.core.WikiResource;
import org.semanticdesktop.wiki.core.WikiResourceAdapter;
import org.semanticdesktop.wiki.core.WikiResourceAdapterFactory;

public class XWikiAdapterFactory extends WikiResourceAdapterFactory {

    /**
     * @param repository
     * @throws RdfException
     */
    public XWikiAdapterFactory()
        throws RdfException {
    }

    /**
     * @see org.semanticdesktop.common.adapter.IObjectAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        return new Class[] { XWikiAdapter.class };
    }

    /**
     * @see org.semanticdesktop.wiki.core.adapter.semantic.AbstractSemanticWikiPageFactory#newAdapter(org.semanticdesktop.wiki.core.WikiResource)
     */
    @Override
    protected WikiResourceAdapter newAdapter(WikiResource resource) {
        return new XWikiAdapter(resource);
    }

}

package org.semanticdesktop.wiki.core.adapter.semantic;

import org.semanticdesktop.wiki.core.WikiResource;
import org.semanticdesktop.wiki.core.WikiResourceAdapter;
import org.semanticdesktop.wiki.core.WikiResourceAdapterFactory;

/**
 * This factory creates "semantic" adapters for {@link WikiResource} class. This
 * adapter is used mostly as a convenience tool to index/search references
 * from/to a wiki document.
 * 
 * @author kotelnikov
 */
public class SemanticWikiPageFactory extends WikiResourceAdapterFactory {

    /**
     * The triple index used to index statements
     */
    public TripleIndex fIndex;

    /**
     * @param index
     */
    public SemanticWikiPageFactory(TripleIndex index) {
        super();
        fIndex = index;
    }

    /**
     * @see org.semanticdesktop.common.adapter.IObjectAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        return new Class[] { SemanticWikiPage.class };
    }

    /**
     * @see org.semanticdesktop.wiki.core.adapter.semantic.AbstractSemanticWikiPageFactory#newAdapter(org.semanticdesktop.wiki.core.WikiResource)
     */
    @Override
    protected WikiResourceAdapter newAdapter(WikiResource resource) {
        return new SemanticWikiPage(resource, this);
    }

}

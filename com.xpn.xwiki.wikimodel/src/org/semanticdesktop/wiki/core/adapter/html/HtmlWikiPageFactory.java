package org.semanticdesktop.wiki.core.adapter.html;

import org.semanticdesktop.wiki.core.WikiResource;
import org.semanticdesktop.wiki.core.WikiResourceAdapter;
import org.semanticdesktop.wiki.core.WikiResourceAdapterFactory;

/**
 * @author kotelnikov
 */
public class HtmlWikiPageFactory extends WikiResourceAdapterFactory {

    private IWikiLinkFormatter fLinkFormatter;

    /**
     * @param linkFormatter
     */
    public HtmlWikiPageFactory(IWikiLinkFormatter linkFormatter) {
        super();
        fLinkFormatter = linkFormatter;
    }

    /**
     * @see org.semanticdesktop.common.adapter.IObjectAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        return new Class[] { HtmlWikiPage.class };
    }

    /**
     * @see org.semanticdesktop.wiki.core.adapter.WikiResourceAdapterFactory#newAdapter(org.semanticdesktop.wiki.core.WikiResource)
     */
    @Override
    protected WikiResourceAdapter newAdapter(WikiResource resource) {
        return new HtmlWikiPage(resource, fLinkFormatter);
    }

}

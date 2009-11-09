package org.semanticdesktop.wiki.core.adapter.source;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import org.semanticdesktop.wiki.core.WikiResource;
import org.semanticdesktop.wiki.core.WikiResourceAdapter;
import org.semanticdesktop.wiki.core.adapter.html.FormatUtil;
import org.semanticdesktop.wikimodel.WikiParserException;
import org.semanticdesktop.wikimodel.wem.IWikiDocumentParser;
import org.semanticdesktop.wikimodel.wem.impl.WikiDocumentParser;
import org.semanticdesktop.wikimodel.wom.IWikiDocument;
import org.semanticdesktop.wikimodel.wom.IWikiDocumentSection;
import org.semanticdesktop.wikimodel.wom.IWikiNodeFactory;
import org.semanticdesktop.wikimodel.wom.util.WikiDocumentBuilder;

/**
 * @author kotelnikov
 */
public class SourceWikiPage extends WikiResourceAdapter {

    /**
     * @param resource
     */
    public SourceWikiPage(WikiResource resource) {
        super(resource);
    }

    /**
     * @see org.semanticdesktop.wiki.core.WikiResourceAdapter#getAdapterName()
     */
    public String getAdapterName() {
        return "source";
    }

    /**
     * @return the string representation of the content of the page
     */
    public String getSource() {
        IWikiDocument doc = fWikiResource.getDocument();
        return FormatUtil.toSource(doc);
    }

    /**
     * @param sectionKey
     * @return the string representation of the section with the given key
     */
    public String getSource(String sectionKey) {
        IWikiDocumentSection section = fWikiResource.getSection(sectionKey);
        return FormatUtil.toSource(section);
    }

    /**
     * Writes down the content of the page into the given stream.
     * 
     * @param writer in this stream the source of the page will be written
     */
    public void getSource(Writer writer) {
        IWikiDocument doc = fWikiResource.getDocument();
        FormatUtil.toSource(doc, writer);
    }

    /**
     * Reads the content of the wiki page in the form of a wiki syntax from the
     * given reader and create a new wiki document object. Note that this method
     * does not close the given reader and it should be closed explicitly by the
     * caller.
     * 
     * @param reader the reader with the wiki content
     * @throws WikiParserException
     * @throws IOException
     */
    public void setSource(Reader reader)
        throws WikiParserException,
        IOException {
        String docUri = fWikiResource.getUri().toString();
        IWikiNodeFactory nodeFactory = fWikiResource.getNodeFactory();
        WikiDocumentBuilder builder = new WikiDocumentBuilder(nodeFactory);
        IWikiDocumentParser parser = new WikiDocumentParser();
        parser.parse(docUri, reader, builder);
        IWikiDocument document = (IWikiDocument) builder.getResult();
        fWikiResource.setDocument(document);
    }

    /**
     * @param content
     * @throws WikiParserException
     * @throws IOException
     */
    public void setSource(String content)
        throws WikiParserException,
        IOException {
        StringReader reader = new StringReader(content);
        setSource(reader);
    }

}

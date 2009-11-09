package org.semanticdesktop.wiki.core.adapter.semantic;

import java.util.ArrayList;
import java.util.List;

import org.semanticdesktop.rdf.RdfException;
import org.semanticdesktop.wiki.core.IWikiResourceListener;
import org.semanticdesktop.wiki.core.WikiResource;
import org.semanticdesktop.wiki.core.WikiResourceAdapter;
import org.semanticdesktop.wiki.core.WikiResourceEvent;
import org.semanticdesktop.wikimodel.wom.IWikiDocument;
import org.semanticdesktop.wikimodel.wom.IWikiDocumentSection;
import org.semanticdesktop.wikimodel.wom.IWikiReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a "semantic" adapter for {@link WikiResource}. It is used mostly as
 * a convenience tool to index/search references from/to the corresponding wiki
 * document.
 * 
 * @author kotelnikov
 */
public class SemanticWikiPage extends WikiResourceAdapter
    implements
    IWikiResourceListener {

    /**
     * The internal class logger.
     */
    private final static Logger log = LoggerFactory
        .getLogger(SemanticWikiPage.class);

    /**
     * The factory produced this adapter.
     */
    private SemanticWikiPageFactory fFactory;

    /**
     * @param wikiResource
     * @param factory
     */
    public SemanticWikiPage(
        WikiResource wikiResource,
        SemanticWikiPageFactory factory) {
        super(wikiResource);
        fFactory = factory;
    }

    /**
     * Retursn a list containing all resulting values (serialized as strings)
     * specified by the given SPARQL query. This is a convinience method. It
     * simply redirect the given query to the underlying triple index.
     * 
     * @param query the SPARQL query to execute
     * @return a list containing all resulting values (serialized as strings)
     *         specified by the given SPARQL query
     * @see TripleIndex#executeQuery(String)
     */
    public List<List<String>> executeQuery(String query) {
        try {
            return fFactory.fIndex.executeQuery(query);
        } catch (RdfException e) {
            log.error("Can not exequite to given query. Query: '"
                + query
                + "'.", e);
            return null;
        }
    }

    /**
     * @see org.semanticdesktop.wiki.core.WikiResourceAdapter#getAdapterName()
     */
    @Override
    public String getAdapterName() {
        return "semantic";
    }

    /**
     * Returns a list of all backlinks to this page
     * 
     * @return all backlinks to this page
     */
    public List<List<String>> getBackLinks() {
        return getBackLinks(null);
    }

    /**
     * Returns a list of all backlinks to this page by the specified property.
     * 
     * @param predicateUri the property used to search backlinks to this page
     * @return all backlinks to this page
     */
    public List<List<String>> getBackLinks(String predicateUri) {
        String uri = getUri();
        try {
            return fFactory.fIndex.getStatements(null, predicateUri, uri);
        } catch (RdfException e) {
            log.error(
                "Can not load backlinks for the specified property. PageUri: '"
                    + uri
                    + "'. PredicateUri: '"
                    + predicateUri
                    + "'.",
                e);
            return null;
        }
    }

    /**
     * Returns the factory.
     * 
     * @return the factory.
     */
    public SemanticWikiPageFactory getFactory() {
        return fFactory;
    }

    /**
     * Returns a list of all forward links from this document
     * 
     * @return a list of all direct links from this document
     */
    public List<List<String>> getForwardLinks() {
        return getForwardLinks(null);
    }

    /**
     * Returns a list of all forward links from this document by the specified
     * property
     * 
     * @param predicateUri the uri of the property used to load all forward
     *        links
     * @return a list of all direct links from this document by the specified
     *         property
     */
    public List<List<String>> getForwardLinks(String predicateUri) {
        String uri = getUri();
        try {
            return fFactory.fIndex.getStatements(uri, predicateUri, null);
        } catch (RdfException e) {
            log.error(
                "Can not load forward links for the specified property. PageUri: '"
                    + uri
                    + "'. PredicateUri: '"
                    + predicateUri
                    + "'.",
                e);
            return null;
        }
    }

    /**
     * @return the triple index used to store all information about references
     *         from/to this page
     */
    public TripleIndex getIndex() {
        return fFactory.fIndex;
    }

    /**
     * Returns an uri corresponding to the given document. If the document does
     * not contain any uri (or it is empty) then this method will create a new
     * one and it will set this generated uri to the document.
     * 
     * @param document for this document a corresponding uri will be generated
     * @return an uri corresponding to the given document
     */
    protected String getOrCreateUri(IWikiDocument document) {
        String uri = document.getReference();
        if (uri == null) {
            document.setReference(uri);
        }
        return uri;
    }

    /**
     * Returns a list of all statements containing this page as a predicate
     * 
     * @return a list of all statements containing this page as a predicate
     */
    public List<List<String>> getPropertyLinks() {
        String uri = getUri();
        try {
            return fFactory.fIndex.getStatements(null, uri, null);
        } catch (RdfException e) {
            log
                .error(
                    "Can not load property links. PageUri: '" + uri + "'.",
                    e);
            return null;
        }
    }

    /**
     * @return the uri of the page in the form of a string
     */
    private String getUri() {
        return fWikiResource.getUri().toString();
    }

    /**
     * @see org.semanticdesktop.wiki.core.IWikiResourceListener#handleEvent(org.semanticdesktop.wiki.core.WikiResourceEvent)
     */
    public void handleEvent(WikiResourceEvent event) {
        String uri = getUri();
        try {
            switch (event.getType()) {
                case DOCUMENT_DELETED:
                    fFactory.fIndex.removeStatements(uri, null, null);
                    break;
                case DOCUMENT_STORED:
                    IWikiDocument document = fWikiResource.getDocument();
                    indexDocument(document);
                    break;
            }
        } catch (RdfException e) {
            log.error(
                "Can not index page references. PageUri: '" + uri + "'.",
                e);
        }
    }

    /**
     * @param document
     * @throws RdfException
     */
    private void indexDocument(IWikiDocument document) throws RdfException {
        String documentUri = getOrCreateUri(document);
        fFactory.fIndex.removeStatements(documentUri, null, null);
        List<IWikiReference> references = new ArrayList<IWikiReference>();
        List<IWikiDocument> documents = new ArrayList<IWikiDocument>();
        for (Object obj : document) {
            references.clear();
            documents.clear();
            IWikiDocumentSection section = (IWikiDocumentSection) obj;
            fWikiResource.extractDocumentsAndReferences(
                section,
                references,
                documents);
            int referenceCount = references.size();
            int documentCount = documents.size();
            String[] referenceList = new String[referenceCount + documentCount];
            int i = 0;
            for (IWikiReference ref : references) {
               //:SL: dont index external links
            	String refUri = ref.getReference();
            	if (refUri != null && refUri.startsWith("http"))
            		continue;
            	referenceList[i++] = ref.getReference();
                
            }
            for (IWikiDocument doc : documents) {
                indexDocument(doc);
                referenceList[i++] = getOrCreateUri(doc);
            }
            String sectionUri = section.getReference();
            if (sectionUri == null)
            	sectionUri = "Content";
            fFactory.fIndex.addStatements(
                documentUri,
                sectionUri,
                referenceList);
        }
    }

}

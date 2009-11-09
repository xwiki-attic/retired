package org.semanticdesktop.wiki.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticdesktop.common.adapter.IAdaptableObject;
import org.semanticdesktop.wikimodel.wom.IWikiDocument;
import org.semanticdesktop.wikimodel.wom.IWikiDocumentSection;
import org.semanticdesktop.wikimodel.wom.IWikiNodeContainer;
import org.semanticdesktop.wikimodel.wom.IWikiNodeFactory;
import org.semanticdesktop.wikimodel.wom.IWikiReference;
import org.semanticdesktop.wikimodel.wom.impl.WikiNodeVisitor;

/**
 * Common class providing access to all wiki resource functionalities.
 * 
 * @author kotelnikov
 */
public class WikiResource implements IAdaptableObject {

    protected Map<String, Object> fAdapters = new HashMap<String, Object>();

    private IWikiDocument fDocument;

    protected boolean fLoaded;

    private boolean fModified;

    private IWikiNodeFactory fNodeFactory;

    private URI fUri;

    /**
     * @param nodeFactory
     * @param uri
     */
    public WikiResource(IWikiNodeFactory nodeFactory, URI uri) {
        fNodeFactory = nodeFactory;
        fUri = uri;
        fDocument = newDocument();
        fLoaded = false;
        fModified = false;
    }

    private String checkSectionKey(String sectionKey) {
        if (sectionKey == null)
            sectionKey = getDefaultSectionKey();
        return sectionKey;
    }

    /**
     * Sends an {@link IWikiResourceListener#DOCUMENT_DELETED} event to all
     * listeners. Resets flags <code>loaded</code> and <code>modified</code>.
     * 
     * @param params user's parameters for this operation; this object is
     *        available for event listeners using
     *        {@link WikiResourceEvent#getParams()} method
     */
    public void delete(Map<String, Object> params) {
        sendEvent(IWikiResourceListener.DOCUMENT_DELETED, params);
        fLoaded = true;
        fModified = false;
    }

    /**
     * Extracts all embedded documents and references from the given section and
     * fills out the given lists with references and embedded documents
     * contained in this section.
     * 
     * @param section the section to filter
     * @param references this list will be filled with references from the given
     *        section
     * @param documents this list will be filled with embedded documents
     *        contained in the given section
     */
    public void extractDocumentsAndReferences(
        IWikiDocumentSection section,
        final List<IWikiReference> references,
        final List<IWikiDocument> documents) {
        final String defaultSectionKey = getDefaultSectionKey();
        section.acceptVisitor(new WikiNodeVisitor() {
            private boolean isDefaultSection(String uri) {
                return uri == null
                    || "".equals(uri)
                    || defaultSectionKey.equals(uri);
            }

            private boolean isSemanticDocument(IWikiDocument node) {
                boolean result = false;
                for (Object obj : node) {
                    IWikiDocumentSection s = (IWikiDocumentSection) obj;
                    String reference = s.getReference();
                    result |= isDefaultSection(reference);
                    if (result)
                        break;
                }
                return result;
            }

            @Override
            public void visit(IWikiDocument node) {
                if (isSemanticDocument(node))
                    documents.add(node);
                else
                    super.visit(node);
            }

            @Override
            public void visit(IWikiReference node) {
                references.add(node);
            }
        });
    }

    /**
     * @see org.semanticdesktop.common.adapter.IAdaptableObject#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapterType) {
        Object adapter = fAdapters.get(adapterType.getName());
        if (adapter != null)
            return adapter;
        return WikiAdapterManager.getInstance().getAdapter(this, adapterType);
    }

    /**
     * Returns all adapters of this object corresponding to this type
     * 
     * @param adapterType
     * @return a set of all adapters of this resource
     */
    public Collection<Object> getAdapters(Class adapterType) {
        WikiAdapterManager manager = WikiAdapterManager.getInstance();
        String[] types = manager.computeAdapterTypes(getClass());
        Set<Object> adapters = new HashSet<Object>();
        for (String type : types) {
            Object adapter = manager.getAdapter(this, type);
            if (adapterType.isInstance(adapter))
                adapters.add(adapter);
        }
        return adapters;
    }

    /**
     * @return all adapters of this type
     */
    public WikiResourceAdapter[] getAllAdapters() {
        Collection<Object> result = getAdapters(WikiResourceAdapter.class);
        return result.toArray(new WikiResourceAdapter[result.size()]);
    }

    /**
     * @return an array of section urls; returned array can not be
     *         <code>null</code>; it can be empty or it contains not-<code>null</code>
     *         section keys (urls)
     */
    public String[] getAllSectionKeys() {
        int size = fDocument.size();
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            IWikiDocumentSection section = (IWikiDocumentSection) fDocument
                .get(i);
            result[i] = getSectionKey(section);
        }
        return result;
    }

    /**
     * Returns the key of the default section (the section for which the key is
     * not defined).
     * 
     * @return the key of the default section (the section for which the key is
     *         not defined).
     */
    public String getDefaultSectionKey() {
        return "content";
    }

    /**
     * @return an internal document corresponding to this resource
     */
    public IWikiDocument getDocument() {
        return fDocument;
    }

    /**
     * @return the string representation of the uri of this resource
     */
    private String getDocumentUri() {
        return fUri.toString();
    }

    /**
     * @return the node factory used by this resource to create structural
     *         elements of wiki documents
     */
    public IWikiNodeFactory getNodeFactory() {
        return fNodeFactory;
    }

    /**
     * @param container
     * @return an array of all references from the given container
     */
    private String[] getReferences(IWikiNodeContainer container) {
        final List<String> references = new ArrayList<String>();
        if (container != null) {
            container.acceptVisitor(new WikiNodeVisitor() {
                public void visit(IWikiReference node) {
                    references.add(node.getReference());
                }
            });
        }
        return references.toArray(new String[references.size()]);
    }

    /**
     * @param sectionKey
     * @return a list of all section keys
     */
    public String[] getReferences(String sectionKey) {
        IWikiDocumentSection section = getSection(sectionKey);
        IWikiNodeContainer container = section;
        return getReferences(container);
    }

    /**
     * @param sectionKey
     * @return the section with the given key
     */
    public IWikiDocumentSection getSection(String sectionKey) {
        sectionKey = checkSectionKey(sectionKey);
        IWikiDocumentSection result = null;
        for (Iterator iterator = fDocument.iterator(); result == null
            && iterator.hasNext();) {
            IWikiDocumentSection section = (IWikiDocumentSection) iterator
                .next();
            String key = getSectionKey(section);
            if (sectionKey.equals(key))
                result = section;
        }
        return result;
    }

    private String getSectionKey(IWikiDocumentSection section) {
        if (section == null)
            return null;
        String sectionKey = section.getReference();
        return checkSectionKey(sectionKey);
    }

    /**
     * Returns the uri of the section with the given key
     * 
     * @param sectionKey the key of the section
     * @return the section with the given key
     */
    public String getSectionUri(String sectionKey) {
        IWikiDocumentSection section = getSection(sectionKey);
        return getSectionKey(section);
    }

    /**
     * Returns the uri.
     * 
     * @return the uri.
     */
    public URI getUri() {
        return fUri;
    }

    /**
     * Returns <code>true</code> if this page was loaded from the storage.
     * 
     * @return <code>true</code> if this page was loaded from the storage.
     */
    public boolean isLoaded() {
        return fLoaded;
    }

    /**
     * Returns <code>true</code> if this resource was modified.
     * 
     * @return <code>true</code> if this resource was modified.
     */
    public boolean isModified() {
        return fModified;
    }

    /**
     * Sends an {@link IWikiResourceListener#DOCUMENT_LOADED} event to all
     * listeners. Resets flags <code>loaded</code> and <code>modified</code>.
     * 
     * @param params user's parameters for this operation; this object is
     *        available for event listeners using
     *        {@link WikiResourceEvent#getParams()} method
     */
    public void load(Map<String, Object> params) {
        sendEvent(IWikiResourceListener.DOCUMENT_LOADED, params);
        fLoaded = true;
        fModified = false;
    }

    /**
     * @return a newly created wiki document
     */
    @SuppressWarnings("unchecked")
    public IWikiDocument newDocument() {
        String uri = getDocumentUri();
        IWikiDocument document = fNodeFactory.newDocument(uri);
        IWikiDocumentSection section = fNodeFactory.newDocumentSection(null);
        document.add(section);
        return document;
    }

    /**
     * @param context
     * @param type
     * @return a newly created event of the given type
     */
    public WikiResourceEvent newEvent(int type, Map<String, Object> context) {
        return new WikiResourceEvent(this, type, context);
    }

    /**
     * Create and sends an event of the given type to all listeners
     * 
     * @param eventType the type of the event
     * @param params user's parameters for this operation; this object is
     *        available for event listeners using
     *        {@link WikiResourceEvent#getParams()} method
     */
    public void sendEvent(int eventType, Map<String, Object> params) {
        sendEvent(newEvent(eventType, params));
    }

    /**
     * Sends the given event to all listeners
     * 
     * @param event the event to send
     */
    public void sendEvent(WikiResourceEvent event) {
        Collection<Object> adapterListeners = getAdapters(IWikiResourceListener.class);
        for (Object o : adapterListeners) {
            IWikiResourceListener listener = (IWikiResourceListener) o;
            listener.handleEvent(event);
        }
    }

    /**
     * Sets a new data in this resource.
     * 
     * @param key the data key
     * @param object the value of the data to set
     */
    public void setData(String key, Object object) {
        fAdapters.put(key, object);
    }

    /**
     * Sets a new document
     * 
     * @param document
     */
    public void setDocument(IWikiDocument document) {
        fDocument = document;
        setModified(true);
        sendEvent(IWikiResourceListener.DOCUMENT_CHANGED, null);
    }

    /**
     * Sets a new value of the modification flag.
     * 
     * @param modified a new value to set.
     */
    public void setModified(boolean modified) {
        fModified = modified;
    }

    /**
     * Sends an {@link IWikiResourceListener#DOCUMENT_DELETED} event to all
     * listeners. Resets flags <code>loaded</code> and <code>modified</code>.
     * 
     * @param params user's parameters for this operation; this object is
     *        available for event listeners using
     *        {@link WikiResourceEvent#getParams()} method
     */
    public void store(Map<String, Object> params) {
        if (!fModified)
            return;
        sendEvent(IWikiResourceListener.DOCUMENT_STORED, params);
        fLoaded = true;
        fModified = false;
    }

}

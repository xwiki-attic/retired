package org.semanticdesktop.wiki.core;

import org.semanticdesktop.common.adapter.IObjectAdapterFactory;
import org.semanticdesktop.wiki.core.WikiResource;

/**
 * @author kotelnikov
 */
public abstract class WikiResourceAdapterFactory
    implements
    IObjectAdapterFactory {

    /**
     * 
     */
    public WikiResourceAdapterFactory() {
        super();
    }

    /**
     * @see org.semanticdesktop.common.adapter.IObjectAdapterFactory#getAdapter(java.lang.Object,
     *      java.lang.Class)
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        WikiResource resource = (WikiResource) adaptableObject;
        Class[] array = getAdapterList();
        String key = array[0].getName();
        Object adapter = resource.fAdapters.get(key);
        if (adapter == null) {
            adapter = newAdapter(resource);
            resource.setData(key, adapter);
        }
        return adapter;
    }

    /**
     * @param resource
     * @return a newly created adapter for the given resource
     */
    protected abstract WikiResourceAdapter newAdapter(WikiResource resource);

}

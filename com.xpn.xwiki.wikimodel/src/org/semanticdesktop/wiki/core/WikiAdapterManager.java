package org.semanticdesktop.wiki.core;

import org.semanticdesktop.common.adapter.ObjectAdapterManager;

/**
 * @author kotelnikov
 */
public class WikiAdapterManager extends ObjectAdapterManager {

    private static WikiAdapterManager fInstance = new WikiAdapterManager();

    /**
     * Returns the instance.
     * 
     * @return the instance.
     */
    public static WikiAdapterManager getInstance() {
        return fInstance;
    }

    /**
     * Registers the given factory of wiki resource adapters.
     * 
     * @param factory the factory to register
     */
    public static void register(WikiResourceAdapterFactory factory) {
        fInstance.registerAdapters(factory, WikiResource.class);
    }

}

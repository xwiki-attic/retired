package org.semanticdesktop.wiki.core;

/**
 * Adapters of this type are notified about events occurred with wiki resources.
 * 
 * @author kotelnikov
 */
public interface IWikiResourceListener {

    /**
     * This event is sent when the document is changed
     */
    int DOCUMENT_CHANGED = 1;

    /**
     * This type of events are sent when a document is deleted.
     */
    int DOCUMENT_DELETED = 2;

    /**
     * Events of this type are used to notify that a documen is loaded.
     */
    int DOCUMENT_LOADED = 3;

    /**
     * This type of events is used when a document is stored.
     */
    int DOCUMENT_STORED = 4;

    /**
     * This method is used to notify about all events with the resource
     * 
     * @param event the event to handle
     */
    void handleEvent(WikiResourceEvent event);

}

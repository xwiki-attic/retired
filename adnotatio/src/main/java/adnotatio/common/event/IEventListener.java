package adnotatio.common.event;

/**
 * Listeners are used to notify about the corresponding fired events.
 * 
 * @author kotelnikov
 */
public interface IEventListener {

    /**
     * This method is called when an event is occurred
     * 
     * @param event the fired event
     */
    void handleEvent(Event event);

    /**
     * This method is called in all registered listeners just before the calling
     * the {@link #handleEvent(Event)} method. If this method in an least one of
     * the registered listeners returns <code>false</code> then the method
     * {@link #handleEvent(Event)} is not called at all. This method can be used
     * to prepare or check events before their real delivering in the
     * {@link #handleEvent(Event)} methods.
     * 
     * @param event the fired event
     */
    boolean prepareEvent(Event event);

}
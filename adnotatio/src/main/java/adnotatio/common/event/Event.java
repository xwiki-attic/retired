/**
 * 
 */
package adnotatio.common.event;

/**
 * The main super-class for all events managed by this event system. Clients can
 * create their own events and the corresponding event listeners. The java class
 * of the event is used as an identifier of the event type.
 * 
 * @author kotelnikov
 */
public abstract class Event {

    /**
     * The event manager rising this event. This field is initialized by the
     * {@link EventManager#fireEvent(Event)} method.
     * 
     * @see #setEventManager(EventManager)
     */
    private EventManager fEventManager;

    /**
     * The type of this event. This field is used by the {@link EventManager} to
     * get listeners corresponding to this type of events.
     * 
     * @see #getType()
     */
    private final Class fType;

    /**
     * The default constructor initializing the type of this event. The given
     * class is used by the {@link EventManager} as a key to load event
     * listeners corresponding to this type of events.
     * 
     * @param type this class defines the type of this event
     */
    public Event(Class type) {
        super();
        fType = type;
    }

    /**
     * Returns the event manager firing this event
     * 
     * @return the event manager firing this event
     */
    public EventManager getEventManager() {
        return fEventManager;
    }

    /**
     * Returns the type of this event. This value is used by the
     * {@link EventManager} to get listeners corresponding to this type of
     * events.
     * 
     * @return the type of this event
     */
    public Class getType() {
        return fType;
    }

    /**
     * Sets the event manager firing this event. This method is called by the
     * {@link EventManager#fireEvent(Event)} method.
     * 
     * @param eventManager event manager firing this event
     */
    void setEventManager(EventManager eventManager) {
        fEventManager = eventManager;
    }
}
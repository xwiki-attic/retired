/**
 * 
 */
package adnotatio.common.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The event manager is used to deliver user-defined events to the corresponding
 * registered event listeners. Clients can create their own events and register
 * the corresponding specific listeners. This class was designed to properly
 * handle situations when an event listener fires a new event. It has no
 * external dependencies and it can be used on the client side as well as on the
 * server.
 * 
 * <pre>
 * // Example of usage of this class:
 * 
 * // A new user-defined type of events
 * class MyEvent extends Event {
 *     String fMessage;
 *     public MyEvent(String msg) {
 *         super(MyEvent.class);
 *         fMessage = msg;
 *     }
 *     public String getMessage() { return fMessage; }
 * }
 * 
 * public class Test {
 *     public static void main(String[] args) {
 *         EventManager manager = new EventManager();
 *         // Registers a new listener which is notified about all MyEvent events
 *         manager.addListener(MyEvent.class, new EventListener() {
 *             public void handleEvent(Event event) {
 *                 MyEvent e = (MyEvent) event;
 *                 System.out.println(e.getMessage());
 *             }
 *         });
 *         // Now we fire a new event
 *         manager.fireEvent(new MyEvent("Hello, world!"));
 *     }
 * }
 </pre>
 * 
 * @author kotelnikov
 */
public class EventManager {

    /**
     * The event queue used to keep events added from event listeners.
     */
    private List fEventQueue = new ArrayList();

    /**
     * This map is used to map event types to the corresponding lists of event
     * listeners.
     */
    private Map fListeners = new HashMap();

    /**
     * Adds a new listener to the internal list of listeners
     * 
     * @param eventType the type of events for which the listener should be
     *        added
     * @param listener the listener to add
     */
    public void addListener(Class eventType, IEventListener listener) {
        List listeners = getListeners(eventType, true);
        listeners.add(listener);
    }

    /**
     * Fires a new event. This method uses the type of the given event (see the
     * {@link Event#getType()} method) to load the corresponding registered
     * event listeners. All events fired by this method are delivered to
     * listeners in the order of arrival so even if a listener fires new events
     * then they are added to the event queue and they are really fired only
     * when the previous events are delivered to all listeners.
     * 
     * @param event the event to fire
     */
    public void fireEvent(Event event) {
        fEventQueue.add(event);
        // Event listeners can fire new events.
        if (fEventQueue.size() > 1)
            return;
        try {
            for (int i = 0; i < fEventQueue.size(); i++) {
                event = (Event) fEventQueue.get(i);
                event.setEventManager(this);
                Class type = event.getType();
                List listeners = getListeners(type, false);
                if (listeners != null) {
                    boolean initialized = true;
                    for (Iterator iterator = listeners.iterator(); iterator
                        .hasNext();) {
                        IEventListener listener = (IEventListener) iterator
                            .next();
                        initialized &= listener.prepareEvent(event);
                    }
                    if (initialized) {
                        for (Iterator iterator = listeners.iterator(); iterator
                            .hasNext();) {
                            IEventListener listener = (IEventListener) iterator
                                .next();
                            listener.handleEvent(event);
                        }
                    }
                }
            }
        } finally {
            fEventQueue.clear();
        }
    }

    /**
     * Returns the list of listeners for the specified type of events
     * 
     * @param eventType the type of the event for which the list of listeners
     *        should be returned
     * @param create if this flag is <code>true</code> and there is no list of
     *        listeners then it will be created and added to the internal map
     * @return the list of listeners for the specified type of events
     */
    private List getListeners(Class eventType, boolean create) {
        List list = (List) fListeners.get(eventType);
        if (list == null && create) {
            list = new ArrayList();
            fListeners.put(eventType, list);
        }
        return list;
    }

    /**
     * Removes the specified listener from the list of listeners.
     * 
     * @param eventType the type of events for which the listener should be
     *        removed
     * @param listener the listener to remove
     */
    public void removeListener(Class eventType, IEventListener listener) {
        List list = getListeners(eventType, false);
        if (list != null) {
            list.remove(listener);
        }
    }

}

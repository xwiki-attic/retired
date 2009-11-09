package adnotatio.common.event;

/**
 * This is a default empty ("do-nothing") implementation of the
 * {@link IEventListener} interface. This class can be used as a super-class for
 * user-defined listeners if it is required to define only one of listener
 * methods.
 * <pre>
 * Example:
 * 
 * </pre>
 * 
 * @author kotelnikov
 */
public class EventListener implements IEventListener {

    /**
     * @see adnotatio.common.event.IEventListener#handleEvent(Event)
     */
    public void handleEvent(Event event) {
        //
    }

    /**
     * @see adnotatio.common.event.IEventListener#prepareEvent(Event)
     */
    public boolean prepareEvent(Event event) {
        return true;
    }

}

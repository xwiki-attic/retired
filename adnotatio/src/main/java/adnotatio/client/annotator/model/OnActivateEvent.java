/**
 * 
 */
package adnotatio.client.annotator.model;

import java.util.Collection;

/**
 * This event is fired when a list of annotations is activated.
 * 
 * @author kotelnikov
 */
public class OnActivateEvent extends AnnotationEvent {

    /**
     * The flag defining if the selection should be activated or removed
     */
    public final boolean activate;

    /**
     * @param annotations
     */
    public OnActivateEvent(Collection annotations, boolean activate) {
        super(OnActivateEvent.class, annotations);
        this.activate = activate;
    }

    /**
     * @param annotation
     */
    public OnActivateEvent(Annotation annotation, boolean activate) {
        super(OnActivateEvent.class, annotation);
        this.activate = activate;
    }

}

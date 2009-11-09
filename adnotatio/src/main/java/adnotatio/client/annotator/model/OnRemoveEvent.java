/**
 * 
 */
package adnotatio.client.annotator.model;

import java.util.Collection;

/**
 * Events of this type are fired to notify that annotations was removed from the
 * model
 * 
 * @author kotelnikov
 */
public class OnRemoveEvent extends AnnotationEvent {

    /**
     * @param annotations
     */
    public OnRemoveEvent(Collection annotations) {
        super(OnRemoveEvent.class, annotations);
    }

    /**
     * @param annotation
     */
    public OnRemoveEvent(Annotation annotation) {
        super(OnRemoveEvent.class, annotation);
    }

}

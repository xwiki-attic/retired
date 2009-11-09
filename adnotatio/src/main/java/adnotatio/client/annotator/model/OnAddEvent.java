/**
 * 
 */
package adnotatio.client.annotator.model;

import java.util.Collection;

/**
 * Events of this type are fired to notify that new annotations was added to the
 * model.
 * 
 * @author kotelnikov
 */
public class OnAddEvent extends AnnotationEvent {

    /**
     * @param annotation a newly added annotation firing this event
     */
    public OnAddEvent(Annotation annotation) {
        super(OnAddEvent.class, annotation);
    }

    /**
     * @param annotations a list of annotations added to the model and firing
     *        this event
     */
    public OnAddEvent(Collection annotations) {
        super(OnAddEvent.class, annotations);
    }

}

package adnotatio.client.annotator.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import adnotatio.common.event.Event;

/**
 * The event fired by the {@link AnnotationModel} if something happened with a
 * list of annotations
 * 
 * @author kotelnikov
 */
public abstract class AnnotationEvent extends Event {

    /**
     * The collection of annotations associated with this event
     */
    private final Collection fAnnotations;

    /**
     * This constructor is used to set a unique annotation in the internal
     * annotation list
     * 
     * @param annotation the annotation
     */
    public AnnotationEvent(Class type, Annotation annotation) {
        super(type);
        fAnnotations = new ArrayList();
        fAnnotations.add(annotation);
    }

    /**
     * This constructor is used to set the specified annotations in the internal
     * annotation list
     * 
     * @param annotations a list of annotations
     */
    public AnnotationEvent(Class type, Collection annotations) {
        super(type);
        fAnnotations = annotations;
    }

    /**
     * Returns the first annotation from the internal collection
     * 
     * @return the first annotation from the internal collection
     */
    public Annotation getAnnotation() {
        Iterator iterator = fAnnotations.iterator();
        return (Annotation) (iterator.hasNext() ? iterator.next() : null);
    }

    /**
     * Returns a collection of all annotations firing this event
     * 
     * @return a collection of all annotations firing this event
     */
    public Collection getAnnotations() {
        return fAnnotations;
    }

}

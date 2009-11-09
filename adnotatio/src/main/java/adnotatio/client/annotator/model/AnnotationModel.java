/**
 * 
 */
package adnotatio.client.annotator.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import adnotatio.client.annotator.util.RangeList;
import adnotatio.common.event.EventManager;

/**
 * This model is used as a container for all annotations and as a manager of
 * annotation events.
 * 
 * @author kotelnikov
 */
public class AnnotationModel extends EventManager {

    /**
     * This class is used to work around the bug of the GWT script generator: it
     * generates infinite recursive calls for the compareTo methods in the
     * Integer class.
     * 
     * @author kotelnikov
     */
    private static class Pos implements Comparable {

        /**
         * The wrapped position
         */
        private int fValue;
        
        /**
         * @param value
         */
        public Pos(int value) {
            fValue = value;
        }
        
        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o) {
            Pos p = (Pos) o;
            return fValue - p.fValue;
        }
        
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Pos))
                return false;
            return compareTo((Pos) obj) == 0;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return fValue;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return fValue + "";
        }
    }

    /**
     * A list of all annotations managed by this model
     */
    private Set fAnnotations = new HashSet();

    /**
     * This object is used to manage annotation ranges
     */
    private RangeList fRanges = new RangeList();

    /**
     * The default constructor
     */
    public AnnotationModel() {
        super();
    }

    /**
     * Adds the specified annotation and fires {@link OnAddEvent} event.
     * 
     * @param annotation the annotation to add
     */
    public void addAnnotation(Annotation annotation) {
        List list = new ArrayList();
        list.add(annotation);
        addAnnotations(list);
    }

    /**
     * Adds annotations and fires the {@link OnAddEvent} event.
     * 
     * @param annotations a list of annotations to add
     */
    public void addAnnotations(Collection annotations) {
        fAnnotations.addAll(annotations);
        for (Iterator iterator = annotations.iterator(); iterator.hasNext();) {
            Annotation annotation = (Annotation) iterator.next();
            int from = annotation.getStartPosition();
            Pos begin = newPos(from);
            int to = from + annotation.getLength();
            Pos end = newPos(to);
            fRanges.add(begin, end, annotation);
        }
        fireEvent(new OnAddEvent(annotations));
    }

    /**
     * Cleans up all annotations and fires the event notifying all listeners
     */
    public void clear() {
        fireEvent(new OnRemoveEvent(fAnnotations));
        fAnnotations.clear();
        fRanges.clear();
    }

    /**
     * Returns a collection containing all annotations managed by this model.
     * The order of annotations in the collection is not specified.
     * 
     * @return a collection containing all annotations managed by this model
     */
    public Collection getAnnotations() {
        return fAnnotations;
    }

    /**
     * Returns a set of annotations containing a word in the specified position
     * 
     * @param wordPos the position of the word for which the set of annotation
     *        should be returned
     * @return a set of annotations containing a word in the specified position
     */
    public Set getAnnotations(int wordPos) {
        return getAnnotations(wordPos, 1);
    }

    /**
     * Returns a set of annotations associated with the specified range of words
     * 
     * @param firstWordPos the first range position
     * @param len the length of the range
     * @return a set of annotations associated with the specified range of words
     */
    public Set getAnnotations(int firstWordPos, int len) {
        List list = new ArrayList();
        Pos from = newPos(firstWordPos);
        Pos to = new Pos(firstWordPos + len);
        Set set = fRanges.getRanges(from, to);
        return set;
    }

    /**
     * Creates and returns a new position wrapper
     * 
     * @param pos the position object
     * @return a new position wrapper
     */
    private Pos newPos(int pos) {
        return new Pos(pos);
    }

    /**
     * Removes the specified annotation and fires {@link OnRemoveEvent} if the
     * annotation really was in the list.
     * 
     * @param annotation the annotation to remove
     */
    public void removeAnnotation(Annotation annotation) {
        if (fAnnotations.contains(annotation)) {
            List list = new ArrayList();
            list.add(annotation);
            removeAnnotations(list);
        }
    }

    /**
     * Adds annotations and fires the {@link OnRemoveEvent} event.
     * 
     * @param annotations a list of annotations to add
     */
    public void removeAnnotations(Collection annotations) {
        fireEvent(new OnRemoveEvent(annotations));
        for (Iterator iterator = annotations.iterator(); iterator.hasNext();) {
            Annotation annotation = (Annotation) iterator.next();
            int from = annotation.getStartPosition();
            Pos begin = newPos(from);
            int to = from + annotation.getLength();
            Pos end = newPos(to);
            fRanges.remove(begin, end, annotation);
        }
        fAnnotations.removeAll(annotations);
    }

    /**
     * Replaces old annotations by the given and fires the {@link OnAddEvent}
     * event for new annotations and the {@link OnRemoveEvent} event for all
     * removed annotations.
     * 
     * @param annotations a list of annotations to set
     */
    public void setAnnotations(Collection annotations) {
        clear();
        addAnnotations(annotations);
    }
}

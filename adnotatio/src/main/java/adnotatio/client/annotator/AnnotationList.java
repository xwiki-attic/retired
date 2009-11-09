/**
 * 
 */
package adnotatio.client.annotator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import adnotatio.client.annotator.model.Annotation;
import adnotatio.client.annotator.model.AnnotationEvent;
import adnotatio.client.annotator.model.AnnotationModel;
import adnotatio.client.annotator.model.OnActivateEvent;
import adnotatio.client.annotator.model.OnAddEvent;
import adnotatio.client.annotator.model.OnRemoveEvent;
import adnotatio.client.annotator.util.CollectionsUtil;
import adnotatio.common.event.Event;
import adnotatio.common.event.EventListener;
import adnotatio.renderer.templates.TemplateBuilder;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * This panel contains a list of all annotations. It catches the following
 * events:
 * <ul>
 * <li>{@link OnAddEvent} - visualizes newly created annotations</li>
 * <li>{@link OnRemoveEvent} - removes visualization of removed annotations</li>
 * <li>{@link OnActivateEvent} - highlights active annotations</li>
 * <li>{@link OnActivateEvent} - highlights active annotations</li>
 * </ul>
 * 
 * @author kotelnikov
 */
class AnnotationList extends Composite {

    /**
     * This map contains annotations and their respective views
     */
    private Map fMap = new HashMap();

    /**
     * The datamodel used to fire/catch events and to retrieve lists of
     * annotations
     */
    private AnnotationModel fModel;

    /**
     * The main panel containing all annotation widgets.
     */
    FlowPanel fPanel = new FlowPanel();

    /**
     * This template builder is used to create widgets for individual
     * annotations
     */
    TemplateBuilder fTemplateBuilder;

    /**
     * This constructor initializes internal fields
     * 
     * @param model the datamodel used to catch/fire events
     */
    public AnnotationList(AnnotationModel model) {
        initWidget(fPanel);
        fModel = model;
        // Adds widgets for new annotations
        fModel.addListener(OnAddEvent.class, new EventListener() {
            public void handleEvent(Event event) {
                AnnotationEvent e = (AnnotationEvent) event;
                Collection collection = e.getAnnotations();
                addAnnotations(collection);
            }
        });
        // Removes widgets corresponding to removed annotations
        fModel.addListener(OnRemoveEvent.class, new EventListener() {
            public void handleEvent(Event event) {
                AnnotationEvent e = (AnnotationEvent) event;
                Collection collection = e.getAnnotations();
                removeAnnotations(collection);
            }
        });
        // Sets/removes highlighting for active annotations
        fModel.addListener(OnActivateEvent.class, new EventListener() {

            /**
             * List of old highlighted annotations. This list is used to remove
             * selections when new annotations was activated.
             */
            Collection fOldCollection = new HashSet();

            /**
             * @see adnotatio.common.event.EventListener#handleEvent(adnotatio.common.event.Event)
             */
            public void handleEvent(Event event) {
                AnnotationEvent e = (AnnotationEvent) event;
                Collection collection = e.getAnnotations();
                select(collection, fOldCollection);
                fOldCollection.clear();
                fOldCollection.addAll(collection);
            }
        });
        Collection annotations = fModel.getAnnotations();
        addAnnotations(annotations);
    }

    /**
     * This method creates a new widget corresponding to the specified
     * annotation and adds it to the internal map. This map is used to
     * activate/deactivate/remove this widget when some operations happens with
     * the annotaiton.
     * 
     * @param annotation the annotation to add to visualize
     */
    private void addAnnotation(Annotation annotation) {
        if (fTemplateBuilder == null)
            return;
        AnnotationView view = new AnnotationView(fModel, fTemplateBuilder);
        view.setAnnotation(annotation);
        fPanel.insert(view, 0);
        fMap.put(annotation, view);
    }

    /**
     * Creates widgets for all annotations in the specified collection and adds
     * them to the internal panel and map.
     * 
     * @param collection the collection containing annotations
     */
    private void addAnnotations(Collection collection) {
        if (fTemplateBuilder == null)
            return;
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Annotation annotation = (Annotation) iterator.next();
            addAnnotation(annotation);
        }
    }

    /**
     * Returns the name of the CSS style which is added to all widgets
     * corresponding to selected annotations
     * 
     * @return the name of the CSS style which is added to all widgets
     *         corresponding to selected annotations
     */
    protected String getHighlightStyleName() {
        return "highlight";
    }

    /**
     * Orders all annotation widgets based on values of the specified annotation
     * fields
     * 
     * @param fieldName the name of the field used to order annotations
     * @param inc if this flag is <code>true</code> then widgets will be
     *        ordered in the incremental order
     */
    public void orderByField(final String fieldName, final boolean inc) {
        orderViews(new Comparator() {
            public int compare(Object o1, Object o2) {
                Map.Entry first = (Entry) o1;
                Map.Entry second = (Entry) o2;
                Annotation a1 = (Annotation) first.getKey();
                Annotation a2 = (Annotation) second.getKey();
                Object v1 = a1.getProperties().getValue(fieldName);
                Object v2 = a2.getProperties().getValue(fieldName);
                Comparable c1 = (Comparable) v1;
                Comparable c2 = (Comparable) v2;
                int result = c1 != null && c2 != null
                    ? c1.compareTo(c2)
                    : c1 == c2 ? 0 : c1 != null ? 1 : -1;
                return inc ? result : -result;
            }
        });
    }

    /**
     * Puts all annotation views in the order of annotations (orders them by
     * start positions of annotations)
     * 
     * @param inc if this flag is <code>true</code> then all views will be
     *        ordered in the incremental order of start positions
     */
    public void orderByPosition(final boolean inc) {
        orderViews(new Comparator() {
            public int compare(Object o1, Object o2) {
                Map.Entry first = (Entry) o1;
                Map.Entry second = (Entry) o2;
                Annotation a1 = (Annotation) first.getKey();
                Annotation a2 = (Annotation) second.getKey();
                int result = a1.getStartPosition() - a2.getStartPosition();
                return inc ? result : -result;
            }
        });
    }

    /**
     * Orders all annotation views based on the specified comparator
     * 
     * @param c the comparator used to compare annotations
     */
    protected void orderViews(Comparator c) {
        List list = new ArrayList();
        for (Iterator iterator = fMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Entry) iterator.next();
            int pos = CollectionsUtil.search(list, entry, c);
            if (pos < 0) {
                pos = -(pos + 1);
            }
            list.add(pos, entry);
        }
        fPanel.clear();
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            Map.Entry entry = (Entry) iterator.next();
            AnnotationView view = (AnnotationView) entry.getValue();
            fPanel.add(view);
        }
    }

    /**
     * Removes the view corresponding to the given annotation from the internal
     * panel and unregisteres this annotation from the internal map.
     * 
     * @param annotation the annotation to remove
     */
    private void removeAnnotation(Annotation annotation) {
        if (fTemplateBuilder == null)
            return;
        AnnotationView view = (AnnotationView) fMap.remove(annotation);
        if (view != null) {
            fPanel.remove(view);
        }
    }

    /**
     * Removes all annotation widgets corresponding to the annotations in the
     * given collection and unregisteres these annotations from the internal map
     * 
     * @param collection the collection with annotations to unregister
     */
    private void removeAnnotations(Collection collection) {
        if (fTemplateBuilder == null)
            return;
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Annotation annotation = (Annotation) iterator.next();
            removeAnnotation(annotation);
        }
    }

    /**
     * Highlights all widgets corresponding to selected annotations and removes
     * highlighting for unselected annotations
     * 
     * @param set collection of all annotations to highlight
     * @param remove collection of all annotations for which highlighting should
     *        be removed
     */
    private void select(Collection set, Collection remove) {
        String style = getHighlightStyleName();
        if (remove != null) {
            for (Iterator iterator = remove.iterator(); iterator.hasNext();) {
                Annotation annotation = (Annotation) iterator.next();
                AnnotationView view = (AnnotationView) fMap.get(annotation);
                if (view != null) {
                    view.removeStyleName(style);
                }
            }
        }
        if (set != null) {
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                Annotation annotation = (Annotation) iterator.next();
                AnnotationView view = (AnnotationView) fMap.get(annotation);
                if (view != null) {
                    view.addStyleName(style);
                }
            }
        }
    }

    /**
     * Sets the template builder used to create individual annotation views.
     * 
     * @param templateBuilder the template builder to set
     */
    public void setAnnotationTemplate(TemplateBuilder templateBuilder) {
        fTemplateBuilder = templateBuilder;
        Collection annotations = fModel.getAnnotations();
        addAnnotations(annotations);
    }

}

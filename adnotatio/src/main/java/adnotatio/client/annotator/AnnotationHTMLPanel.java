/**
 * 
 */
package adnotatio.client.annotator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import adnotatio.client.annotator.model.Annotation;
import adnotatio.client.annotator.model.AnnotationEvent;
import adnotatio.client.annotator.model.AnnotationModel;
import adnotatio.client.annotator.model.OnActivateEvent;
import adnotatio.client.annotator.model.OnAddEvent;
import adnotatio.client.annotator.model.OnEditEvent;
import adnotatio.client.annotator.model.OnRemoveEvent;
import adnotatio.client.annotator.model.OnSelectionChangeEvent;
import adnotatio.client.annotator.util.ui.ActiveHTMLPanel;
import adnotatio.common.event.Event;
import adnotatio.common.event.EventListener;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;

/**
 * This panel is used as a container for the HTML content to annotate. It
 * automatically highlights all newly added annotations, removes highlighting
 * for removed annotations, a changes highlighting when an annotation is
 * activated or edited. This panel fires the following events:
 * <ul>
 * <li>{@link OnSelectionChangeEvent} - this event is fired when the text
 * selection of the content to annotate is changed</li>
 * <li>{@link OnActivateEvent} - this event is fired when user clicks on the
 * text covered by one or many annotations</li>
 * </ul>
 * 
 * @author kotelnikov
 */
class AnnotationHTMLPanel extends Composite {

    /**
     * This static counter is used to generate unique identifiers for content
     * panels
     */
    private static int fCounter;

    /**
     * The annotation model; it is used to catch/fire annotation events
     */
    private AnnotationModel fModel;

    /**
     * This is an active panel containing the HTML text to annotate. It
     * automatically fires new {@link OnSelectionChangeEvent} events when the
     * text selection is changed.
     */
    ActiveHTMLPanel fPanel = new ActiveHTMLPanel("ap-id-" + (fCounter++)) {

        /**
         * The length (in words) of the selection.
         */
        private int fSelectionLen = -1;

        /**
         * The first word position covered by the selection.
         */
        private int fSelectionPos = -1;

        /**
         * This timer periodically checks the selection of the text and if the
         * selection was changed then it fires the
         * {@link OnSelectionChangeEvent} event on the model.
         */
        Timer fTimer = new Timer() {
            public void run() {
                WordSelection selection = getWordSelection();
                int startPos = selection.getFirstWordPos();
                int len = selection.getLength();
                String text = selection.getText();
                if (fSelectionPos != startPos || fSelectionLen != len) {
                    boolean enabled = startPos >= 0 && len > 0;
                    OnSelectionChangeEvent event = new OnSelectionChangeEvent(
                        enabled);
                    if (enabled) {
                        event.setSelectionPos(startPos);
                        event.setSelectionLen(len);
                        event.setSelectionText(text);
                    }
                    fModel.fireEvent(event);
                }
                fSelectionPos = startPos;
                fSelectionLen = len;
            }
        };

        /**
         * This method starts the internal timer checking the text selection
         * when this widget is attached to the DOM.
         * 
         * @see adnotatio.client.annotator.util.ui.ActiveHTMLPanel#onLoad()
         */
        protected void onLoad() {
            fTimer.scheduleRepeating(100);
        }

        /**
         * When this widget is detached from the DOM this method stops the
         * internal timer checking the text selection.
         * 
         * @see com.google.gwt.user.client.ui.Widget#onUnload()
         */
        protected void onUnload() {
            fTimer.cancel();
        }
    };

    /**
     * The default constructor initializing internal fields and initializes
     * model listeners. These listeners change the content panel visualization
     * when annotations are added/removed to the model or when an annotation is
     * activated/deactivated.
     * 
     * @param model the annotation model; it is used to catch/fire events
     */
    public AnnotationHTMLPanel(AnnotationModel model) {
        fModel = model;
        initWidget(fPanel);
        fPanel.addWordClickListener(new ActiveHTMLPanel.IWordClickListener() {
            public void onWordClick(ActiveHTMLPanel widget, int wordPos) {
                Set annotations = fModel.getAnnotations(wordPos);
                fModel.fireEvent(new OnActivateEvent(annotations, true));
            }
        });

        // Highlights the text with a specific style the edited annotation
        fModel.addListener(OnEditEvent.class, new EventListener() {

            /**
             * Collection of annotations highlighted with a specific editing
             * style
             */
            private Collection fEdited;

            /**
             * @see adnotatio.common.event.EventListener#handleEvent(adnotatio.common.event.Event)
             */
            public void handleEvent(Event event) {
                OnEditEvent e = (OnEditEvent) event;
                Collection edited = e.getAnnotations();
                String style = getStyleEdited();
                if (e.stage == OnEditEvent.BEGIN) {
                    highlight(edited, fEdited, style);
                    fEdited = edited;
                } else {
                    highlight(null, edited, style);
                    highlight(null, fEdited, style);
                    fEdited = null;
                }
            }
        });
        // Highlights newly added annotations in the text
        fModel.addListener(OnAddEvent.class, new EventListener() {
            public void handleEvent(Event event) {
                AnnotationEvent e = (AnnotationEvent) event;
                Collection c = e.getAnnotations();
                highlight(c, null, getStyleSelected());
            }
        });
        // Removes text highlighting when annotations are removed
        fModel.addListener(OnRemoveEvent.class, new EventListener() {
            public void handleEvent(Event event) {
                AnnotationEvent e = (AnnotationEvent) event;
                Collection c = e.getAnnotations();
                highlight(null, c, getStyleSelected());
            }
        });
        // Sets specific styles for active (selected) annotations and removes
        // highlighting for deactivated annotations.
        fModel.addListener(OnActivateEvent.class, new EventListener() {

            /**
             * Collection of active annotations. It is used to remove old
             * highlighting when new annotations are selected.
             */
            Collection fActiveAnnotations;

            /**
             * @see adnotatio.common.event.EventListener#handleEvent(adnotatio.common.event.Event)
             */
            public void handleEvent(Event event) {
                AnnotationEvent e = (AnnotationEvent) event;
                Collection c = e.getAnnotations();
                highlight(c, fActiveAnnotations, getStyleHighlighted());
                fActiveAnnotations = c;
            }
        });

    }

    /**
     * Adds highlighting for words covered by the specified annotation.
     * 
     * @param annotation the annotation for which the text highlighting should
     *        be added
     * @param style the name of the CSS style to add to the highlighted words
     */
    private void addHighlight(Annotation annotation, String style) {
        int start = annotation.getStartPosition();
        int len = annotation.getLength();
        fPanel.addWordStyle(start, len, style, null);
    }

    /**
     * Returns the name of the CSS style used to highlight words covered by the
     * edited annotation
     * 
     * @return the name of the CSS style added to all words covered by the
     *         edited annotation
     */
    protected String getStyleEdited() {
        return "edited";
    }

    /**
     * Returns the name of the CSS style used to highlight words covered by an
     * annotation
     * 
     * @return the name of the CSS style added to all words covered by an
     *         annotation
     */
    protected String getStyleHighlighted() {
        return "highlight";
    }

    /**
     * Returns the name of the CSS style used to highlight words covered by a
     * selected (currently active) annotation
     * 
     * @return the name of the CSS style used to highlight words covered by a
     *         selected (currently active) annotation
     */
    protected String getStyleSelected() {
        return "selected";
    }

    /**
     * Adds the specified CSS style for words covered by annotations from the
     * <code>set</code> collection and removes this style for all words
     * covered by annotations from the <code>remove</code> collection.
     * 
     * @param set this collection contains annotations for which the specified
     *        CSS style should be added
     * @param remove this is a collection with annotations for which the
     *        specified CSS style should be removed
     * @param style the name of the CSS style to add/remove
     */
    private void highlight(Collection set, Collection remove, String style) {
        if (remove != null) {
            for (Iterator iterator = remove.iterator(); iterator.hasNext();) {
                Annotation annotation = (Annotation) iterator.next();
                removeHighlight(annotation, style);
            }
        }
        if (set != null) {
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                Annotation annotation = (Annotation) iterator.next();
                addHighlight(annotation, style);
            }
        }
    }

    /**
     * Removes CSS highlighting for words covered by the specified annotation.
     * 
     * @param annotation the annotation for which the text highlighting should
     *        be remove
     * @param style the name of the CSS style to remove from the highlighted
     *        words
     */
    private void removeHighlight(Annotation annotation, String style) {
        int start = annotation.getStartPosition();
        int len = annotation.getLength();
        fPanel.removeWordStyle(start, len, style, null);
    }

    /**
     * Sets the HTML content to annotate
     * 
     * @param html the HTML content to annotate
     */
    public void setHTML(String html) {
        fPanel.setHTML(html);
        Collection c = fModel.getAnnotations();
        highlight(c, null, getStyleSelected());
    }
}

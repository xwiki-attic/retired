/**
 * 
 */
package adnotatio.client.annotator;

import adnotatio.client.annotator.model.Annotation;
import adnotatio.client.annotator.model.AnnotationModel;
import adnotatio.client.annotator.model.OnEditEvent;
import adnotatio.client.annotator.model.OnSelectionChangeEvent;
import adnotatio.common.event.Event;
import adnotatio.common.event.EventListener;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * This type of buttons subscribe themselves as listeners of the
 * {@link OnSelectionChangeEvent} events and they became active only when a
 * selection is available; these buttons fire {@link OnEditEvent#BEGIN} events
 * in their click listeners.
 * 
 * @author kotelnikov
 */
class AnnotationButton extends Button {

    /**
     * The last catched selection change event
     */
    private OnSelectionChangeEvent fEvent;

    /**
     * The annotation model; it is used to catch and fire events
     */
    private AnnotationModel fModel;

    /**
     * The default constructor
     * 
     * @param model the annotation model
     * @param label the label for this button
     */
    public AnnotationButton(AnnotationModel model, String label) {
        super(label);
        fModel = model;
        fModel.addListener(OnSelectionChangeEvent.class, new EventListener() {
            public void handleEvent(Event event) {
                fEvent = (OnSelectionChangeEvent) event;
                setEnabled(fEvent.isEnabled());
            }
        });
        addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                Annotation annotation = new Annotation(
                    fEvent.getSelectionPos(),
                    fEvent.getSelectionLen(),
                    fEvent.getSelectionText());
                fModel
                    .fireEvent(new OnEditEvent(annotation, OnEditEvent.BEGIN));
            }
        });

    }
}
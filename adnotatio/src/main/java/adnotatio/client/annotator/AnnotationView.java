package adnotatio.client.annotator;

import adnotatio.client.annotator.model.AnnotationModel;
import adnotatio.client.annotator.model.Annotation;
import adnotatio.client.annotator.model.OnActivateEvent;
import adnotatio.client.app.FieldPanelUtil;
import adnotatio.renderer.templates.FieldPanel;
import adnotatio.renderer.templates.TemplateBuilder;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget defines the visualization of individual annotations. This widget
 * fires the {@link OnActivateEvent} when user clicks on the annotation
 * description.
 * 
 * @author kotelnikov
 */
class AnnotationView extends Composite {

    /**
     * The name of the template used to visualize the internal annotation.
     */
    public static final String ANNOTATION_VIEW = "annotationView";

    /**
     * The annotation to visualize
     */
    private Annotation fAnnotation;

    /**
     * The form widget used to visualize the template corresponding to
     * individual annotations
     */
    private FieldPanel fForm;

    /**
     * The annotation model
     */
    private AnnotationModel fModel;

    /**
     * The main panel containing the annotation widget. It is used to handle
     * mouse clicks to activate/deactivate the internal annotation.
     */
    FocusPanel fPanel = new FocusPanel();

    /**
     * The default constructor initializing internal panels and defining
     * listeners.
     * 
     * @param model
     * @param builder
     */
    public AnnotationView(AnnotationModel model, TemplateBuilder builder) {
        fModel = model;
        fForm = builder.buildPanel(ANNOTATION_VIEW);
        fPanel.add(fForm);
        initWidget(fPanel);
        fPanel.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                fModel.fireEvent(new OnActivateEvent(fAnnotation, true));
            }
        });
    }

    /**
     * Sets a new annotation and updates the internal fields of all internal
     * widgets.
     * 
     * @param annotation the annotation to set
     */
    public void setAnnotation(Annotation annotation) {
        fAnnotation = annotation;
        FieldPanelUtil.setFieldValues(fForm, annotation.getProperties());
    }

}

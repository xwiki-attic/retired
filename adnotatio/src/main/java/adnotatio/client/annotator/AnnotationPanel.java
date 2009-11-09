/**
 * 
 */
package adnotatio.client.annotator;

import adnotatio.client.annotator.model.AnnotationModel;
import adnotatio.client.annotator.model.OnEditEvent;
import adnotatio.renderer.templates.TemplateBuilder;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * This is the annotation panel defining the main layout of the content to
 * annotate, annotation forms, buttons activating the annotation process and
 * panels containing existing annotations.
 * 
 * @author kotelnikov
 */
class AnnotationPanel extends Composite {

    /**
     * The internal scroll panel.
     * 
     * @author kotelnikov
     */
    static class ContainerPanel extends ScrollPanel {
    }

    /**
     * The annotation form widget. Normally this widget is hidden until the
     * {@link OnEditEvent} is fired.
     */
    protected AnnotationForm fAnnotationForm;

    /**
     * This widget contains a list of all widgets visualizing individual
     * annotations.
     */
    private AnnotationList fAnnotationList;

    /**
     * The main panel regrouping the annotation button, annotation form and the
     * list of annotation views.
     */
    private FlowPanel fAnnotationWidgets = new FlowPanel();

    /**
     * The scroll panel used as a container for the {@link #fAnnotationWidgets}
     * panel
     */
    private ContainerPanel fAnnotationWidgetScroll = new ContainerPanel();

    /**
     * The annotation button initializing the annotation editing process
     */
    private AnnotationButton fButton;

    /**
     * This panel contains the HTML content to annotate
     */
    private AnnotationHTMLPanel fContentPanel;

    /**
     * The scroll panel containing the {@link #fContentPanel} panel
     */
    private ContainerPanel fContentScroll = new ContainerPanel();

    /**
     * The main layout used to put on the screen all individual elements
     */
    private DockPanel fLayout = new DockPanel();

    /**
     * The model used to catch/fire events and to have access to annotations
     */
    private AnnotationModel fModel;

    /**
     * The main constructor
     * 
     * @param model the annotation model
     */
    public AnnotationPanel(AnnotationModel model) {
        fModel = model;
        fAnnotationList = new AnnotationList(fModel);
        fAnnotationForm = new AnnotationForm(fModel);
        fButton = new AnnotationButton(fModel, "Annotate");
        fContentPanel = new AnnotationHTMLPanel(fModel);

        initWidget(fLayout);
        fLayout.setBorderWidth(0);
        fLayout.setSpacing(5);

        fContentScroll.add(fContentPanel);
        fLayout.add(fContentScroll, DockPanel.CENTER);

        fAnnotationWidgetScroll.add(fAnnotationWidgets);
        fLayout.add(fAnnotationWidgetScroll, DockPanel.WEST);

        fAnnotationWidgets.add(fButton);
        fAnnotationWidgets.add(fAnnotationForm);
        fAnnotationWidgets.add(fAnnotationList);

        setHeight(500);
        setWidth(750);
    }

    /**
     * Enables the editing form in this panel
     * 
     * @param enabled if this flag is <code>true</code> then the editing form
     *        will be activated; otherwise the form will be disabled
     */
    public void setEnabled(boolean enabled) {
        fAnnotationForm.setEnabled(enabled);
    }

    /**
     * Sets the heights of this widget. It defines the heights of internal
     * scrollers with the main content and with the annotation list.
     * 
     * @param height the height to set
     */
    public void setHeight(int height) {
        String h = (height - 30) + "px";
        fLayout.setHeight(h);
        fAnnotationWidgetScroll.setHeight(h);
        fContentScroll.setHeight(h);
    }

    /**
     * Sets the HTML content to annotate.
     * 
     * @param html the HTML content to annotate
     */
    // TODO: move this operation in the model and make the content available
    // using events
    public void setHTML(String html) {
        fContentPanel.setHTML(html);
    }

    /**
     * Sets the template builder used to create views for individual annotations
     * and for annotation editing forms
     * 
     * @param templateBuilder the builder to set
     */
    public void setTemplateBuilder(TemplateBuilder templateBuilder) {
        fAnnotationForm.setFormTemplate(templateBuilder);
        fAnnotationList.setAnnotationTemplate(templateBuilder);
    }

    /**
     * Sets the width of this panel. It changes the size of scrollers for the
     * content and for the list of annotations
     * 
     * @param width the width to set
     */
    public void setWidth(int width) {
        width -= 30;
        int w = (width / 3);
        fAnnotationWidgetScroll.setWidth(w + "px");
        w = width - w;
        fContentScroll.setWidth(w + "px");
    }

}

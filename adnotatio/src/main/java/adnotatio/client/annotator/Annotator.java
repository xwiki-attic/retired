/**
 * 
 */
package adnotatio.client.annotator;

import java.util.ArrayList;
import java.util.List;

import adnotatio.client.annotator.model.Annotation;
import adnotatio.client.annotator.model.AnnotationModel;
import adnotatio.client.annotator.model.OnActivateEvent;
import adnotatio.client.annotator.model.OnEditEvent;
import adnotatio.common.data.IPropertiesContainer;
import adnotatio.common.event.Event;
import adnotatio.common.event.EventListener;
import adnotatio.common.io.ResourceLoaderBarrier;
import adnotatio.common.io.TextLoader;
import adnotatio.common.io.XMLLoader;
import adnotatio.common.xml.IXmlDocument;
import adnotatio.common.xml.IXmlElement;
import adnotatio.common.xml.SerializationUtil;
import adnotatio.common.xml.gwt.GwtXmlDocument;
import adnotatio.common.xml.gwt.GwtXmlElement;
import adnotatio.renderer.templates.TemplateBuilder;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

/**
 * This is the main panel defining the behaviour of the annotator. This panel
 * uses the given annotator configuration to get the names of services providing
 * individual templates and the information to visualize (like the main content,
 * annotations corresponding to this content, services responsable for
 * storing/formatting of newly added annotations and so on).
 * 
 * @author kotelnikov
 */
public class Annotator extends Composite {

    /**
     * The main callback class used as the main superclass for all internal
     * callbacks. This callback object delegates failure handling to the
     * configuration object.
     * 
     * @author kotelnikov
     */
    abstract class Callback implements AsyncCallback {
        public void onFailure(Throwable caught) {
            fConfig.handleError(caught);
        }
    }

    /**
     * The annotation model
     */
    AnnotationModel fAnnotationModel = new AnnotationModel();

    /**
     * This panel contains annotations, the annotated content, annotation forms
     * and buttons activating the annotation process.
     */
    AnnotationPanel fAnnotationPanel = new AnnotationPanel(fAnnotationModel);

    /**
     * The configuration defining all used services. This configuration is
     * loaded from the "resources" java script variable defined in the main HTML
     * page.
     */
    AnnotatorConfig fConfig = new AnnotatorConfig("resources");

    /**
     * The content to annotate.
     */
    private String fContent;

    /**
     * This callback is notified when formatted annotations are returned by the
     * server. It is used when a list of annotations is initially loaded or when
     * new annotations are stored on the server and their formatted
     * representations are returned.
     */
    AsyncCallback fReadCallback = new Callback() {
        {
            fAnnotationPanel.setEnabled(false);
        }

        /**
         * @see adnotatio.client.annotator.Annotator.Callback#onFailure(java.lang.Throwable)
         */
        public void onFailure(Throwable caught) {
            try {
                super.onFailure(caught);
            } finally {
                fAnnotationPanel.setEnabled(true);
            }
        }

        /**
         * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
         */
        public void onSuccess(Object o) {
            try {
                IXmlElement e = new GwtXmlElement(
                    (com.google.gwt.xml.client.Element) o);
                List list = new ArrayList();
                SerializationUtil util = SerializationUtil.getInstance();
                util.deserializePropertiesList(e, list);
                for (int i = 0; i < list.size(); i++) {
                    IPropertiesContainer container = (IPropertiesContainer) list
                        .get(i);
                    Annotation annotation = new Annotation(container);
                    list.set(i, annotation);
                }
                if (list.size() > 0) {
                    fAnnotationModel.addAnnotations(list);
                    Annotation annotation = (Annotation) list.get(0);
                    fAnnotationModel.fireEvent(new OnActivateEvent(
                        annotation,
                        true));
                }
            } finally {
                fAnnotationPanel.setEnabled(true);
            }
        }
    };

    /**
     * Main constructor initializing internal fields and adding listeners for
     * the {@link OnEditEvent#SAVE} event. This listener uses the remove service
     * defined by the configuration to store the content of saved annotations.
     * 
     * @param config the configuration object
     */
    public Annotator(AnnotatorConfig config) {
        fConfig = config;
        FlowPanel panel = new FlowPanel();
        panel.add(fAnnotationPanel);
        initWidget(panel);

        Window.addWindowResizeListener(new WindowResizeListener() {
            public void onWindowResized(int width, int height) {
                updateAnnotationPanelDimensions();
            }
        });
        // Adds a listener for the {@link OnEditEvent#SAVE} event. This listener
        // sends the content of annotations to save on the server.
        fAnnotationModel.addListener(OnEditEvent.class, new EventListener() {
            public void handleEvent(Event event) {
                OnEditEvent e = (OnEditEvent) event;
                if (e.stage != OnEditEvent.SAVE)
                    return;
                Annotation a = e.getAnnotation();
                Document doc = XMLParser.createDocument();
                IXmlDocument document = new GwtXmlDocument(doc);
                List list = new ArrayList();
                list.add(a.getProperties());
                SerializationUtil.getInstance().serializeAnnotations(
                    document,
                    list);

                String content = document.toString();
                XMLLoader loader = new XMLLoader(fConfig.getWriteService());
                loader.load(content, fReadCallback);
            }
        });

        // Reloads all templates, annotations and the content to annotate from
        // the remote server
        reloadConfiguration();
    }

    /**
     * This method resizes the annotation panel. The annotation panel should fit
     * to the whole screen.
     * 
     * @see com.google.gwt.user.client.ui.Widget#onLoad()
     */
    protected void onLoad() {
        updateAnnotationPanelDimensions();
    }

    /**
     * This method calls multiple remote services and loads all templates,
     * annotations and the content to annotate from the remote server. When this
     * information is loaded then this method activates all internal panels.
     */
    private void reloadConfiguration() {
        // This barrier sets template builder to the internal fields when all
        // templates are successfully loaded
        ResourceLoaderBarrier barrier = new ResourceLoaderBarrier() {
            /**
             * This method is called when all asynchronous loading processes are
             * finished.
             * 
             * @see adnotatio.common.io.ResourceLoaderBarrier#onFinish(java.util.List,
             *      java.util.List)
             */
            protected void onFinish(List successList, List failureList) {
                fAnnotationPanel.setHTML(fContent);
                // Now the internal template builder is initialized so it can be
                // used to create widgets
                TemplateBuilder templateBuilder = fConfig.getTemplateBuilder();
                fAnnotationPanel.setTemplateBuilder(templateBuilder);
            }
        };
        // Loads the template used to visualize annotation editing form
        barrier.add(new XMLLoader(fConfig.getAnnotationForm()), new Callback() {
            public void onSuccess(Object o) {
                IXmlElement e = new GwtXmlElement(
                    (com.google.gwt.xml.client.Element) o);
                TemplateBuilder templateBuilder = fConfig.getTemplateBuilder();
                templateBuilder.addTemplate(AnnotationForm.ANNOTATION_FORM, e);
            }
        });
        // Loads the view template used to visualize individual annotations
        barrier.add(new XMLLoader(fConfig.getAnnotationView()), new Callback() {
            public void onSuccess(Object o) {
                IXmlElement e = new GwtXmlElement(
                    (com.google.gwt.xml.client.Element) o);
                TemplateBuilder templateBuilder = fConfig.getTemplateBuilder();
                templateBuilder.addTemplate(AnnotationView.ANNOTATION_VIEW, e);
            }
        });
        // Loads the annotated content
        barrier.add(
            new TextLoader(fConfig.getContentService()),
            new Callback() {
                public void onSuccess(Object o) {
                    fContent = (String) o;
                }
            });
        // Loads the list of all annotations
        barrier.add(new XMLLoader(fConfig.getReadService()), fReadCallback);

        // Starts the loading process
        barrier.load();
    }

    /**
     * Updates dimensions of the internal AnnotationPanel to fit to the whole
     * screen.
     */
    private void updateAnnotationPanelDimensions() {
        int height = Window.getClientHeight();
        int width = Window.getClientWidth();
        fAnnotationPanel.setHeight(height);
        fAnnotationPanel.setWidth(width);
    }

}

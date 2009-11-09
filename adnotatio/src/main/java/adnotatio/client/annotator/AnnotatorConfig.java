/**
 * 
 */
package adnotatio.client.annotator;

import adnotatio.renderer.templates.FieldInfoFactoryRegistry;
import adnotatio.renderer.templates.TemplateBuilder;

import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Window;

/**
 * This class provides various configurations for the main {@link Annotator}
 * panel. It provides references to the following remote services:
 * <ul>
 * <li>{@link #getAnnotationView()} - the address of the service used to load
 * the annotation visualization template </li>
 * <li>{@link #getAnnotationForm()} - the address of the remove service used to
 * load the annotation editing form</li>
 * <li>{@link #getContentService()} the address of the service used to load the
 * content to annotate</li>
 * <li>{@link #getReadService()} the address of the service returning all
 * existing annotations for the content</li>
 * <li>{@link #getWriteService()} the address of the service used to store new
 * annotations</li>
 * </li>
 * This class contains the additional service methods as well:
 * <ul>
 * <li>{@link #getTemplateBuilder()} this method returns the template builder
 * used as a registry for loaded editing and view templates</li>
 * <li>{@link #handleError(Throwable)} this method is called to report about
 * errors</li>
 * </ul>
 * 
 * @author kotelnikov
 */
public class AnnotatorConfig {

    /**
     * The address of the service providing the annotation form
     */
    protected String fAnnotationForm;

    /**
     * The address of the service used to load the annotation visualization
     * template
     */
    protected String fAnnotationView;

    /**
     * The address of the service used to load the content to annotate
     */
    protected String fContentService;

    /**
     * The address of the service returning all existing annotations for the
     * content
     */
    protected String fReadService;

    /**
     * The template builder used as a registry for loaded editing and view
     * templates
     */
    protected TemplateBuilder fTemplateBuilder = new TemplateBuilder(
        new FieldInfoFactoryRegistry());

    /**
     * The address of the service used to store new annotations
     */
    protected String fWriteService;

    /**
     * Loads the configuration defined in the main HTML page and initializes all
     * internal fields
     * 
     * @param configName the name of the configuration javascript map defined in
     *        the main HTML page; this map is used to load all configuration
     *        parameters
     */
    public AnnotatorConfig(String configName) {
        loadFromDictionary(configName);
    }

    /**
     * Returns address of the service providing the annotation form
     * 
     * @return address of the service providing the annotation form
     */
    public String getAnnotationForm() {
        return fAnnotationForm;
    }

    /**
     * Returns the address of the service providing a template for annotation
     * views
     * 
     * @return the address of the service providing a template for annotation
     *         views
     */
    public String getAnnotationView() {
        return fAnnotationView;
    }

    /**
     * Returns the address of the service used to load the content to annotate
     * 
     * @return the address of the service used to load the content to annotate
     */
    public String getContentService() {
        return fContentService;
    }

    /**
     * Returns the address of the service used to load a list of existing
     * annotations
     * 
     * @return the address of the service used to load a list of existing
     *         annotations
     */
    public String getReadService() {
        return fReadService;
    }

    /**
     * Returns the template builder used as a registry for loaded editing and
     * view templates.
     * 
     * @return the template builder used as a registry for loaded editing and
     *         view templates.
     */
    public TemplateBuilder getTemplateBuilder() {
        return fTemplateBuilder;
    }

    /**
     * Returns the address of the service used to store new annotations
     * 
     * @return the address of the service used to store new annotations
     */
    public String getWriteService() {
        return fWriteService;
    }

    /**
     * This method is called to report about errors. This method can be
     * overloaded in subclasses.
     * 
     * @param t an exception or an error to handle
     */
    public void handleError(Throwable t) {
        Window.alert("Error! " + t.getMessage());
    }

    /**
     * Loads all configuration parameters from the global javascript map with
     * the specified name. This javascript map should be defined in the main
     * HTML page.
     * 
     * @param configName the name of the javascript map containing configuration
     *        parameters (names of services)
     */
    public void loadFromDictionary(String configName) {
        Dictionary resources = Dictionary.getDictionary(configName);
        fReadService = resources.get("readService");
        fWriteService = resources.get("writeService");
        fContentService = resources.get("contentService");
        fAnnotationForm = resources.get("annotationForm");
        fAnnotationView = resources.get("annotationView");
    }

}

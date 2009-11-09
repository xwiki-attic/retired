package adnotatio.client.app;

import adnotatio.client.annotator.Annotator;
import adnotatio.client.annotator.AnnotatorConfig;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * References:
 * 
 * <pre>
 * http://www.quirksmode.org/dom/range_intro.html
 * http://www.quirksmode.org/dom/w3c_range.html
 * http://groups.google.com/group/comp.lang.javascript/msg/db76484c36bde1be
 * http://jsfromhell.com/forms/selection
 * </pre>
 */
public class HtmlAnnotatorClient implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        AnnotatorConfig config = new AnnotatorConfig("resources");
        Annotator annotator = new Annotator(config);
        RootPanel.get().add(annotator);
    }
}

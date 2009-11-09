/**
 * 
 */
package adnotatio.server.impl;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.ServletConfig;

import org.w3c.dom.Document;

import adnotatio.server.ITemplateProvider;

/**
 * @author kotelnikov
 */
public class TemplateProvider extends Provider implements ITemplateProvider {

    public TemplateProvider(ServletConfig config) {
        super(config);
    }

    /**
     * @see adnotatio.server.ITemplateProvider#getEditTemplate(java.lang.String)
     */
    public Document getEditTemplate(String docId) throws IOException {
        return readDocument("HtmlAnnotator-form.xml");
    }

    /**
     * @see adnotatio.server.ITemplateProvider#getMainPage(java.lang.String)
     */
    public Document getMainPage(String docId) throws IOException {
        LinkedHashMap<String, String> replacements = new LinkedHashMap<String, String>();
        replacements.put("${scriptBase}", "..");
        replacements.put("${base}", "service");
        replacements.put("${docId}", docId);
        return readDocument("HtmlAnnotator-app.xml", replacements);
    }

    /**
     * @see adnotatio.server.ITemplateProvider#getViewTemplate(java.lang.String)
     */
    public Document getViewTemplate(String docId) throws IOException {
        return readDocument("HtmlAnnotator-view.xml");
    }

}

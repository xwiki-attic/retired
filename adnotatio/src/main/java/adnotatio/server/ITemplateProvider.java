/**
 * 
 */
package adnotatio.server;

import java.io.IOException;

import org.w3c.dom.Document;

/**
 * Instances of this type are used to load the content of templates used to
 * visualize annotations in edit and view modes.
 * 
 * @author kotelnikov
 */
public interface ITemplateProvider {

    /**
     * Returns an XML template for annotation editing forms
     * 
     * @param docId the identifier of the document
     * @return an editing template
     * @throws IOException
     */
    Document getEditTemplate(String docId) throws IOException;

    /**
     * Returns an XML template used to visualize annotations
     * 
     * @param docId the identifier of the document
     * @return an annotation view template
     * @throws IOException
     */
    Document getViewTemplate(String docId) throws IOException;

    /**
     * Returns the main page containg the reference to the GWT application
     * 
     * @param docId the identifier of the document
     * @return the main page containing the reference to the GWT application
     * @throws IOException
     */
    Document getMainPage(String docId) throws IOException;

}

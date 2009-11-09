/**
 * 
 */
package adnotatio.server;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.*;

import adnotatio.common.data.IPropertiesContainer;

/**
 * This interface gives access to annotations corresponding to a specific
 * document.
 * 
 * @author kotelnikov
 */
public interface IAnnotationProvider {

    /**
     * Returns the HTML content of the document
     * 
     * @param docId the document identifier
     * @return the HTML content of the document
     * @throws IOException
     */
    String getContent(String docId, HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Returns a list of formatted annotations corresponding to the document
     * with the specified identifier
     * 
     * @param docId the identifier of the document for which a list of
     *        annotations should be returned
     * @return a list of annotations corresponding to the document with the
     *         specified identifier
     * @throws IOException
     */
    List<IPropertiesContainer> loadAnnotations(String docId) throws IOException;

    /**
     * This method gets a list of non-formatted annotations, stores them and
     * returns the corresponding list of formatted annotations;
     * 
     * @param docId the identifier of the document for which a list of
     *        annotations should be stored
     * @param nonFormattedAnnotations a list of non-formatted annotations
     * @return a list of reformatted annotations
     * @throws IOException
     */
    List<IPropertiesContainer> storeAnnotations(
        String docId,
        List<IPropertiesContainer> nonFormattedAnnotations) throws IOException;
}

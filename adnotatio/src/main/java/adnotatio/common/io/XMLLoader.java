/**
 * 
 */
package adnotatio.common.io;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

/**
 * @author kotelnikov
 */
public class XMLLoader extends TextLoader {

    /**
     * @param path
     */
    public XMLLoader(String path) {
        super(path);
    }

    /**
     * @see adnotatio.common.io.TextLoader#onSuccess(com.google.gwt.user.client.rpc.AsyncCallback,
     *      java.lang.String)
     */
    protected void onSuccess(AsyncCallback callback, String text)
        throws Exception {
        Document doc = XMLParser.parse(text);
        com.google.gwt.xml.client.Element e = doc.getDocumentElement();
        callback.onSuccess(e);
    }

}
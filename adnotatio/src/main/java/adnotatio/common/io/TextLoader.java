/**
 * 
 */
package adnotatio.common.io;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Loads a block of text and returns it in the specified callback object.
 * 
 * @author kotelnikov
 */
public class TextLoader extends ResourceLoader {

    public TextLoader(String path) {
        super(path);
    }

    /**
     * @param callback
     * @param text
     * @throws Exception
     */
    protected void onSuccess(AsyncCallback callback, String text)
        throws Exception {
        callback.onSuccess(text);
    }

    /**
     * @see adnotatio.common.io.ResourceLoader#onSuccess(com.google.gwt.http.client.Request,
     *      com.google.gwt.http.client.Response,
     *      com.google.gwt.user.client.rpc.AsyncCallback)
     */
    protected void onSuccess(
        Request request,
        Response response,
        AsyncCallback callback) throws Exception {
        if (response.getStatusCode() != 200)
            throw new RuntimeException("Bad response status");
        String text = response.getText();
        onSuccess(callback, text);
    }
}

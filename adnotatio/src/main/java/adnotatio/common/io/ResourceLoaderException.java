/**
 * 
 */
package adnotatio.common.io;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

/**
 * @author kotelnikov
 */
public class ResourceLoaderException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3813361916158553078L;

    private Exception fException;

    private Request fRequest;

    private Response fResponse;

    public ResourceLoaderException(
        Request request,
        Response response,
        Exception e) {
        fRequest = request;
        fResponse = response;
        fException = e;
    }

    public Exception getException() {
        return fException;
    }

    public Request getRequest() {
        return fRequest;
    }

    /**
     * @return the response
     */
    public Response getResponse() {
        return fResponse;
    }

}

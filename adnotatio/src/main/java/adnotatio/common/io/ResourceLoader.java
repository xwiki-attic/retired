/**
 * 
 */
package adnotatio.common.io;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;

public class ResourceLoader {

    private String fUrl;

    public ResourceLoader(String path) {
        fUrl = GWT.getModuleBaseURL() + path;
    }

    public final void load(AsyncCallback callback) {
        load("", callback);
    }

    public final void load(String content, final AsyncCallback callback) {
        RequestBuilder request = new RequestBuilder(RequestBuilder.POST, fUrl);
        try {
            request.sendRequest(content, new RequestCallback() {

                public void onError(Request request, Throwable exception) {
                    callback.onFailure(exception);
                }

                public void onResponseReceived(
                    Request request,
                    Response response) {
                    try {
                        ResourceLoader.this.onSuccess(
                            request,
                            response,
                            callback);
                    } catch (ResourceLoaderException e) {
                        callback.onFailure(e);
                    } catch (Exception e) {
                        callback.onFailure(new ResourceLoaderException(
                            request,
                            response,
                            e));
                    }
                }

            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }

    }

    protected void onSuccess(
        Request request,
        Response response,
        AsyncCallback callback) throws Exception {
        callback.onSuccess(response);
    }

}

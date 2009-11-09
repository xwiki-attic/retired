/**
 * 
 */
package adnotatio.common.io;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class ResourceLoaderBarrier {

    private List fCallbacks = new ArrayList();

    private List fFailureList;

    private List fLoaders = new ArrayList();

    private List fSuccessList;

    public void add(ResourceLoader loader) {
        add(loader, null);
    }

    public void add(ResourceLoader loader, AsyncCallback callback) {
        fLoaders.add(loader);
        fCallbacks.add(callback);
    }

    public void load() {
        if (fSuccessList != null)
            throw new RuntimeException("The barrieer is  already activated");
        fSuccessList = new ArrayList();
        fFailureList = new ArrayList();
        final int len = fLoaders.size();
        for (int i = 0; i < len; i++) {
            ResourceLoader loader = (ResourceLoader) fLoaders.get(i);
            final AsyncCallback callback = (AsyncCallback) fCallbacks.get(i);
            loader.load(new AsyncCallback() {

                private void checkEnd() {
                    if (fSuccessList.size() + fFailureList.size() == len) {
                        onFinish(fSuccessList, fFailureList);
                    }
                }

                public void onFailure(Throwable caught) {
                    fFailureList.add(caught);
                    try {
                        if (callback != null) {
                            callback.onFailure(caught);
                        }
                    } finally {
                        checkEnd();
                    }
                }

                public void onSuccess(Object result) {
                    fSuccessList.add(result);
                    try {
                        if (callback != null) {
                            callback.onSuccess(result);
                        }
                    } finally {
                        checkEnd();
                    }
                }
            });
        }
    }

    protected abstract void onFinish(List successList, List failureList);
}
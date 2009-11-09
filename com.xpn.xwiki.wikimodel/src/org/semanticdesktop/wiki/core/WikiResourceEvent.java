package org.semanticdesktop.wiki.core;
import java.util.Map;

/**
 * @author kotelnikov
 */
public class WikiResourceEvent {

    protected Map<String, Object> fParams;

    protected WikiResource fResource;

    protected int fType;

    /**
     * @param resource
     * @param type
     * @param params
     */
    public WikiResourceEvent(
        WikiResource resource,
        int type,
        Map<String, Object> params) {
        fType = type;
        fParams = params;
        fResource = resource;
    }

    /**
     * Returns the params.
     * 
     * @return the params.
     */
    public Map<String, Object> getParams() {
        return fParams;
    }

    /**
     * Returns the resource.
     * 
     * @return the resource.
     */
    protected WikiResource getResource() {
        return fResource;
    }

    /**
     * Returns the type.
     * 
     * @return the type.
     */
    public int getType() {
        return fType;
    }

}

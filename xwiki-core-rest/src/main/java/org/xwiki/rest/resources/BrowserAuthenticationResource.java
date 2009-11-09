package org.xwiki.rest.resources;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Resource;
import org.restlet.resource.Variant;

/**
 * @version $Id$
 */
public class BrowserAuthenticationResource extends Resource
{
    public static final String URI_PATTERN = "/browser_authentication";

    public BrowserAuthenticationResource(Context context, Request request, Response response)
    {
        super(context, request, response);
        getVariants().clear();
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }

    @Override
    public void handleGet()
    {
        getResponse().redirectSeeOther(String.format("%s/", getRequest().getRootRef()));
    }

}

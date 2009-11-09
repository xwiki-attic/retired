package org.xwiki.rest;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;

/**
 * @version $Id$
 */
public interface XWikiResourceRepresenter
{
    /**
     * @return The media type for this representer.
     */
    MediaType getMediaType();

    /*
     * TODO: Think about the signature because xwikiApi and xwikiContext objects are also needed for certain
     * representations
     */
    Representation represent(Context context, Request request, Response response, Object... objects);
}

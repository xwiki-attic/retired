package org.xwiki.rest.representers;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.xwiki.rest.XWikiResourceRepresenter;

/**
 * A representer that always returns null as its representation.
 * 
 * @version $Id$
 */
public class NullRepresenter implements XWikiResourceRepresenter
{
    public MediaType getMediaType()
    {
        return MediaType.ALL;
    }

    public Representation represent(Context context, Request request, Response response, Object... object)
    {
        return null;
    }

}

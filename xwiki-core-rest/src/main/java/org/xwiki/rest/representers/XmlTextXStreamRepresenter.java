package org.xwiki.rest.representers;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.xwiki.rest.Utils;
import org.xwiki.rest.XWikiResourceRepresenter;

/**
 * @version $Id$
 */
public class XmlTextXStreamRepresenter implements XWikiResourceRepresenter
{

    public MediaType getMediaType()
    {
        return MediaType.TEXT_XML;
    }

    public Representation represent(Context context, Request request, Response response, Object... objects)
    {
        return new StringRepresentation(Utils.toXml(objects[0]), MediaType.APPLICATION_XML);
    }

}

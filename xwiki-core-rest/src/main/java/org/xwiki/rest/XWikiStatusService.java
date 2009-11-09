package org.xwiki.rest;

import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.service.StatusService;

/**
 * A status service that is able to catch unhandled exceptions and correctly release the resource that was serving the
 * request.
 * 
 * @version $Id$
 */
public class XWikiStatusService extends StatusService
{
    private XWikiRestApplication application;

    public XWikiStatusService(XWikiRestApplication application)
    {
        super();
        this.application = application;
    }

    @Override
    public Status getStatus(Throwable throwable, Request request, Response response)
    {
        Utils.cleanupResource(request, application.getComponentManager(), application.getLogger());

        return super.getStatus(throwable, request, response);
    }

}

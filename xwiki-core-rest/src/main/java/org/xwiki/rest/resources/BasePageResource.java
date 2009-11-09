package org.xwiki.rest.resources;

import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.xwiki.rest.DomainObjectFactory;
import org.xwiki.rest.XWikiResource;
import org.xwiki.rest.model.Page;

import com.xpn.xwiki.api.Document;

/**
 * @version $Id$
 */
public abstract class BasePageResource extends XWikiResource
{
    @Override
    public Representation represent(Variant variant)
    {
        DocumentInfo documentInfo = getDocumentFromRequest(getRequest(), true);
        if (documentInfo == null) {
            /* If the document doesn't exist send a not found header */
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return null;
        }

        Document doc = documentInfo.getDocument();

        /* Check if we have access to it */
        if (doc == null) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
            return null;
        }

        Page page = DomainObjectFactory.createPage(getRequest(), resourceClassRegistry, doc);
        if (page == null) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            return null;
        }

        return getRepresenterFor(variant).represent(getContext(), getRequest(), getResponse(), page);
    }
}

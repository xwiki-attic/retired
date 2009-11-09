package org.xwiki.rest.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.xwiki.rest.DomainObjectFactory;
import org.xwiki.rest.XWikiResource;
import org.xwiki.rest.model.XWikiRoot;

/**
 * @version $Id$
 */
public class RootResource extends XWikiResource
{
    @Override
    public Representation represent(Variant variant)
    {
        XWikiRoot xwikiRoot = DomainObjectFactory.createXWikiRoot(getRequest(), xwikiApi, resourceClassRegistry);

        return getRepresenterFor(variant).represent(getContext(), getRequest(), getResponse(), xwikiRoot);
    }
}

package org.xwiki.rest.resources;

import java.util.List;

import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.xwiki.rest.Constants;
import org.xwiki.rest.DomainObjectFactory;
import org.xwiki.rest.XWikiResource;
import org.xwiki.rest.model.Space;

import com.xpn.xwiki.XWikiException;

/**
 * @version $Id$
 */
public class SpaceResource extends XWikiResource
{
    @Override
    public Representation represent(Variant variant)
    {
        try {
            String spaceName = (String) getRequest().getAttributes().get(Constants.SPACE_NAME_PARAMETER);

            xwiki.setDatabase("xwiki");

            List<String> docNames = xwikiApi.getSpaceDocsName(spaceName);
            String home = String.format("%s.WebHome", spaceName);

            if (!xwikiApi.exists(home)) {
                home = null;
            }

            String wiki = "xwiki";

            Space space =
                DomainObjectFactory.createSpace(getRequest(), resourceClassRegistry, wiki, spaceName, home, docNames
                    .size());

            return getRepresenterFor(variant).represent(getContext(), getRequest(), getResponse(), space);
        } catch (XWikiException e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        } finally {
            xwiki.setDatabase("xwiki");
        }

        return null;
    }

}

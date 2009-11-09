package org.xwiki.rest.resources;

import java.util.Collections;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.xwiki.rest.Constants;
import org.xwiki.rest.DomainObjectFactory;
import org.xwiki.rest.RangeIterable;
import org.xwiki.rest.Utils;
import org.xwiki.rest.XWikiResource;
import org.xwiki.rest.model.Spaces;

import com.xpn.xwiki.XWikiException;

/**
 * @version $Id$
 */
public class SpacesResource extends XWikiResource
{
    @Override
    public Representation represent(Variant variant)
    {
        String database = xwikiContext.getDatabase();

        try {
            String wiki = (String) getRequest().getAttributes().get(Constants.WIKI_NAME_PARAMETER);
            xwikiContext.setDatabase(wiki);

            Spaces spaces = new Spaces();

            List<String> spaceNames = xwikiApi.getSpaces();
            Collections.sort(spaceNames);

            Form queryForm = getRequest().getResourceRef().getQueryAsForm();
            RangeIterable<String> ri =
                new RangeIterable<String>(spaceNames, Utils.parseInt(queryForm
                    .getFirstValue(Constants.START_PARAMETER), 0), Utils.parseInt(queryForm
                    .getFirstValue(Constants.NUMBER_PARAMETER), -1));

            for (String spaceName : ri) {
                List<String> docNames = xwikiApi.getSpaceDocsName(spaceName);
                String home = String.format("%s.WebHome", spaceName);
                if (!xwikiApi.exists(home)) {
                    home = null;
                }

                spaces.addSpace(DomainObjectFactory.createSpace(getRequest(), resourceClassRegistry, wiki, spaceName,
                    home, docNames.size()));
            }

            return getRepresenterFor(variant).represent(getContext(), getRequest(), getResponse(), spaces);
        } catch (XWikiException e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        } finally {
            xwikiContext.setDatabase(database);
        }

        return null;
    }
}

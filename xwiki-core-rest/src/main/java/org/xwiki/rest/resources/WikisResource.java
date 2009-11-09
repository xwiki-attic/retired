package org.xwiki.rest.resources;

import java.util.List;

import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.xwiki.rest.Constants;
import org.xwiki.rest.Utils;
import org.xwiki.rest.XWikiResource;
import org.xwiki.rest.model.Link;
import org.xwiki.rest.model.Relations;
import org.xwiki.rest.model.Wiki;
import org.xwiki.rest.model.Wikis;

import com.xpn.xwiki.XWikiException;

/**
 * @version $Id$
 */
public class WikisResource extends XWikiResource
{
    @Override
    public Representation represent(Variant variant)
    {
        try {
            Wikis wikis = new Wikis();

            List<String> databaseNames = xwiki.getVirtualWikisDatabaseNames(xwikiContext);

            if (databaseNames.isEmpty()) {
                databaseNames.add("xwiki");
            }

            for (String databaseName : databaseNames) {
                Wiki wiki = new Wiki(databaseName);

                String fullUri =
                    String.format("%s%s", getRequest().getRootRef(), resourceClassRegistry
                        .getUriPatternForResourceClass(SpacesResource.class));
                Link link = new Link(Utils.formatUriTemplate(fullUri, Constants.WIKI_NAME_PARAMETER, databaseName));
                link.setRel(Relations.SPACES);
                wiki.addLink(link);

                wikis.addWiki(wiki);
            }

            return getRepresenterFor(variant).represent(getContext(), getRequest(), getResponse(), wikis);
        } catch (XWikiException e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }

        return null;

    }
}

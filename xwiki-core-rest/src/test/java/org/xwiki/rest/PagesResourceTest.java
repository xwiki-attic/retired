package org.xwiki.rest;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xwiki.rest.model.Link;
import org.xwiki.rest.model.Pages;
import org.xwiki.rest.model.Relations;
import org.xwiki.rest.model.Space;
import org.xwiki.rest.model.Spaces;
import org.xwiki.rest.model.Wiki;
import org.xwiki.rest.model.Wikis;
import org.xwiki.rest.resources.WikisResource;

public class PagesResourceTest extends AbstractHttpTest
{
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        GetMethod getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(WikisResource.class))));
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Wikis wikis = (Wikis) xstream.fromXML(getMethod.getResponseBodyAsString());
        assertTrue(wikis.getWikiList().size() > 0);

        Wiki wiki = wikis.getWikiList().get(0);
        Link link = wiki.getFirstLinkByRelation(Relations.SPACES);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Spaces spaces = (Spaces) xstream.fromXML(getMethod.getResponseBodyAsString());
        assertTrue(spaces.getSpaceList().size() > 0);

        Space space = spaces.getSpaceList().get(0);
        link = space.getFirstLinkByRelation(Relations.PAGES);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Pages pages = (Pages) xstream.fromXML(getMethod.getResponseBodyAsString());
        assertTrue(pages.getPageSummaryList().size() > 0);
    }

}

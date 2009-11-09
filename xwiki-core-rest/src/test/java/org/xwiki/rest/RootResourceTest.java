package org.xwiki.rest;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xwiki.rest.model.Link;
import org.xwiki.rest.model.Relations;
import org.xwiki.rest.model.XWikiRoot;
import org.xwiki.rest.resources.RootResource;

public class RootResourceTest extends AbstractHttpTest
{
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        GetMethod getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(RootResource.class))));
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        XWikiRoot xwikiRoot = (XWikiRoot) xstream.fromXML(getMethod.getResponseBodyAsString());

        Link link = xwikiRoot.getFirstLinkByRelation(Relations.WIKIS);
        assertNotNull(link);

        link = xwikiRoot.getFirstLinkByRelation(Relations.WADL);
        assertNotNull(link);
    }
}

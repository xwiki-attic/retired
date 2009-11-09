package org.xwiki.rest.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @version $Id$
 */
@XStreamAlias("xwiki")
public class XWikiRoot extends LinkCollection
{
    private String version;

    public XWikiRoot(String version)
    {
        this.version = version;
    }

    public String getVersion()
    {
        return version;
    }

}

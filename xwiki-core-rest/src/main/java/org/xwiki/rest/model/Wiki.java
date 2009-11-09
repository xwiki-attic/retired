package org.xwiki.rest.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @version $Id$
 */
@XStreamAlias("wiki")
public class Wiki extends LinkCollection
{
    private String name;

    public Wiki(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

}

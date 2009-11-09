package org.xwiki.rest.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @version $Id$
 */
@XStreamAlias("space")
public class Space extends LinkCollection
{
    private String wiki;

    private String name;

    private String home;

    private Integer numberOfPages;

    public Space(String wiki, String name, String home, Integer numberOfPages)
    {
        this.wiki = wiki;
        this.name = name;
        this.home = home;
        this.numberOfPages = numberOfPages;
    }

    public String getName()
    {
        return name;
    }

    public String getHome()
    {
        return home;
    }

    public Integer getNumberOfPages()
    {
        return numberOfPages;
    }

    public String getWiki()
    {
        return wiki;
    }

}

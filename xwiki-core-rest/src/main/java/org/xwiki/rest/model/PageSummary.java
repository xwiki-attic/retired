package org.xwiki.rest.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @version $Id$
 */
@XStreamAlias("pageSummary")
public class PageSummary extends LinkCollection
{
    private String wiki;

    private String id;

    private String fullId;

    private String space;

    private String name;

    private String title;

    private String parent;

    private Translations translations;

    public PageSummary()
    {
        translations = new Translations();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getSpace()
    {
        return space;
    }

    public void setSpace(String space)
    {
        this.space = space;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFullName()
    {
        return String.format("%s.%s", space, name);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Translations getTranslations()
    {
        return translations;
    }

    public void setTranslations(Translations translations)
    {
        this.translations = translations;
    }

    public void setParent(String parent)
    {
        this.parent = parent;
    }

    public String getParent()
    {
        return parent;
    }

    public String getWiki()
    {
        return wiki;
    }

    public void setWiki(String wiki)
    {
        this.wiki = wiki;
    }

    public String getFullId()
    {
        return fullId;
    }

    public void setFullId(String fullId)
    {
        this.fullId = fullId;
    }
}

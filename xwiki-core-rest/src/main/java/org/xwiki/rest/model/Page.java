package org.xwiki.rest.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @version $Id$
 */
@XStreamAlias("page")
public class Page extends PageSummary
{
    private String language;

    private Integer version;

    private Integer minorVersion;

    private String xwikiUrl;

    private Long created;

    private String creator;

    private Long modified;

    private String modifier;

    private String content;

    public Page()
    {
        super();
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public Integer getMinorVersion()
    {
        return minorVersion;
    }

    public void setMinorVersion(Integer minorVersion)
    {
        this.minorVersion = minorVersion;
    }

    public String getXwikiUrl()
    {
        return xwikiUrl;
    }

    public void setXWikiUrl(String xwikiUrl)
    {
        this.xwikiUrl = xwikiUrl;
    }

    public Long getCreated()
    {
        return created;
    }

    public void setCreated(Long created)
    {
        this.created = created;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public Long getModified()
    {
        return modified;
    }

    public void setModified(Long modified)
    {
        this.modified = modified;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}

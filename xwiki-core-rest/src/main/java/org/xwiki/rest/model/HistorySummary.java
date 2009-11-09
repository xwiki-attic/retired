package org.xwiki.rest.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("historySummary")
public class HistorySummary extends LinkCollection
{
    private String pageId;

    private Integer version;

    private Integer minorVersion;

    private Long modified;

    private String modifier;

    public String getPageId()
    {
        return pageId;
    }

    public void setPageId(String pageId)
    {
        this.pageId = pageId;
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

}

package org.xwiki.rest.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @version $Id$
 */
@XStreamAlias("link")
public class Link
{
    @XStreamAsAttribute
    private String rel;

    @XStreamAsAttribute
    private String href;

    @XStreamAsAttribute
    private String type;

    @XStreamAsAttribute
    private String hrefLang;

    public Link(String href)
    {
        this.href = href;
    }

    public String getRel()
    {
        return rel;
    }

    public void setRel(String rel)
    {
        this.rel = rel;
    }

    public String getHref()
    {
        return href;
    }

    public void setHref(String href)
    {
        this.href = href;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getHrefLang()
    {
        return hrefLang;
    }

    public void setHrefLang(String hrefLang)
    {
        this.hrefLang = hrefLang;
    }

}

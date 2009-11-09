package org.xwiki.rest.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @version $Id$
 */
@XStreamAlias("translations")
public class Translations extends LinkCollection
{
    @XStreamAsAttribute
    @XStreamAlias("default")
    private String defaultTranslation;

    @XStreamAsAttribute
    private String page;

    public String getDefaultTranslation()
    {
        return defaultTranslation;
    }

    public void setDefaultTranslation(String defaultTranslation)
    {
        this.defaultTranslation = defaultTranslation;
    }

    public String getPage()
    {
        return page;
    }

    public void setPageFullName(String page)
    {
        this.page = page;
    }

}

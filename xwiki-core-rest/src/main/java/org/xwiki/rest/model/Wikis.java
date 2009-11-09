package org.xwiki.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @version $Id$
 */
@XStreamAlias("wikis")
public class Wikis extends LinkCollection
{
    @XStreamImplicit
    private List<Wiki> wikiList;

    public Wikis()
    {
        wikiList = new ArrayList<Wiki>();
    }

    public void addWiki(Wiki space)
    {
        wikiList.add(space);
    }

    public List<Wiki> getWikiList()
    {
        return wikiList;
    }
}

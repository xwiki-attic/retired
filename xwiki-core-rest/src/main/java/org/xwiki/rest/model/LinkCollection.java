package org.xwiki.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @version $Id$
 */
public class LinkCollection
{
    @XStreamImplicit
    private List<Link> links;

    public void addLink(Link link)
    {
        if (links == null) {
            links = new ArrayList<Link>();
        }

        links.add(link);
    }

    public List<Link> getLinks()
    {
        return links;
    }

    public List<Link> getLinksByRelation(String rel)
    {
        List<Link> result = new ArrayList<Link>();

        for (Link link : links) {
            if (rel.equals(link.getRel())) {
                result.add(link);
            }
        }

        return result;
    }

    public Link getFirstLinkByRelation(String rel)
    {
        for (Link link : links) {
            if (rel.equals(link.getRel())) {
                return link;
            }
        }

        return null;
    }

}

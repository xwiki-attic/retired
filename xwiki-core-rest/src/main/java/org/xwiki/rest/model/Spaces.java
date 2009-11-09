package org.xwiki.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @version $Id$
 */
@XStreamAlias("spaces")
public class Spaces extends LinkCollection
{
    @XStreamImplicit
    private List<Space> spaceList;

    public Spaces()
    {
        spaceList = new ArrayList<Space>();
    }

    public void addSpace(Space space)
    {
        spaceList.add(space);
    }

    public List<Space> getSpaceList()
    {
        return spaceList;
    }
}

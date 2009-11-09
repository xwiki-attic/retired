package org.xwiki.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @version $Id$
 */
@XStreamAlias("pages")
public class Pages extends LinkCollection
{
    @XStreamImplicit
    private List<PageSummary> pageSummaries;

    public Pages()
    {
        pageSummaries = new ArrayList<PageSummary>();
    }

    public List<PageSummary> getPageSummaryList()
    {
        return pageSummaries;
    }

    public void addPageSummary(PageSummary pageSummary)
    {
        pageSummaries.add(pageSummary);
    }

}

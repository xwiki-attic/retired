package org.xwiki.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("history")
public class History extends LinkCollection
{
    @XStreamImplicit
    List<HistorySummary> historySummaries;

    public History()
    {
        historySummaries = new ArrayList<HistorySummary>();
    }

    public void addHistorySummary(HistorySummary historySummary)
    {
        historySummaries.add(historySummary);
    }

    public List<HistorySummary> getHistorySummaryList()
    {
        return historySummaries;
    }
}

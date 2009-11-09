package org.xwiki.rest.model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.SortableFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @version $Id$
 */
public class XStreamFactory
{
    private static XStream xstream;

    public static XStream getXStream()
    {
        if (xstream == null) {
            xstream = createAndConfigureXStream();
        }

        return xstream;
    }

    private static XStream createAndConfigureXStream()
    {
        SortableFieldKeySorter sorter = new SortableFieldKeySorter();

        sorter.registerFieldOrder(XWikiRoot.class, new String[] {"version", "links"});

        sorter.registerFieldOrder(Wiki.class, new String[] {"name", "links"});

        sorter.registerFieldOrder(Space.class, new String[] {"wiki", "name", "home", "numberOfPages", "links"});

        sorter.registerFieldOrder(PageSummary.class, new String[] {"fullId", "id", "wiki", "space", "name", "title",
        "parent", "translations", "links"});

        sorter.registerFieldOrder(Page.class, new String[] {"fullId", "id", "wiki", "space", "name", "title", "parent",
        "version", "minorVersion", "language", "xwikiUrl", "created", "creator", "modified", "modifier",
        "translations", "content", "links"});

        XStream xstream = new XStream(new Sun14ReflectionProvider(new FieldDictionary(sorter)), new DomDriver());

        xstream.processAnnotations(Link.class);
        xstream.processAnnotations(XWikiRoot.class);
        xstream.processAnnotations(Wikis.class);
        xstream.processAnnotations(Wiki.class);
        xstream.processAnnotations(Spaces.class);
        xstream.processAnnotations(Space.class);
        xstream.processAnnotations(Pages.class);
        xstream.processAnnotations(PageSummary.class);
        xstream.processAnnotations(Page.class);
        xstream.processAnnotations(History.class);
        xstream.processAnnotations(HistorySummary.class);

        return xstream;

    }
}

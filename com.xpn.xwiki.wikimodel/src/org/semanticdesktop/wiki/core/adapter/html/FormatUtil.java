package org.semanticdesktop.wiki.core.adapter.html;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticdesktop.wikimodel.IWikiParams;
import org.semanticdesktop.wikimodel.impl.WikiPageUtil;
import org.semanticdesktop.wikimodel.wem.IWikiDocumentListener;
import org.semanticdesktop.wikimodel.wem.impl.WikiDocumentListener;
import org.semanticdesktop.wikimodel.wem.impl.WikiSourceFormatter;
import org.semanticdesktop.wikimodel.wom.IWikiBlockPlugin;
import org.semanticdesktop.wikimodel.wom.IWikiDocumentInclusion;
import org.semanticdesktop.wikimodel.wom.IWikiHeader;
import org.semanticdesktop.wikimodel.wom.IWikiInlinePlugin;
import org.semanticdesktop.wikimodel.wom.IWikiNode;
import org.semanticdesktop.wikimodel.wom.IWikiNodeContainer;
import org.semanticdesktop.wikimodel.wom.util.WikiEventGenerator;
import org.semanticdesktop.wikimodel.xhtml.WikiDocumentHtmlFormatter;
import org.semanticdesktop.wikimodel.xhtml.WikiInlineHtmlFormatter;

/**
 * @author kotelnikov
 */
public class FormatUtil {

    protected static class WikiDocFormatter extends WikiDocumentHtmlFormatter
        implements
        IDocFormatterConst {

        protected int fDivCounter;

        protected Set<String> fKeySet = new HashSet<String>();

        protected WikiEventGenerator fTransformer;

        /**
         * @param writer
         */
        public WikiDocFormatter(PrintWriter writer) {
            super(writer);
        }

        /**
         * @see org.semanticdesktop.wikimodel.xhtml.WikiDocumentHtmlFormatter#beginHeader(int,
         *      org.semanticdesktop.wikimodel.IWikiParams)
         */
        public void beginHeader(int level, IWikiParams params) {
            IWikiHeader node = (IWikiHeader) fTransformer.getCurrentNode();
            String id = getHeaderLabel(fKeySet, node);
            fWriter.print("<a name='" + id + "'></a>");
            super.beginHeader(level, params);
        }

        /**
         * @param transformer
         */
        public void setTransformer(WikiEventGenerator transformer) {
            fTransformer = transformer;
        }
    }

    /**
     * Transforms all link information into a simple string
     */
    public static IWikiLinkFormatter NULL_LINK_FORMATTER = new IWikiLinkFormatter() {

        public void formatLink(String label, String uri, PrintWriter writer) {
            if (label == null)
                label = uri;
            writer.print(label);
        }

    };

    /**
     * @param node
     * @return a header label corresponding to the given node
     */
    public static String getHeaderLabel(IWikiHeader node) {
        String str = toText(node, true);
        return str.trim().replace(' ', '_');
    }

    /**
     * @param keySet
     * @param node
     * @return a unique node key (unique in the given set)
     */
    public static String getHeaderLabel(Set<String> keySet, IWikiHeader node) {
        String key = getHeaderLabel(node);
        String k = key;
        int counter = 0;
        while (keySet.contains(key)) {
            key = k + "_" + (counter++);
        }
        keySet.add(key);
        return key;
    }

    /**
     * @param node
     * @param linkFormatter
     * @param includeNode
     * @return the html representation of the given node
     */
    public static String toHtml(
        IWikiNode node,
        final IWikiLinkFormatter linkFormatter,
        boolean includeNode) {
        if (node == null)
            return "";
        DocumentFormatter formatter = new DocumentFormatter() {

            @Override
            protected void executePlugin(
                IWikiBlockPlugin blockPlugin,
                PrintWriter writer) {
                //
            }

            @Override
            protected void executePlugin(
                IWikiInlinePlugin inlinePlugin,
                PrintWriter writer) {
                //
            }

            /**
             * @see org.semanticdesktop.wiki.core.adapter.html.IWikiLinkFormatter#formatLink(java.lang.String,
             *      java.lang.String, java.io.PrintWriter)
             */
            public void formatLink(String label, String uri, PrintWriter writer) {
                linkFormatter.formatLink(label, uri, writer);
            }

            @Override
            protected void includeEntity(
                IWikiDocumentInclusion inclusion,
                PrintWriter writer) {
                //
            }

        };
        return formatter.toHtml(node, includeNode);
    }

    /**
     * @param node
     * @param linkFormatter
     * @return the html representation of the given node
     */
    public static String toInlineHtml(
        IWikiNode node,
        final IWikiLinkFormatter linkFormatter) {
        if (node == null)
            return "";
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        WikiInlineHtmlFormatter formatter = new WikiInlineHtmlFormatter(writer) {
            protected void formatLink(String label, String uri) {
                linkFormatter.formatLink(label, uri, fWriter);
            }
        };
        WikiEventGenerator transformer = new WikiEventGenerator(formatter);
        transformer.execute(node);
        String str = stringWriter.toString();
        return str;
    }

    /**
     * @param node
     * @return the html representation of the given node
     */
    public static String toSource(IWikiNode node) {
        if (node == null)
            return "";
        Writer writer = new StringWriter();
        toSource(node, writer);
        String str = writer.toString();
        return str;
    }

    /**
     * Serializes the given wiki node into a wiki source and writes this content
     * into the given writer
     * 
     * @param node
     * @param writer
     */
    public static void toSource(IWikiNode node, Writer writer) {
        PrintWriter printer = new PrintWriter(writer);
        WikiSourceFormatter formatter = new WikiSourceFormatter(printer);
        WikiEventGenerator transformer = new WikiEventGenerator(formatter);
        transformer.execute(node);
    }

    /**
     * The text corresponding to the given node.
     * 
     * @param node for this node the corresponding text representation will be
     *        returned
     * @param includeNode
     * @return the text corresponding to the given node
     */
    public static String toText(IWikiNode node, boolean includeNode) {
        if (node == null)
            return "";
        StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);
        IWikiDocumentListener formatter = new WikiDocumentListener() {
            /**
             * @see org.wikimodel.core.IWikiTextListener#onEscapedSymbol(char)
             */
            public void onEscapedSymbol(char ch) {
                String text = WikiPageUtil.escapeXmlString("" + ch);
                writer.print(text);
            }

            /**
             * @see org.wikimodel.core.IWikiTextListener#onNewLine()
             */
            public void onNewLine() {
                writer.println();
            }

            /**
             * @see org.wikimodel.core.IWikiTextListener#onReference(java.lang.String,
             *      java.lang.String)
             */
            public void onReference(String label, String uri) {
                String link = WikiPageUtil.encodeHttpParams(uri);
                if (label == null || "".equals(label))
                    label = link;
                label = WikiPageUtil.escapeXmlString(label);
                if (label.equals(link))
                    writer.print(label);
                else
                    writer.print(label + "(" + link + ")");
            }

            /**
             * @see org.wikimodel.core.IWikiTextListener#onText(java.lang.String)
             */
            public void onText(String str) {
                str = WikiPageUtil.escapeXmlString(str);
                writer.print(str);
            }
        };
        WikiEventGenerator transformer = new WikiEventGenerator(formatter);
        if (includeNode) {
            transformer.execute(node);
        } else if (node instanceof IWikiNodeContainer) {
            IWikiNodeContainer container = (IWikiNodeContainer) node;
            for (Iterator iterator = container.iterator(); iterator.hasNext();) {
                IWikiNode n = (IWikiNode) iterator.next();
                transformer.execute(n);
            }
        }
        String str = stringWriter.toString();
        return str;
    }

}

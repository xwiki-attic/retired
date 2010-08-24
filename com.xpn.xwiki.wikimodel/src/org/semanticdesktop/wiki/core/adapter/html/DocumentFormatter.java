package org.semanticdesktop.wiki.core.adapter.html;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.wikimodel.IWikiParams;
import org.semanticdesktop.wikimodel.wom.IWikiBlockPlugin;
import org.semanticdesktop.wikimodel.wom.IWikiDocumentInclusion;
import org.semanticdesktop.wikimodel.wom.IWikiHeader;
import org.semanticdesktop.wikimodel.wom.IWikiInlinePlugin;
import org.semanticdesktop.wikimodel.wom.IWikiNode;
import org.semanticdesktop.wikimodel.wom.util.WikiEventGenerator;
import org.semanticdesktop.wikimodel.xhtml.WikiDocumentHtmlFormatter;

/**
 * @author kotelnikov
 */
public abstract class DocumentFormatter implements IWikiLinkFormatter {

    private class InternalFormatter extends WikiDocumentHtmlFormatter
        implements
        IDocFormatterConst {

        protected Set<String> fKeySet = new HashSet<String>();

        protected WikiEventGenerator fTransformer;

        /**
         * @param writer
         */
        public InternalFormatter(PrintWriter writer) {
            super(writer);
        }

        /**
         * @see org.semanticdesktop.wikimodel.xhtml.WikiDocumentHtmlFormatter#beginHeader(int,
         *      org.semanticdesktop.wikimodel.IWikiParams)
         */
        public void beginHeader(int level, IWikiParams params) {
            IWikiHeader node = (IWikiHeader) fTransformer.getCurrentNode();
            String id = FormatUtil.getHeaderLabel(fKeySet, node);
            fWriter.print("<A NAME='" + id + "'></A>");
            super.beginHeader(level, params);
        }

        /**
         * @see org.semanticdesktop.wikimodel.xhtml.WikiInlineHtmlFormatter#formatLink(java.lang.String,
         *      java.lang.String)
         */
        protected void formatLink(String label, String uri) {
            DocumentFormatter.this.formatLink(label, uri, fWriter);
        }

        /**
         * @see org.semanticdesktop.wikimodel.xhtml.WikiDocumentHtmlFormatter#onBlockPlugin(java.lang.String)
         */
        public void onBlockPlugin(String pluginContent) {
            IWikiBlockPlugin blockPlugin = (IWikiBlockPlugin) fTransformer
                .getCurrentNode();
            DocumentFormatter.this.executePlugin(blockPlugin, fWriter);
        }

        /**
         * @see org.semanticdesktop.wikimodel.xhtml.WikiDocumentHtmlFormatter#onDocumentInclusion(java.lang.String)
         */
        public void onDocumentInclusion(String uri) {
            IWikiDocumentInclusion inclusion = (IWikiDocumentInclusion) fTransformer
                .getCurrentNode();
            DocumentFormatter.this.includeEntity(inclusion, fWriter);
        }

        /**
         * @see org.semanticdesktop.wikimodel.xhtml.WikiInlineHtmlFormatter#onInlinePlugin(java.lang.String)
         */
        public void onInlinePlugin(String pluginContent) {
            IWikiInlinePlugin inlinePlugin = (IWikiInlinePlugin) fTransformer
                .getCurrentNode();
            DocumentFormatter.this.executePlugin(inlinePlugin, fWriter);
        }

    }

    /**
     * 
     *
     */
    public DocumentFormatter() {
        super();
    }

    /**
     * Executes the given block plugin and writes up its output to the given
     * stream.
     * 
     * @param blockPlugin the block plugin to execute
     * @param writer the output stream used to write the execution results
     */
    protected abstract void executePlugin(
        IWikiBlockPlugin blockPlugin,
        PrintWriter writer);

    /**
     * Executes the given inline plugin and writes up its output to the given
     * stream. The written values should be inline HTML elements.
     * 
     * @param inlinePlugin the inline plugin to execute
     * @param writer the output stream used to write the execution results
     */
    protected abstract void executePlugin(
        IWikiInlinePlugin inlinePlugin,
        PrintWriter writer);

    /**
     * @param inclusion a wiki node providing access to the URI of the included
     *        object
     * @param writer this writer has to be used to print out the content of the
     *        included object
     */
    protected abstract void includeEntity(
        IWikiDocumentInclusion inclusion,
        PrintWriter writer);

    /**
     * @param node
     * @param includeNode
     * @return the html representation of the
     */
    public String toHtml(IWikiNode node, boolean includeNode) {
        StringWriter writer = new StringWriter();
        toHtml(new PrintWriter(writer), node, includeNode);
        return writer.toString();
    }

    /**
     * @param writer
     * @param node
     * @param linkFormatter
     * @param includeNode
     */
    public void toHtml(PrintWriter writer, IWikiNode node, boolean includeNode) {
        InternalFormatter formatter = new InternalFormatter(writer);
        formatter.fTransformer = new WikiEventGenerator(formatter);
        formatter.fTransformer.execute(node, includeNode);
    }

}

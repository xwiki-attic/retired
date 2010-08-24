/*******************************************************************************
 * Copyright (c) 2005 Cognium Systems SA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Cognium Systems SA - initial API and implementation
 *******************************************************************************/
package org.semanticdesktop.wiki.core.adapter.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.wiki.core.WikiResource;
import org.semanticdesktop.wiki.core.WikiResourceAdapter;
import org.semanticdesktop.wikimodel.util.ListContext;
import org.semanticdesktop.wikimodel.wom.IWikiDocumentSection;
import org.semanticdesktop.wikimodel.wom.IWikiHeader;
import org.semanticdesktop.wikimodel.wom.IWikiNode;
import org.semanticdesktop.wikimodel.wom.IWikiNodeVisitor;
import org.semanticdesktop.wikimodel.wom.impl.WikiNodeVisitor;

/**
 * HTML adapter for wiki resources. This adapter uses
 * {@link WikiDocumentAdapter} to access to the internal structure of the wiki
 * document. It provide the following functionalities:
 * <ul>
 * <li>(X)HTML formatting of the wiki document</li>
 * <li> table of content generation (TOC) based on headers in the text</li>
 * <li>HTML import - transforms an HTML stream into an editable wiki document
 * (it replaces the wiki document in {@link WikiDocumentAdapter})</li>
 * </ul>
 * 
 * @author kotelnikov
 */
public class HtmlWikiPage extends WikiResourceAdapter {

    protected static class TocWriter extends ListContext {

        protected final PrintWriter fPrinter;

        /**
         * @param writer
         */
        public TocWriter(PrintWriter writer) {
            fPrinter = writer;
        }

        protected void alignLevel(int level) {
            StringBuffer buf = new StringBuffer();
            if (level > 0) {
                int i;
                for (i = 0; i < level; i++) {
                    buf.append(' ');
                }
                if (i <= level)
                    buf.append('-');
            }
            alignContext(buf.toString());
        }

        protected void beginRow(char rowType) {
            fPrinter.print("<li>");
        }

        protected void beginTree(char type) {
            fPrinter.println("<ul>");
        }

        /**
         * @param node
         * @param documentDepth
         * @param headerLevels
         * @param plain
         * @throws IOException
         */
        public void build(
            IWikiNode node,
            final int documentDepth,
            final int headerLevels,
            final boolean plain) throws IOException {
            if (node == null)
                return;
            IWikiNodeVisitor visitor = new WikiNodeVisitor() {

                private int fDepth;

                private int fHeaderLevel;

                private int fLevel;

                protected Set<String> fSet = new HashSet<String>();

                /**
                 * @see org.wikimodel.core.dom.IWikiNodeVisitor#visit(org.wikimodel.core.dom.IWikiDocumentSection)
                 */
                public void visit(IWikiDocumentSection n) {
                    if (fDepth >= documentDepth)
                        return;
                    fDepth++;
                    if (plain) {
                        super.visit(n);
                    } else {
                        int oldLevel = fLevel;
                        fLevel += fHeaderLevel + 1;
                        super.visit(n);
                        fLevel = oldLevel;
                    }
                    fDepth--;
                }

                /**
                 * @see org.wikimodel.core.dom.IWikiNodeVisitor#visit(org.wikimodel.core.dom.IWikiHeader)
                 */
                public void visit(IWikiHeader n) {
                    int level = n.getLevel();
                    if (level > headerLevels)
                        return;
                    fHeaderLevel = level;
                    alignLevel(fLevel + fHeaderLevel);
                    String key = FormatUtil.getHeaderLabel(fSet, n);
                    fPrinter.print("<a href='#" + key + "'>");
                    String str = FormatUtil.toHtml(
                        n,
                        FormatUtil.NULL_LINK_FORMATTER,
                        false);
                    fPrinter.print(str);
                    fPrinter.print("</a>");
                }

            };
            node.acceptVisitor(visitor);
            alignLevel(0);
        }

        protected void endRow(char rowType) {
            fPrinter.println("</li>");
        }

        protected void endTree(char type) {
            fPrinter.println("</ul>");
        }

    }

    private IWikiLinkFormatter fLinkFormatter;

    /**
     * @param wikiPage
     * @param linkFormatter
     */
    public HtmlWikiPage(WikiResource wikiPage, IWikiLinkFormatter linkFormatter) {
        super(wikiPage);
        fLinkFormatter = linkFormatter;
    }

    /**
     * @param reference
     * @return a formatted link
     */
    public String formatLink(String reference) {
        if (reference == null)
            return "";
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        String label = reference;
        if (reference.indexOf('.') > 0)
            label = reference.substring(reference.indexOf('.') + 1);
        getLinkFormatter().formatLink(label, reference, writer);
        String str = stringWriter.toString();
        return str;
    }

    /**
     * @see org.semanticdesktop.wiki.core.WikiResourceAdapter#getAdapterName()
     */
    @Override
    public String getAdapterName() {
        return "html";
    }

    /**
     * @param sectionKey
     * @return the string representation of the section with the given key
     */
    public String getHtml(String sectionKey) {
        IWikiDocumentSection section = getSection(sectionKey);
        return FormatUtil.toHtml(section, getLinkFormatter(), false);
    }

    /**
     * @param sectionKey
     * @return a document section corresponding to the given section key
     */
    protected IWikiDocumentSection getSection(String sectionKey) {
        return fWikiResource.getSection(sectionKey);
    }

    /**
     * @param sectionKey
     * @return the string representation of the section with the given key
     */
    public String getInlineHtml(String sectionKey) {
        IWikiDocumentSection section = getSection(sectionKey);
        return FormatUtil.toInlineHtml(section, getLinkFormatter());
    }

    protected IWikiLinkFormatter getLinkFormatter() {
        return fLinkFormatter;
    }

    /**
     * @param sectionKey for this section the TOC will be created
     * @param documentDepth the maximal section level to be shown in the
     * @param headerDepth
     * @return the string representation of the section with the given key
     */
    public String getTableOfContent(
        String sectionKey,
        int documentDepth,
        int headerDepth) {
        return getTableOfContent(sectionKey, documentDepth, headerDepth, false);
    }

    /**
     * @param sectionKey for this section the TOC will be created
     * @param documentDepth the maximal section level to be shown in the
     * @param headerDepth
     * @param plain if this flag is <code>true</code> then headers in embedded
     *        documents will be shown at the same level as the topmost headers
     * @return the string representation of the section with the given key
     */
    public String getTableOfContent(
        String sectionKey,
        int documentDepth,
        int headerDepth,
        boolean plain) {
        try {
            IWikiDocumentSection section = getSection(sectionKey);
            final StringWriter writer = new StringWriter();
            PrintWriter printer = new PrintWriter(writer);
            TocWriter t = new TocWriter(printer);
            t.build(section, documentDepth, headerDepth, plain);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sectionKey
     * @return the string representation of the section with the given key
     */
    public String getText(String sectionKey) {
        IWikiDocumentSection section = getSection(sectionKey);
        return FormatUtil.toText(section, false);
    }

    /**
     * @param label the label of the link
     * @param uri the uri to serialize into html
     * @return the serialized form of the given uri
     */
    public String toHtml(String label, String uri) {
        StringWriter writer = new StringWriter();
        getLinkFormatter().formatLink(label, uri, new PrintWriter(writer));
        return writer.toString();
    }

}
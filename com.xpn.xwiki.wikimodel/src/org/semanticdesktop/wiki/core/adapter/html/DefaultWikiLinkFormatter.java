/*
 * $Header$
 * $Revision$
 * $Date$
 */
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

import java.io.PrintWriter;

import org.semanticdesktop.wikimodel.impl.WikiPageUtil;

/**
 * @author kotelnikov
 */
public class DefaultWikiLinkFormatter implements IWikiLinkFormatter {

    /**
     * @param link
     * @return the formatted internal link
     */
    protected String formatInternalLink(String link) {
        return link;
    }

    /**
     * @see org.semanticdesktop.wiki.core.adapter.html.IWikiLinkFormatter#formatLink(java.lang.String,
     *      java.lang.String, java.io.PrintWriter)
     */
    public void formatLink(String label, String uri, PrintWriter writer) {
        if (uri == null)
            return;
        if (label == null || "".equals(label))
            label = uri;
        label = WikiPageUtil.escapeXmlString(label);
        // uri = WikiPageUtil.escapeXmlString(uri, true);
        uri = WikiPageUtil.encodeHttpParams(uri);

        String schema = getSchema(uri);
        boolean external = isExternal(schema);
        String link = uri;
        if (!external)
            link = formatInternalLink(uri);
        writer.print("<a href='"
            + link
            + "'"
            + (external ? " target='_blank'" : "")
            + ">"
            + label
            + "</a>");
    }

    /**
     * @param schema
     * @return <code>true</code> if the given link is an external url
     */
    protected boolean isExternal(String schema) {
        return schema.startsWith("http")
            || schema.startsWith("https")
            || schema.startsWith("ftp");
    }

    /**
     * @param uri
     * @return the schema for the given uri
     */
    protected String getSchema(String uri) {
        int schemaPos = uri.indexOf(':');
        String schema = "";
        if (schemaPos >= 0) {
            schema = uri.substring(0, schemaPos);
        }
        return schema;
    }

}

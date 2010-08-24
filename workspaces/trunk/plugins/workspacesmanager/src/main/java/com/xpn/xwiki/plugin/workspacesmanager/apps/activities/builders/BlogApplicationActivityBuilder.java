/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xpn.xwiki.plugin.workspacesmanager.apps.activities.builders;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

/**
 * Activity builder for the blog application.
 * 
 * @version $Id$
 */
public class BlogApplicationActivityBuilder extends DefaultActivityBuilder
{

    /**
     * {@inheritDoc}
     */
    protected String getDocumentTitle(XWikiDocument olddoc, XWikiDocument newdoc, XWikiContext context)
    {
        XWikiDocument doc;
        if (!olddoc.isNew()) {
            // creation or modification
            doc = olddoc;
        } else {
            // deletion
            doc = newdoc;
        }
        String title = new String();
        BaseObject article = doc.getObject("XWiki.ArticleClass", false, context);
        if (article != null) {
            title = article.getStringValue("title");
        }
        if (title.equals("")) {
            title = doc.getTitle();
        }
        if (title.equals("")) {
            title = doc.getName();
        }
        return title;
    }

}

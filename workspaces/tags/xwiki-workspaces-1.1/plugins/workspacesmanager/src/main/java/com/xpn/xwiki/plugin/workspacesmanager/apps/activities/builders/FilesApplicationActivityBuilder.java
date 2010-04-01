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
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;

/**
 * Activity builder for the files application
 * 
 * @version $Id: $
 */
public class FilesApplicationActivityBuilder extends ApplicationDefaultActivityBuilder
{

    protected String getDocumentTitle(XWikiDocument olddoc, XWikiDocument newdoc, XWikiContext context)
    {
        XWikiDocument doc;
        if (!olddoc.isNew()) { // creation or modification
            doc = olddoc;
        } else { // deletion
            doc = newdoc;
        }
        String title = new String();
        if (doc.getAttachmentList().size() > 0)
            title = ((XWikiAttachment) doc.getAttachmentList().get(0)).getFilename();
        title = olddoc.getTitle();
        if (title.equals("") && newdoc!=null)
            title = newdoc.getTitle();
        if (title.equals("") && !olddoc.isNew())
            title = olddoc.getName();
        else if (title.equals("")) title = newdoc.getName();
        return title;
    }

}

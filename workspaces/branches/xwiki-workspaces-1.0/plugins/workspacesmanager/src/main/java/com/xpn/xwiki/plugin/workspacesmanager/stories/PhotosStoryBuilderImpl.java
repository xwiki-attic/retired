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
package com.xpn.xwiki.plugin.workspacesmanager.stories;

import java.util.Date;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManager;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManagerException;

/**
 * A story builder for documents that belong to a Photo application
 * 
 * @version $Id: $
 */
public class PhotosStoryBuilderImpl implements StoryBuilder
{
    private static final String XWS_GALLERY_CLASSNAME = "XWSCode.PhotoGalleryClass";

    public Story createStory(String spaceName, String docFullName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            XWikiDocument doc = context.getWiki().getDocument(docFullName, context);
            String appName =
                ((WorkspacesManager) context.getWiki().getPlugin(
                    WorkspacesManager.WORKSPACESMANAGER_PLUGIN_NAME, context))
                    .getApplicationName(doc.getFullName(), context);
            String appURL =
                ((WorkspacesManager) context.getWiki().getPlugin(
                    WorkspacesManager.WORKSPACESMANAGER_PLUGIN_NAME, context))
                    .getApplicationURL(spaceName, appName, "WebHome", new String(), context);
            String url = doc.getURL("view", context);
            String title = new String();
            if (doc.getObject(XWS_GALLERY_CLASSNAME)==null)
            {
                // This is a photo, we use the filename as story title.
                if(doc.getAttachmentList().size()>0)
                {
                    XWikiAttachment att = (XWikiAttachment) doc.getAttachmentList().get(0);
                    title = att.getFilename();
                    url = doc.getURL("view","viewer=photo", context);
                }
            }
            if(title.equals(""))
                title = doc.getTitle();
            if (title.trim().equals(""))
                title = doc.getName();
            String author = doc.getContentAuthor();
            Date date = doc.getContentUpdateDate();
            if (!doc.getComment().equals(""))
                return new StoryImpl(title, url, appName, appURL, date, author, doc.getComment());
            return new StoryImpl(title, url, appName, appURL, date, author);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }
}

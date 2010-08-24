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
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.plugin.spacemanager.api.Space;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManager;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManagerException;

/**
 * StoryBuilder default implementation
 * 
 * @version $Id$
 */
public class DefaultStoryBuilderImpl implements StoryBuilder
{
    private static final String APPLICATION_HOME_MSG_KEY = "xws.stories.homeapplication";

    public Story createStory(String spaceName, String docFullName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            XWikiDocument doc = context.getWiki().getDocument(docFullName, context);
            String title = doc.getTitle();
            if (title.trim().equals(""))
                title = doc.getName();
            String url = doc.getURL("view", context);
            String appName;
            String appURL;
            if (spaceName.equals(doc.getSpace())) {
                // For the case the story happens on the space root (no application)
                // The title is obtain from the message tool (or "Home" if nothing found for the
                // key)
                // And the application name is the space pretty name.
                title = context.getMessageTool().get(APPLICATION_HOME_MSG_KEY);
                if (title.equals(APPLICATION_HOME_MSG_KEY))
                    title = "Home";
                Space s =
                    ((WorkspacesManager) context.getWiki().getPlugin("xwsmgr", context))
                        .getSpace(spaceName, context);
                appName = s.getDisplayTitle();
                appURL = context.getWiki().getURL(doc.getSpace() + ".WebHome", "view", context);
            } else {
                appName =
                    ((WorkspacesManager) context.getWiki().getPlugin(
                        WorkspacesManager.WORKSPACESMANAGER_PLUGIN_NAME, context))
                        .getApplicationName(doc.getFullName(), context);
                appURL =
                    ((WorkspacesManager) context.getWiki().getPlugin(
                        WorkspacesManager.WORKSPACESMANAGER_PLUGIN_NAME, context))
                        .getApplicationURL(spaceName, appName, "WebHome", new String(), context);
            }
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

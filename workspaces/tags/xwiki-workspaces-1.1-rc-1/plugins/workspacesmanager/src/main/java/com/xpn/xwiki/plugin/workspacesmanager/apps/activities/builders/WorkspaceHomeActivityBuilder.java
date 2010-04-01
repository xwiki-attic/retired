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
import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent;

/**
 * Activity builder for notifications that occurs on a workspace home page.
 * 
 * @version $Id: $
 */
public class WorkspaceHomeActivityBuilder extends AbstractActivityBuilder implements
    ActivityBuilder
{

    private final String WORKSPACE_ACTIVITY_HOMEPAGE_UPDATED_TITLE_KEY =
        "xws.activities.homepageupdated";

    private final String WORKSPACE_ACTIVITY_HOMEPAGE_UPDATED_LINK_KEY = "xws.workspace.homepage";

    public ActivityEvent createActivity(String streamName, String applicationName,
        XWikiDocument olddoc, XWikiDocument newdoc, XWikiContext context)
    {
        ActivityEvent activityEvent =
            super.prepareActivity(streamName, applicationName, olddoc, newdoc, context);
        activityEvent.setTitle(WORKSPACE_ACTIVITY_HOMEPAGE_UPDATED_TITLE_KEY);
        activityEvent.setBody(WORKSPACE_ACTIVITY_HOMEPAGE_UPDATED_TITLE_KEY);
        activityEvent.setParam1(context.getMessageTool().get(
            WORKSPACE_ACTIVITY_HOMEPAGE_UPDATED_LINK_KEY));
        return activityEvent;
    }

    protected String getDocumentTitle(XWikiDocument olddoc, XWikiDocument newdoc,
        XWikiContext context)
    {
        return new String();
    }

}

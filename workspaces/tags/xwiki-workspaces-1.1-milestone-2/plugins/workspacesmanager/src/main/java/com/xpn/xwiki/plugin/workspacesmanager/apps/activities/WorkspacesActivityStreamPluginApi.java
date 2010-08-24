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
package com.xpn.xwiki.plugin.workspacesmanager.apps.activities;

import java.util.Collections;
import java.util.List;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.plugin.activitystream.plugin.ActivityEvent;
import com.xpn.xwiki.plugin.activitystream.plugin.ActivityStreamPluginApi;
import com.xpn.xwiki.plugin.workspacesmanager.Workspace;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManager;

/**
 * @version $Id$
 */
public class WorkspacesActivityStreamPluginApi extends ActivityStreamPluginApi
{

    public WorkspacesActivityStreamPluginApi(WorkspacesActivityStreamPlugin plugin,
        XWikiContext context)
    {
        super(plugin, context);
    }

    public List<ActivityEvent> getMyEvents(String userName, int nb, int start)
    {
        if (hasProgrammingRights()) {
            try {
                return wrapEvents(((WorkspacesActivityStreamPlugin) getProtectedPlugin())
                    .getMyEvents(userName, nb, start, context));
            } catch (XWikiException e) {
                context.put("haserror", "1");
                context.put("lasterror", e.getMessage());
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    public List<ActivityEvent> getEventsForWorkspace(String spaceName, int nb, int start)
    {
        try {
            WorkspacesManager wm =
                (WorkspacesManager) context.getWiki().getPlugin("xwsmgr", context);
            Workspace workspace = (Workspace) wm.getSpace(spaceName, context);
            // if the request comes from the workspace itself, we trust and give the stream
            if (context.getDoc().getSpace().equals(workspace.getSpace())) {
                return wrapEvents(this.getActivityStream().getEvents(spaceName, false, nb, start,
                    context));
            } else {
                // otherwise let pass the request to the activitystream api
                // that will check for programming rights
                return super.getEvents(spaceName, false, nb, start);
            }
        } catch (XWikiException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return Collections.emptyList();
        }
    }
}

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

import java.util.List;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent;
import com.xpn.xwiki.plugin.activitystream.api.ActivityStream;
import com.xpn.xwiki.plugin.activitystream.api.ActivityStreamException;

/**
 * XWiki Workspaces activity stream inteface. Extends the standard activity interface adding method
 * to add/retrieve events that are specific to XWS.
 * 
 * @version $Id: $
 */
public interface WorkspaceActivityStream extends ActivityStream
{

    /**
     * Returns all activity events for the passed user. Searches in all event streams corresponding to the
     * workspaces the user is a member of.
     * 
     * @param userName the wiki name of the user to retrieve activity events for
     * @param nb the number of events to retrieve
     * @param start the offset to start retrieving events at
     * @param context the XWiki context
     * @return the list of activity events retrieved for the user
     * @throws ActivityStreamException an exception that can occur while searching the events
     */
    List<ActivityEvent> getMyEvents(String userName, int nb, int start, XWikiContext context)
        throws ActivityStreamException;

    /**
     * Write in the activity stream an event related to a member in a workspace. Possible events are :
     * <ul>
     * <li>A new member join the workspace</li>
     * <li>An existing member changes group inside a workspace</li>
     * <li>An existing member leaves a workspaces</li>
     * </ul>
     * 
     * @param workspaceName the name of the workspaces the event occurs in
     * @param userName the name of the user concerned by the activity event
     * @param oldgroup the old group of the user in the workspace. Should be "null" if the member is new to the
     *            workspace.
     * @param newgroup the new group of the user in the workspace. Should be "null" if the member leaves the workspace.
     * @param context the XWiki context.
     * @throws ActivityStreamException an exception that can occur when writing the event to the database.
     */
    void addWorkspaceMemberActivityEvent(String workspaceName, String userName, String oldgroup,
        String newgroup, XWikiContext context) throws ActivityStreamException;

}

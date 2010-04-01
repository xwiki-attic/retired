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
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.WorkspaceActivityStreamException;

/**
 * Builder interface to create {@link com.xpn.xwiki.plugin.activitystream.api.ActivityEvent} upon
 * notification, for workspaces.
 * 
 * @version $Id: $
 */
public interface ActivityBuilder
{

    /**
     * Create an activity event filled with data retrieved from passed arguments.
     * 
     * @param streamName the name of the stream the event will flow in.
     * @param applicationName the name of the application the event is fired from
     * @param olddoc the old version of the document that triggers the notification
     * @param newdoc the new version of the document that triggers the notification
     * @param context the XWiki context.
     * @return the filled event
     * @throws WorkspaceActivityStreamException any error that can occur during this process. 
     */
    ActivityEvent createActivity(String streamName, String applicationName,
        XWikiDocument olddoc, XWikiDocument newdoc, XWikiContext context)
        throws WorkspaceActivityStreamException;

}

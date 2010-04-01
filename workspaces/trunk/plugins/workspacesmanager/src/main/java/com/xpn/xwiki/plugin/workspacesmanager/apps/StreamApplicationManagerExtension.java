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
package com.xpn.xwiki.plugin.workspacesmanager.apps;

import java.util.List;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.plugin.activitystream.api.ActivityStreamException;
import com.xpn.xwiki.plugin.activitystream.plugin.ActivityEvent;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManagerException;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.WorkspacesActivityStreamPlugin;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.WorkspacesActivityStreamPluginApi;

/**
 * Stream application manager extension.
 * 
 * @version $Id: $
 */
public class StreamApplicationManagerExtension implements ApplicationManagerExtension
{

    /**
     * {@inheritDoc}
     */
    public void postInstall(String web, XWikiContext context) throws WorkspacesManagerException
    {
        // do nothing
    }

    /**
     * {@inheritDoc}
     * 
     * Deletes all activity events from the activitystream table located in that web.
     */
    public void postUninstall(String web, XWikiContext context) throws WorkspacesManagerException
    {
        // delete all activity events in the stream from that application
        WorkspacesActivityStreamPluginApi asApi =
            (WorkspacesActivityStreamPluginApi) context.getWiki().getPluginApi(
                WorkspacesActivityStreamPlugin.PLUGIN_NAME, context);
        String hql = "act.space='" + web + "' and act.type='workspace_stream' ";
        try {
            List<ActivityEvent> evs = asApi.searchEvents(hql, false, 0, 0);
            asApi.deleteActivityEvents(evs);
        } catch (ActivityStreamException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void preInstall(String web, XWikiContext context) throws WorkspacesManagerException
    {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void preUninstall(String web, XWikiContext context) throws WorkspacesManagerException
    {
        // do nothing
    }

}

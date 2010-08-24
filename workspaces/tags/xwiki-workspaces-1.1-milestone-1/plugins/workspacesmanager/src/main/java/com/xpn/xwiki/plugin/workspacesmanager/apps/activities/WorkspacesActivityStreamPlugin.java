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

import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent;
import com.xpn.xwiki.plugin.activitystream.api.ActivityStreamException;
import com.xpn.xwiki.plugin.activitystream.plugin.ActivityStreamPlugin;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

/**
 * @version $Id$
 */
public class WorkspacesActivityStreamPlugin extends ActivityStreamPlugin
{
    
    public static final String PLUGIN_NAME = "xwsactivitystream";

    public WorkspacesActivityStreamPlugin(String name, String className, XWikiContext context)
    {
        super(name, className, context);
        setActivityStream(new WorkspacesActivityStream());
    }

    public String getName()
    {
        return PLUGIN_NAME;
    }

    public List<ActivityEvent> getMyEvents(String userName, int nb, int start, XWikiContext context)
        throws ActivityStreamException
    {
        return ((WorkspacesActivityStream) this.getActivityStream()).getMyEvents(userName, nb,
            start, context);
    }

    /**
     * Gets the activity plugin Api
     * 
     * @param plugin The plugin interface
     * @param context Xwiki context
     * @return
     */
    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context)
    {
        return new WorkspacesActivityStreamPluginApi((WorkspacesActivityStreamPlugin) plugin,
            context);
    }

}

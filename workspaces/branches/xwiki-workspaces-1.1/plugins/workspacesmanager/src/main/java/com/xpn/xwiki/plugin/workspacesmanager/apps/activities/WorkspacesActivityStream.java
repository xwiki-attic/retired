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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Object;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.notify.XWikiNotificationRule;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent;
import com.xpn.xwiki.plugin.activitystream.api.ActivityStreamException;
import com.xpn.xwiki.plugin.activitystream.impl.ActivityStreamImpl;
import com.xpn.xwiki.plugin.applicationmanager.ApplicationManagerPlugin;
import com.xpn.xwiki.plugin.applicationmanager.ApplicationManagerPluginApi;
import com.xpn.xwiki.plugin.applicationmanager.doc.XWikiApplication;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.workspacesmanager.Workspace;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManager;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.builders.ActivityBuilder;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.builders.ApplicationDefaultActivityBuilder;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.builders.WorkspaceHomeActivityBuilder;

/**
 * @version $Id: $
 */
public class WorkspacesActivityStream extends ActivityStreamImpl
{

    /**
     * Log object to log messages in this class.
     */
    private static final Log LOG = LogFactory.getLog(WorkspacesActivityStream.class);

    /**
     * Name of the field describing the activity builder to use in XWiki.XWSApplicationClass XClass
     */
    private static final String XWS_APP_ACTIVITY_BUILDER_FIELD = "activity_builder";

    private static WorkspacesActivityStream instance = new WorkspacesActivityStream();
    
    /**
     * Private constructor to ensure their is always a single instance at a time,
     * thus a single object that listens to document change notifications.
     */
    private WorkspacesActivityStream()
    {
    }
    
    /**
     * @return the unique instance of the workspaces activity stream
     */
    public static WorkspacesActivityStream getInstance()
    {
        return WorkspacesActivityStream.instance;
    }
    
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
    public List<ActivityEvent> getMyEvents(String userName, int nb, int start, XWikiContext context)
        throws ActivityStreamException
    {
        //TODO Parametrize query when activitystream plugin implements search with parameters.
        String myStreamsHql =
            "select distinct doc.web from XWikiDocument as doc, XWikiDocument as wsPrefs, BaseObject as wsObj, StringProperty as spType,"
                + " BaseObject as obj, StringProperty as memberprop"
                + " where doc.name='MemberGroup' and doc.fullName=obj.name and obj.className = 'XWiki.XWikiGroups'"
                + " and obj.id=memberprop.id.id and memberprop.id.name='member' and memberprop.value='"
                + userName
                + "' and wsObj.name=wsPrefs.fullName and wsPrefs.name='WebPreferences' and wsObj.className='XWiki.SpaceClass'"
                + " and spType.id.id=wsObj.id and spType.id.name='type' and spType.value='workspace' and doc.web=wsPrefs.web";
        return searchEvents("act.stream in (" + myStreamsHql + ")", false, nb, start, context);
    }

    protected ActivityBuilder getBuilderForApplication(String applicationName,
        XWikiContext context)
    {
        ActivityBuilder builder = null;

        ApplicationManagerPluginApi appmanager =
            (ApplicationManagerPluginApi) context.getWiki().getPluginApi(
                ApplicationManagerPlugin.PLUGIN_NAME, context);
        try {
            // Retrieve the application descriptor
            XWikiApplication app = appmanager.getApplicationDocument(applicationName);
            if (app != null) {
                Object xwsApp = app.getObject("XWiki.XWSApplicationClass");
                if (xwsApp != null) {
                    String builderClassName = (String) xwsApp.get(XWS_APP_ACTIVITY_BUILDER_FIELD);
                    builder = (ActivityBuilder) Class.forName(builderClassName).newInstance();
                }
            }
        } catch (Exception e) {
            // do nothing and fold back on the application default builder
        }
        if (builder == null)
            builder = new ApplicationDefaultActivityBuilder();
        return builder;
    }

    public void notify(XWikiNotificationRule rule, XWikiDocument olddoc, XWikiDocument newdoc,
        int event, XWikiContext context)
    {
        // first check if the document belongs to a workspace
        WorkspacesManager wm = (WorkspacesManager) context.getWiki().getPlugin("xwsmgr", context);
        try {
            // Minor edit won't generate activities
            if (newdoc != null && newdoc.isMinorEdit())
                return;
            XWikiDocument doc = olddoc;
            Workspace workspace = (Workspace) wm.getRootSpace(doc.getSpace(), context);
            if (workspace == null || workspace.isNew())
                return;

            String streamName = workspace.getSpace();
            String application = wm.getApplicationName(doc.getFullName(), context);

            ActivityBuilder ab;
            ActivityEvent activityEvent;

            // look if it's a application document
            if (!application.equals("") && !doc.getName().equals("WebHome")
                && !doc.getName().equals("WebPreferences")) {
                ab = getBuilderForApplication(application, context);
                try {
                    activityEvent =
                        ab.createActivity(streamName, application, olddoc, newdoc, context);
                    this.addActivityEvent(activityEvent, doc, context);
                } catch (ActivityStreamException e) {
                    if (e.getCode() == WorkspaceActivityStreamException.XWSACTIVITYSTREAM_NO_ACTIVITY_FOR_NOTIFICATION) {
                        // silently do nothing as this is expected
                    } else {
                        LOG.error("Error while recording an workspace activity", e);
                    }
                }
            }
            // look if it's the space home page
            else if (doc.getName().equals("WebHome") && workspace.getSpaceName().equals(doc.getSpace())) {
                ab = new WorkspaceHomeActivityBuilder();
                try {
                    activityEvent =
                        ab.createActivity(streamName, application, olddoc, newdoc, context);
                    this.addActivityEvent(activityEvent, doc, context);
                } catch (ActivityStreamException e) {
                    // silently do nothing
                }
            }
        } catch (SpaceManagerException e) {
            LOG.error("Error retrieving the workspace while processing activity notification", e);
        }
    }
}

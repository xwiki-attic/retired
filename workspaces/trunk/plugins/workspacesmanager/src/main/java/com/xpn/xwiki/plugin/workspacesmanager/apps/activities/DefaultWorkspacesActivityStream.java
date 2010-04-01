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
import com.xpn.xwiki.plugin.activitystream.impl.ActivityEventImpl;
import com.xpn.xwiki.plugin.activitystream.impl.ActivityStreamImpl;
import com.xpn.xwiki.plugin.applicationmanager.ApplicationManagerPlugin;
import com.xpn.xwiki.plugin.applicationmanager.ApplicationManagerPluginApi;
import com.xpn.xwiki.plugin.applicationmanager.doc.XWikiApplication;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.workspacesmanager.Workspace;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManager;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.builders.ActivityBuilder;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.builders.DefaultActivityBuilder;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.builders.WorkspaceHomeActivityBuilder;

/**
 * Default implementation of the XWiki Workspaces activity stream.
 * 
 * @version $Id: $
 */
public final class DefaultWorkspacesActivityStream extends ActivityStreamImpl implements WorkspaceActivityStream
{

    /**
     * Wiki document name for the WebHome of a wiki space.
     */
    private static final String WEB_HOME = "WebHome";

    /**
     * Dot, used as separator between space and page in a wiki document full name.
     */
    private static final String DOT = ".";

    /**
     * Log object to log messages in this class.
     */
    private static final Log LOG = LogFactory.getLog(DefaultWorkspacesActivityStream.class);

    /**
     * Name of the field describing the activity builder to use in XWiki.XWSApplicationClass XClass.
     */
    private static final String XWS_APP_ACTIVITY_BUILDER_FIELD = "activity_builder";
    
    /**
     * The unique instance of this class, to ensure only one activity stream is running at a time. 
     */
    private static DefaultWorkspacesActivityStream instance = new DefaultWorkspacesActivityStream();
    
    /**
     * Private constructor to ensure their is always a single instance at a time,
     * thus a single object that listens to document change notifications.
     */
    private DefaultWorkspacesActivityStream()
    {
    }
    
    /**
     * @return the unique instance of the workspaces activity stream
     */
    public static DefaultWorkspacesActivityStream getInstance()
    {
        return DefaultWorkspacesActivityStream.instance;
    }
    
    /** 
     * {@inheritDoc}
     */
    public List<ActivityEvent> getMyEvents(String userName, int nb, int start, XWikiContext context)
        throws ActivityStreamException
    {
        //TODO Parametrize query when activitystream plugin implements search with parameters.
        String myStreamsHql =
            "select distinct doc.web from XWikiDocument as doc, XWikiDocument as wsPrefs, BaseObject as wsObj, "
            + "StringProperty as spType, BaseObject as obj, StringProperty as memberprop"
                + " where doc.name='MemberGroup' and doc.fullName=obj.name and obj.className = 'XWiki.XWikiGroups'"
                + " and obj.id=memberprop.id.id and memberprop.id.name='member' and memberprop.value='"
                + userName
                + "' and wsObj.name=wsPrefs.fullName and wsPrefs.name='WebPreferences' "
                + "and wsObj.className='XWiki.SpaceClass' and spType.id.id=wsObj.id "
                + "and spType.id.name='type' and spType.value='workspace' and doc.web=wsPrefs.web";
        return searchEvents("act.stream in (" + myStreamsHql + ")", false, nb, start, context);
    }


    /**
     * {@inheritDoc}
     */
    public void addWorkspaceMemberActivityEvent(String workspaceName, String userName, String oldgroup,
        String newgroup, XWikiContext context) throws ActivityStreamException
    {
        ActivityEvent ae = new ActivityEventImpl();
        ae.setStream(workspaceName);
        ae.setSpace(workspaceName);
        ae.setApplication("");
        ae.setUser(userName);
        ae.setType("member");
        String body = "xws.activities.member.";
        if (newgroup == null) {
            body += "leave";
            ae.setPage(workspaceName + DOT + oldgroup);
            
        } else if (oldgroup == null) {
            body += "join";
            ae.setPage(workspaceName + DOT + newgroup);
        } else {
            body += "change";
            ae.setPage(workspaceName + DOT + newgroup);
        }
        ae.setParam1(oldgroup);
        ae.setParam2(newgroup);
        ae.setBody(body);
        ae.setTitle(body);
        super.addActivityEvent(ae, context);
    }
    
    /**
     * Retrieve the proper activity builder for the passed application. Tries to load the class precised in the
     * application descriptor document, if it is indicated. If the loading fails or if no specific builder
     * is indicated in the descriptor, foldback on a default application builder.
     * 
     * @param applicationName the name of the application to retrieve the activity builer for.
     * @param context the XWiki context
     * @return the activity builder for the passed application.
     */
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
        if (builder == null) {
            builder = new DefaultActivityBuilder();
        }
        return builder;
    }

    /**
     * {@inheritDoc}
     */
    public void notify(XWikiNotificationRule rule, XWikiDocument olddoc, XWikiDocument newdoc,
        int event, XWikiContext context)
    {
        // first check if the document belongs to a workspace
        WorkspacesManager wm = (WorkspacesManager) context.getWiki().getPlugin("xwsmgr", context);
        try {
            // Minor edit won't generate activities
            if (newdoc != null && newdoc.isMinorEdit()) {
                return;
            }
            XWikiDocument doc = olddoc;
            Workspace workspace = (Workspace) wm.getRootSpace(doc.getSpace(), context);
            if (workspace == null || workspace.isNew()) {
                return;
            }

            String streamName = workspace.getSpace();
            String application = wm.getApplicationName(doc.getFullName(), context);

            ActivityBuilder ab = null;
            ActivityEvent activityEvent = null;

            // look if it's a application document
            if (isApplicationDataDocument(application, doc)) {
                ab = getBuilderForApplication(application, context);
            } else if (isWorkspaceHome(doc, workspace)) {
             // look if it's the space home page 
                ab = new WorkspaceHomeActivityBuilder();
            }
            
            activityEvent = buildActivity(ab, streamName, application, olddoc, newdoc, context);
            if (activityEvent != null) {
                this.addActivityEvent(activityEvent, doc, context);
            }
        } catch (SpaceManagerException e) {
            LOG.error("Error retrieving the workspace while processing activity notification", e);
        } catch (ActivityStreamException e) {
            LOG.error("Error while recording an workspace activity", e);
        }
    }
    
    /**
     * @param applicationName the name of the application in which the document is located
     * @param doc the document to test
     * @return true if the document is a document that holds data of an application, for example a blog post,
     *  or a photo gallery. false otherwise (for example if the document is a UI).
     */
    private boolean isApplicationDataDocument(String applicationName, XWikiDocument doc)
    {
        if (!applicationName.equals("") && !doc.getName().equals(WEB_HOME)
            && !doc.getName().equals("WebPreferences")) {
            return true;
        }
        return false;
    }
    
    /**
     * @param doc the document to test
     * @param workspace the workspace the document could be the Home page of
     * @return true if the passed document is the Home page of the passed workspaces. false otherwise.
     */
    private boolean isWorkspaceHome(XWikiDocument doc, Workspace workspace)
    {
        if (doc.getName().equals(WEB_HOME) && workspace.getSpaceName().equals(doc.getSpace())) {
            return true;
        }
        return false;
    }
    
    /**
     * Builds an activity event using the passed builder and arguments.
     * 
     * @param builder the builder that will create the activity
     * @param streamName the name of the stream to create the activity in
     * @param application the name of the application the activity concerns
     * @param olddoc the document before the change that triggered the activity notification
     * @param newdoc the document after the change that triggered the activity notification
     * @param context the XWikiContext
     * @return the builded activity
     * @throws ActivityStreamException an exception occuring while creating the activity
     */
    private ActivityEvent buildActivity(ActivityBuilder builder, String streamName, String application,
        XWikiDocument olddoc, XWikiDocument newdoc, XWikiContext context) throws ActivityStreamException
    {
        if (builder == null) {
            return null;
        }
        try {
            return builder.createActivity(streamName, application, olddoc, newdoc, context);
        } catch (ActivityStreamException e) {
            if (e.getCode() == WorkspaceActivityStreamException.XWSACTIVITYSTREAM_NO_ACTIVITY_FOR_NOTIFICATION) {
                // silently do nothing as this is the purpose of this exception : not create any activity.
                return null;
            } else {
                throw e;
            }
        }
    }
}

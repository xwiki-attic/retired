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

package com.xpn.xwiki.plugin.workspacesmanager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEventType;
import com.xpn.xwiki.plugin.spacemanager.api.Space;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManager;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.spacemanager.impl.SpaceManagerExtensionImpl;
import com.xpn.xwiki.plugin.workspacesmanager.apps.WorkspaceApplicationManager;
import com.xpn.xwiki.plugin.workspacesmanager.apps.WorkspaceApplicationManagerImpl;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.WorkspacesActivityStreamPluginApi;

/**
 * SpaceManager extension for Workspaces manager plugin
 * 
 * @version $Id: $
 */
public class WorkspacesManagerExtension extends SpaceManagerExtensionImpl
{
    public static final String XWIKI_SPACE_SEPARATOR = ".";

    public static final String XWIKI_WORKSPACE_APPSEPARATOR = "_";

    protected static final String WORKSPACE_SPACE_TYPE = "workspace";

    public static final String WORKSPACE_SPACE_CLASS_NAME = "XWiki.WorkspaceSpaceClass";

    protected static final String WORKSPACE_ROLE_READER_CODE = "reader";

    protected static final String WORKSPACE_ROLE_READER_LEVELS = "view, comment";

    protected static final String WORKSPACE_ROLE_READER_GROUP = "ReaderGroup";

    protected static final String WORKSPACE_ROLE_WRITER_CODE = "writer";

    protected static final String WORKSPACE_ROLE_WRITER_LEVELS = "edit, view, comment";

    protected static final String WORKSPACE_ROLE_WRITER_GROUP = "WriterGroup";

    protected static final String WORKSPACE_PREFERENCES_SHEET = "XWSCode.SpacePreferences";

    protected static final String WORKSPACE_EVENT_SPACE_CREATION_KEY =
        "xws.activities.workspacecreated";

    protected static final Log LOG = LogFactory.getLog(WorkspacesManagerExtension.class);

    protected static Map<String, String> roles = new HashMap<String, String>();

    private WorkspaceApplicationManager appManager;

    /**
     * {@inheritDoc}
     */
    public void init(SpaceManager manager, XWikiContext context) throws SpaceManagerException
    {
        try {
            updateWorkspaceSpaceClass(context);
            this.sm = manager;
            roles.put(WORKSPACE_ROLE_READER_CODE, WORKSPACE_ROLE_READER_GROUP);
            roles.put(WORKSPACE_ROLE_WRITER_CODE, WORKSPACE_ROLE_WRITER_GROUP);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void virtualInit(SpaceManager manager, XWikiContext context)
        throws SpaceManagerException
    {
        try {
            updateWorkspaceSpaceClass(context);
            this.sm = manager;
            roles.put(WORKSPACE_ROLE_READER_CODE, WORKSPACE_ROLE_READER_GROUP);
            roles.put(WORKSPACE_ROLE_WRITER_CODE, WORKSPACE_ROLE_WRITER_GROUP);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    public void setApplicationManager(WorkspaceApplicationManager appManager)
    {
        this.appManager = appManager;
    }

    private WorkspaceApplicationManager getXWSApplicationManager()
    {
        if (this.appManager == null) {
            this.appManager = new WorkspaceApplicationManagerImpl();
        }
        return appManager;
    }

    /**
     * {@inheritDoc}
     */
    public String getSpaceTypeName()
    {
        return WORKSPACE_SPACE_TYPE;
    }

    /**
     * @return the name of the document that holds a Workspace XClass definition.
     */
    public String getWorkspaceSpaceClassName()
    {
        return WORKSPACE_SPACE_CLASS_NAME;
    }

    /**
     * @return the full name of the document that holds the XWiki.XWikiGroups XObjects with all the
     *         workspace members
     */
    public String getMemberGroupName(String spaceName)
    {
        return spaceName + XWIKI_SPACE_SEPARATOR + "MemberGroup";
    }

    /**
     * @return the full name of the document that holds the XWiki.XWikiGroups XObjects with all the
     *         workspace admins
     */
    public String getAdminGroupName(String spaceName)
    {
        return spaceName + XWIKI_SPACE_SEPARATOR + "AdminGroup";
    }

    /**
     * Updates the Workspace class definition
     * 
     * @throws XWikiException
     */
    protected void updateWorkspaceSpaceClass(XWikiContext context) throws XWikiException
    {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(getWorkspaceSpaceClassName(), context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(getWorkspaceSpaceClassName());
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(getWorkspaceSpaceClassName());

        String content = doc.getContent();
        if ((content == null) || (content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 XWiki Workspace Space Class");
        }

        String levelsFields =
            Workspace.WORKSPACE_ACCESSLEVEL_OPEN + "|" + Workspace.WORKSPACE_ACCESSLEVEL_PUBLIC
                + "|" + Workspace.WORKSPACE_ACCESSLEVEL_PRIVATE;

        String colorField = new String();

        for (int i = 0; i < Workspace.WORKSPACE_COLOR_VALUES.length; i++) {
            String color = Workspace.WORKSPACE_COLOR_VALUES[i];
            colorField += color;
            if (i != (Workspace.WORKSPACE_COLOR_VALUES.length - 1))
                colorField += "|";
        }

        needsUpdate |=
            bclass.addStaticListField(Workspace.WORKSPACE_ACCESSLEVEL, "Workspace Access Level",
                1, false, levelsFields, "select");
        needsUpdate |=
            bclass.addStaticListField(Workspace.WORKSPACE_SPACETYPE, "Workspace Space Type", 1,
                false, Workspace.WORKSPACE_SPACETYPE_WORK + "|"
                    + Workspace.WORKSPACE_SPACETYPE_USER + "|"
                    + Workspace.WORKSPACE_SPACETYPE_ORG, "select");
        needsUpdate |=
            bclass.addStaticListField(Workspace.WORKSPACE_COLOR, "Workspace Color", 1, false,
                colorField);

        if (needsUpdate) {
            xwiki.saveDocument(doc, context);
        }

    }

    /**
     * {@inheritDoc}
     */
    public String getRoleGroupName(String spaceName, String roleName)
    {
        String roleDocName = roles.get(roleName);
        if (roleDocName != null)
            return spaceName + XWIKI_SPACE_SEPARATOR + roleDocName;
        return null;
    }

    /**
     * Operations executed after the actual {@link Space} creation. For a workspace, consist in
     * intializing the space rights, according to its visivility, private or public, and publish the
     * first story of its activity stream . {@inheritDoc}
     */
    public void postCreateSpace(String spaceName, XWikiContext context)
        throws SpaceManagerException
    {
        try {
            // Set the workspace and its application rights

            Workspace w = (Workspace) sm.getSpace(spaceName, context);
            // set rights for reader group
            ((WorkspacesManager) sm).setReadersRights(spaceName, context);
            // set rights for writer group
            ((WorkspacesManager) sm).setWritersRights(spaceName, context);
            // compute and save the access level
            String accessLevel = getAccessLevelForRequest(context);
            w.set(Workspace.WORKSPACE_ACCESSLEVEL, accessLevel);
            w.saveWithProgrammingRights("Updated access level");
            // set rights accordingly
            if (accessLevel.equals(Workspace.WORKSPACE_ACCESSLEVEL_PRIVATE)) {
                makeSpacePrivate(spaceName, context);
            } else if (accessLevel.equals(Workspace.WORKSPACE_ACCESSLEVEL_OPEN)) {
                makeSpaceOpen(spaceName, context);
            } else {
                makeSpacePublic(spaceName, context);
            }

            // set content and install applications
            prepareWorkspaceContent(spaceName, context);

            // publish an activity story about the workspace creation
            XWikiDocument doc = context.getWiki().getDocument(w.getSpace() + ".WebHome", context);

            WorkspacesActivityStreamPluginApi wasApi =
                (WorkspacesActivityStreamPluginApi) context.getWiki().getPluginApi(
                    "xwsactivitystream", context);
            wasApi.addDocumentActivityEvent(w.getSpace(), new Document(doc, context),
                ActivityEventType.CREATE_SPACE, WORKSPACE_EVENT_SPACE_CREATION_KEY);
        } catch (XWikiException e) {
            throw new SpaceManagerException();
        }
    }

    /**
     * After space deletion by the spacemanager plugin (see
     * {@link com.xpn.xwiki.plugin.spacemanager.api.SpaceManager#deleteSpace(String, boolean, XWikiContext)},
     * delete inherited spaces documents (Applications spaces documents) of the workspace.
     * 
     * @param spaceName the name of the root space (web) of the workspace for which to delete
     *            application data. This is the name present in the 'parent' field of the
     *            XWikiPreferences object of the application spaces (webs).
     * @param if true, will delete the application documents, will do nothing otherwise.
     */
    public void postDeleteSpace(String spaceName, boolean deleteData, XWikiContext context)
    {
        if (deleteData) {
            // Look for all documents that belong to a space that inherit from
            // the workspace root space.
            String hql =
                ", XWikiDocument as sp"
                    + ", BaseObject as xws, BaseObject as prefs, StringProperty as parent"
                    + " where sp.web='" + spaceName
                    + "' and sp.name='WebPreferences' and xws.name=sp.fullName"
                    + " and xws.className='" + getWorkspaceSpaceClassName() + "'"
                    + " and prefs.className='XWiki.XWikiPreferences' and prefs.name=doc.fullName"
                    + " and parent.id.id=prefs.id and parent.id.name='parent' and parent.value='"
                    + spaceName + "'";
            try {
                List<XWikiDocument> allSpaceDocs =
                    context.getWiki().getStore().searchDocuments(hql, context);
                for (XWikiDocument toBeDeleted : allSpaceDocs) {
                    context.getWiki().deleteDocument(toBeDeleted, context);
                }
            } catch (XWikiException e) {
                // silently fail but log the error
                LOG.error("Failed to delete workspace data after deleting workspace ["
                    + spaceName + "]", e);
            }
        }
    }

    /**
     * Helper to check if an access level from a request correspond to an actual level of a
     * Workspace.
     */
    private boolean isValidLevel(String level)
    {
        if (level.equals(Workspace.WORKSPACE_ACCESSLEVEL_PRIVATE)
            || level.equals(Workspace.WORKSPACE_ACCESSLEVEL_PUBLIC)
            || level.equals(Workspace.WORKSPACE_ACCESSLEVEL_OPEN))
            return true;
        return false;
    }

    /**
     * Helper to obtain the space type (@see Workspace.WORKSPACE_SPACETYPE) from a workspace
     * creation request.
     */
    private String getSpaceTypeForRequest(XWikiContext context)
    {
        String spaceType;
        if (context.getRequest().getParameter(getWorkspaceSpaceClassName() + "_0_spacetype") != null)
            spaceType =
                context.getRequest().getParameter(getWorkspaceSpaceClassName() + "_0_spacetype");
        else
            spaceType = Workspace.WORKSPACE_SPACETYPE_DEFAULT;
        if (spaceType.equals(Workspace.WORKSPACE_SPACETYPE_USER)
            || spaceType.equals(Workspace.WORKSPACE_SPACETYPE_ORG)
            || spaceType.equals(Workspace.WORKSPACE_SPACETYPE_WORK))
            return spaceType;
        return Workspace.WORKSPACE_SPACETYPE_DEFAULT;
    }

    /**
     * Helper to obtain the space access level (@see Workspace.WORKSPACE_ACCESSLEVEL) from a
     * workspace creation request.
     * 
     * @param context
     * @return
     */
    private String getAccessLevelForRequest(XWikiContext context)
    {
        String accessLevel;
        if (getSpaceTypeForRequest(context).equals(Workspace.WORKSPACE_SPACETYPE_USER)) {
            accessLevel =
                context.getWiki().getXWikiPreference("xws_userspace_defaultlevel", context);
            if (!isValidLevel(accessLevel)) {
                accessLevel =
                    context.getWiki().Param("xwiki.workspaces.userspace.defaultlevel",
                        Workspace.WORKSPACE_DEFAULTACCESSLEVEL_USER);
                if (!isValidLevel(accessLevel))
                    accessLevel = Workspace.WORKSPACE_DEFAULTACCESSLEVEL_USER;
            }
        } else if (getSpaceTypeForRequest(context).equals(Workspace.WORKSPACE_SPACETYPE_ORG)) {
            accessLevel = Workspace.WORKSPACE_ACCESSLEVEL_PUBLIC;
        } else {
            accessLevel =
                context.getRequest()
                    .getParameter(getWorkspaceSpaceClassName() + "_0_accesslevel");
            if (!isValidLevel(accessLevel))
                accessLevel = Workspace.WORKSPACE_DEFAULTACCESSLEVEL;
        }
        return accessLevel;
    }

    /**
     * Prepare a newly created Workspace by adding some document for it. Iterate over an application
     * list, and for each application to install in the workspace, copy or link inside the workspace
     * resources defined by the application object.
     * 
     * @throws SpaceManagerException
     */
    protected void prepareWorkspaceContent(String spaceName, XWikiContext context)
        throws SpaceManagerException
    {
        // set space WebPreferences content
        try {
            XWikiDocument pDoc =
                context.getWiki().getDocument(spaceName + ".WebPreferences", context);
            pDoc.setContent("#includeInContext('" + WORKSPACE_PREFERENCES_SHEET + "')");
            context.getWiki().saveDocument(pDoc, context);
        } catch (XWikiException e1) {
            throw new WorkspacesManagerException(e1);
        }

        // lookup applications requested
        Set<String> requestedApps = new HashSet<String>();

        Set<String> available =
            getXWSApplicationManager().getAvailableApplicationsNames(spaceName, context);

        for (String application : available) {
            if (context.getRequest().getParameter("application_" + application) != null
                && !context.getRequest().getParameter("application_" + application).equals("")) {
                requestedApps.add(application);
            }
        }

        for (String application : requestedApps) {
            try {
                getXWSApplicationManager().installApplicationInSpace(application, spaceName,
                    context);
            } catch (SpaceManagerException e) {
                // silently fail, but log the error
                if (LOG.isErrorEnabled())
                    LOG.error("Error while adding application" + application, e);
            }
        }
    }

    /**
     * Save the rights of a workspace to make it private : deny view right to XWiki.XWikiAllGroup
     * and XWiki.XWikiGuest
     * 
     * @throws WorkspacesManagerException
     */
    public void makeSpacePrivate(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            XWikiDocument doc =
                context.getWiki().getDocument(spaceName + ".WebPreferences", context);
            // Deny view to guest (in case global prefs are
            // accidently/maliciously modified)
            BaseObject gg = doc.getObject("XWiki.XWikiGlobalRights", "users", "XWiki.XWikiGuest");
            if (gg == null) {
                gg = doc.newObject("XWiki.XWikiGlobalRights", context);
                gg.setLargeStringValue("users", "XWiki.XWikiGuest");
            }
            gg.setStringValue("levels", "view");
            gg.setIntValue("allow", 0);
            gg.setLargeStringValue("groups", "");
            context.getWiki().saveDocument(doc, context);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * Save the rights of a workspace to make it public : deny view right to XWiki.XWikiGuest but
     * allow view right to XWiki.XWikiAllGroup so that authenticated users can access the Workspace.
     * 
     * @throws WorkspacesManagerException
     */
    public void makeSpacePublic(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            // Allow view to all group
            XWikiDocument doc =
                context.getWiki().getDocument(spaceName + ".WebPreferences", context);
            BaseObject ag =
                doc.getObject("XWiki.XWikiGlobalRights", "groups", "XWiki.XWikiAllGroup");
            if (ag == null) {
                ag = doc.newObject("XWiki.XWikiGlobalRights", context);
                ag.setLargeStringValue("groups", "XWiki.XWikiAllGroup");
            }
            ag.setStringValue("levels", "view");
            ag.setIntValue("allow", 1);
            ag.setLargeStringValue("users", "");
            // Deny view to guest (in case global prefs are
            // accidently/maliciously modified)
            BaseObject gg = doc.getObject("XWiki.XWikiGlobalRights", "users", "XWiki.XWikiGuest");
            if (gg == null) {
                gg = doc.newObject("XWiki.XWikiGlobalRights", context);
                gg.setLargeStringValue("users", "XWiki.XWikiGuest");
            }
            gg.setStringValue("levels", "view");
            gg.setIntValue("allow", 0);
            gg.setLargeStringValue("groups", "");
            context.getWiki().saveDocument(doc, context);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * Save rights of a workspace to make it open : deny view to XWiki.XWikiGuest but allow both
     * wiew and edit to XWiki.XWikiAllGroup so that authenticated users can access in view et edit
     * mode the workspace.
     * 
     * @throws WorkspacesManagerException
     */
    public void makeSpaceOpen(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            // Allow view and edit to all group
            XWikiDocument doc =
                context.getWiki().getDocument(spaceName + ".WebPreferences", context);
            BaseObject ag =
                doc.getObject("XWiki.XWikiGlobalRights", "groups", "XWiki.XWikiAllGroup");
            if (ag == null) {
                ag = doc.newObject("XWiki.XWikiGlobalRights", context);
                ag.setLargeStringValue("groups", "XWiki.XWikiAllGroup");
            }
            ag.setStringValue("levels", "view, comment, edit");
            ag.setIntValue("allow", 1);
            ag.setLargeStringValue("users", "");
            // Deny view to guest (in case global prefs are
            // accidently/maliciously modified)
            BaseObject gg = doc.getObject("XWiki.XWikiGlobalRights", "users", "XWiki.XWikiGuest");
            if (gg == null) {
                gg = doc.newObject("XWiki.XWikiGlobalRights", context);
                gg.setLargeStringValue("users", "XWiki.XWikiGuest");
            }
            gg.setStringValue("levels", "view");
            gg.setIntValue("allow", 0);
            gg.setLargeStringValue("groups", "");
            context.getWiki().saveDocument(doc, context);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

}

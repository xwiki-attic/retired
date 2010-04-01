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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.applicationmanager.ApplicationManagerPlugin;
import com.xpn.xwiki.plugin.applicationmanager.ApplicationManagerPluginApi;
import com.xpn.xwiki.plugin.applicationmanager.doc.XWikiApplication;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManager;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.spacemanager.impl.SpaceManagerExtensionImpl;

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

    protected static final String WORKSPACE_SPACE_CLASS_NAME = "XWiki.WorkspaceSpaceClass";

    protected static final String WORKSPACE_ROLE_READER_CODE = "reader";

    protected static final String WORKSPACE_ROLE_READER_LEVELS = "view, comment";

    protected static final String WORKSPACE_ROLE_READER_GROUP = "ReaderGroup";

    protected static final String WORKSPACE_ROLE_WRITER_CODE = "writer";

    protected static final String WORKSPACE_ROLE_WRITER_LEVELS = "edit, view, comment";

    protected static final String WORKSPACE_ROLE_WRITER_GROUP = "WriterGroup";

    protected static final String WORKSPACE_PREFERENCES_SHEET = "XWSCode.SpacePreferences";

    protected static final Log LOG = LogFactory.getLog(WorkspacesManagerExtension.class);

    protected static Map roles = new HashMap();

    /**
     * {@inheritDoc}
     */
    public void init(SpaceManager manager, XWikiContext context) throws SpaceManagerException
    {
        try {
            getWorkspaceSpaceClass(context);
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
            getWorkspaceSpaceClass(context);
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
     * @return the full name of the document that holds the XWiki.XWikiGroups XObjects with
     * all the workspace members
     */
    public String getMemberGroupName(String spaceName)
    {
        return spaceName + XWIKI_SPACE_SEPARATOR + "MemberGroup";
    }

    /**
     * @return the full name of the document that holds the XWiki.XWikiGroups XObjects with
     * all the workspace admins
     */
    public String getAdminGroupName(String spaceName)
    {
        return spaceName + XWIKI_SPACE_SEPARATOR + "AdminGroup";
    }

    /**
     * Returns the Workspace {@link BaseClass}, updating its definition if needed.
     * 
     * @throws XWikiException
     */
    protected BaseClass getWorkspaceSpaceClass(XWikiContext context) throws XWikiException
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
            Workspace.WORKSPACE_ACCESSLEVEL_PUBLIC + "|"
                + Workspace.WORKSPACE_ACCESSLEVEL_PRIVATE;

        needsUpdate |=
            bclass.addStaticListField(Workspace.WORKSPACE_ACCESSLEVEL, "Workspace Access Level",
                1, false, levelsFields, "select");
        needsUpdate |=
            bclass.addStaticListField(Workspace.WORKSPACE_SPACETYPE, "Workspace Space Type", 1,
                false, "workspace=Work space|userspace=User space|orgspace=Organization space",
                "select");

        if (needsUpdate)
            xwiki.saveDocument(doc, context);

        return bclass;
    }

    /**
     * {@inheritDoc}
     */
    public String getRoleGroupName(String spaceName, String roleName)
    {
        String roleDocName = (String) roles.get(roleName);
        if (roleDocName != null)
            return spaceName + XWIKI_SPACE_SEPARATOR + roleDocName;
        return null;
    }

    /**
     * Operations executed before the actual {@link Space} creation. For a {@link Workspace},
     * consists in checking if the Workspace is actually allowed to be created, so that user can
     * only create once there own Workspace, and that there is only one organization Workspace in
     * the wiki instance. 
     * 
     * {@inheritDoc}
     */
    public void preCreateSpace(String spaceName, XWikiContext context)
        throws SpaceManagerException

    {
        // Check if requested for a userspace for a user that already owns a space
        if (context.getRequest().getParameter(getWorkspaceSpaceClassName() + "_0_spacetype") != null
            && context.getRequest().getParameter(getWorkspaceSpaceClassName() + "_0_spacetype")
                .equals("userspace")
            && ((WorkspacesManager) sm).getSpaceForUser(context.getUser(), context) != null)
            throw new WorkspacesManagerException(WorkspacesManagerException.MODULE_PLUGIN_XWS,
                WorkspacesManagerException.ERROR_XWSMGR_USERSPACEALREADYEXISTS,
                "User alreadys has own workspace");
        // Check if requested for a organization space for an already existing organization
        if (context.getRequest().getParameter(getWorkspaceSpaceClassName() + "_0_spacetype") != null
            && context.getRequest().getParameter(getWorkspaceSpaceClassName() + "_0_spacetype")
                .equals("orgspace")
            && ((WorkspacesManager) sm).getOrganizationSpace(context) != null)
            throw new WorkspacesManagerException(WorkspacesManagerException.MODULE_PLUGIN_XWS,
                WorkspacesManagerException.ERROR_XWSMGR_ORGSPACEALREADYEXISTS,
                "The organization space has already been created");
        super.preCreateSpace(spaceName, context);
    }

    /**
     * Operations executed after the actual {@link Space} creation. For a workspace, consist in
     * intializing the space rights, according to its visivility, private or public. 
     * 
     * {@inheritDoc}
     */
    public void postCreateSpace(String spaceName, XWikiContext context)
        throws SpaceManagerException
    {
        try {
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
            if (accessLevel.equals(Workspace.WORKSPACE_ACCESSLEVEL_PRIVATE))
                makeSpacePrivate(spaceName, context);
            else
                makeSpacePublic(spaceName, context);
            // set content and install applications
            prepareWorkspaceContent(spaceName, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException();
        }
    }

    /**
     * Helper to check if an access level from a request correspond to an actual level of a Workspace.
     */
    private boolean isValidLevel(String level)
    {
        if (level.equals(Workspace.WORKSPACE_ACCESSLEVEL_PRIVATE)
            || level.equals(Workspace.WORKSPACE_ACCESSLEVEL_PUBLIC))
            return true;
        return false;
    }

    /**
     * Helper to obtain the space type (@see Workspace.WORKSPACE_SPACETYPE) from a workspace creation request.
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
     * Helper to obtain the space access level (@see Workspace.WORKSPACE_ACCESSLEVEL) from a workspace creation request.
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
     * Prepare a newly created Workspace by adding some document for it.
     * Iterate over an application list, and for each application to install in the workspace, copy or link
     * inside the workspace resources defined by the application object.
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

        // install default applications
        String applist = context.getWiki().Param("xwiki.workspaces.defaultapplications", "");
        String[] apps = StringUtils.split(applist, " ,");
        for (int i = 0; i < apps.length; i++) {
            try {
                addApplicationToSpace(apps[i], spaceName, context);
            } catch (SpaceManagerException e) {
                // silently fail, but log the error
                if (LOG.isErrorEnabled())
                    LOG.error("Error while adding application" + apps[i], e);
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
            // Deny view to all group
            XWikiDocument doc =
                context.getWiki().getDocument(spaceName + ".WebPreferences", context);
            BaseObject ag =
                doc.getObject("XWiki.XWikiGlobalRights", "groups", "XWiki.XWikiAllGroup");
            if (ag == null) {
                ag = doc.newObject("XWiki.XWikiGlobalRights", context);
                ag.setStringValue("groups", "XWiki.XWikiAllGroup");
            }
            ag.setStringValue("levels", "view");
            ag.setIntValue("allow", 0);
            ag.setStringValue("users", "");
            // Deny view to guest (in case global prefs are accidently/maliciously modified)
            BaseObject gg = doc.getObject("XWiki.XWikiGlobalRights", "users", "XWiki.XWikiGuest");
            if (gg == null) {
                gg = doc.newObject("XWiki.XWikiGlobalRights", context);
                gg.setStringValue("users", "XWiki.XWikiGuest");
            }
            gg.setStringValue("levels", "view");
            gg.setIntValue("allow", 0);
            gg.setStringValue("groups", "");
            context.getWiki().saveDocument(doc, context);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * Save the rights of a workspace to make it private : deny view right to XWiki.XWikiGuest
     * but allow view right to XWiki.XWikiAllGroup so that authenticated users can access the Workspace.
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
                ag.setStringValue("groups", "XWiki.XWikiAllGroup");
            }
            ag.setStringValue("levels", "view");
            ag.setIntValue("allow", 1);
            ag.setStringValue("users", "");
            // Deny view to guest (in case global prefs are accidently/maliciously modified)
            BaseObject gg = doc.getObject("XWiki.XWikiGlobalRights", "users", "XWiki.XWikiGuest");
            if (gg == null) {
                gg = doc.newObject("XWiki.XWikiGlobalRights", context);
                gg.setStringValue("users", "XWiki.XWikiGuest");
            }
            gg.setStringValue("levels", "view");
            gg.setIntValue("allow", 0);
            gg.setStringValue("groups", "");
            context.getWiki().saveDocument(doc, context);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * Install an application in the space, by copying or linking documents.
     * Read the list of documents to include (link) and copy from the {@link ApplicationManagerPlugin}
     * and save their content locally in a wiki space (web) composed of the space wiki name and the application name.
     * Also make the application web inherits its rights from the space root space (web).
     * 
     * @param appName the name of the application to install
     * @param spaceName the wiki name of the space to install the application in
     * @throws SpaceManagerException
     */
    protected void addApplicationToSpace(String appName, String spaceName, XWikiContext context)
        throws SpaceManagerException
    {
        // TODO Note that this method makes a deviant usage of the application manager plugin 
        // and XWiki application objects. It uses the application field docsToInclude and documents
        // to make a local installation (as opposed as cross-wiki global installation, which the application
        // manager is initially designed for). In the future, the application manager should be able to
        // handle local installation/local copy parameters, and the SpaceManagerPlugin implements a method to
        // install a space from an application or application list.
        
        // get the application manager api
        ApplicationManagerPluginApi appmanager =
            (ApplicationManagerPluginApi) context.getWiki().getPluginApi(
                ApplicationManagerPlugin.PLUGIN_NAME, context);
        try {
            // Retrieve the application descriptor
            XWikiApplication app = appmanager.getApplicationDocument(appName);

            if (app == null)
                throw new SpaceManagerException();

            String appSpace = spaceName + XWIKI_WORKSPACE_APPSEPARATOR + app.getAppName();

            // Retrieve the application document list
            Collection appDocs = app.getDocumentsNames(false, false);

            // Retrieve the application documents to include
            Collection docsToInclude = app.getDocsNameToInclude(true);

            for (Iterator it = appDocs.iterator(); it.hasNext();) {
                String docFullName = (String) it.next();
                // If the doc is not in the include list,
                // We copy it to the target space
                if (!docsToInclude.contains(docFullName)) {
                    String docName = docFullName.substring(docFullName.indexOf('.') + 1);
                    String targetDocName = appSpace + XWIKI_SPACE_SEPARATOR + docName;
                    context.getWiki().copyDocument(docFullName, targetDocName, true, context);
                }
            }

            for (Iterator it = docsToInclude.iterator(); it.hasNext();) {
                String docFullName = (String) it.next();
                String docName = docFullName.substring(docFullName.indexOf('.') + 1);

                // Compute the target doc name based on application name, space name and document
                // name
                // EX: Space_Wiki.WebHome for "Space" space name, "Wiki" appname and "WebHome" doc
                String targetDocName = appSpace + XWIKI_SPACE_SEPARATOR + docName;
                XWikiDocument targetDoc = context.getWiki().getDocument(targetDocName, context);

                // Link the content with the application code
                targetDoc.setContent(MessageFormat.format("#includeInContext(\"{0}\")",
                    new Object[] {docFullName}));

                // Save the document
                context.getWiki().saveDocument(targetDoc, context);
            }

            if (appDocs.size() > 0) {
                // if we've installed anything,
                // make the installed app inherit its right from the Workspace root web
                XWikiDocument appPreferences =
                    context.getWiki().getDocument(
                        appSpace + XWIKI_SPACE_SEPARATOR + "WebPreferences", context);
                BaseObject pObj =
                    appPreferences.getObject("XWiki.XWikiPreferences", true, context);
                pObj.setStringValue("parent", spaceName);
                context.getWiki().saveDocument(appPreferences, context);
            }
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }
}

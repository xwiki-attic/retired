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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Object;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.applicationmanager.ApplicationManagerPlugin;
import com.xpn.xwiki.plugin.applicationmanager.ApplicationManagerPluginApi;
import com.xpn.xwiki.plugin.applicationmanager.doc.XWikiApplication;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManagerException;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManagerExtension;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.WorkspacesActivityStream;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.builders.ActivityBuilder;

/**
 * A Manager for Workspaces Applications
 * 
 * @version $Id$
 */
public class WorkspaceApplicationManager
{
    /**
     * Log object to log messages in this class.
     */
    private static final Log LOG = LogFactory.getLog(WorkspaceApplicationManager.class);

    public static final String XWIKI_SPACE_SEPARATOR = ".";

    public static final String XWIKI_WORKSPACE_APPSEPARATOR = "_";

    private Set<String> allWorkspacesApps;

    private ApplicationManagerPluginApi xwikiApplicationManagerApi;

    /**
     * Lazily instanciate the application manager api
     * 
     * @return the {@link ApplicationManagerPluginApi} instance
     */
    private ApplicationManagerPluginApi getXWikiApplicationManagerApi(XWikiContext context)
    {
        if (xwikiApplicationManagerApi == null) {
            xwikiApplicationManagerApi =
                (ApplicationManagerPluginApi) context.getWiki().getPluginApi(
                    ApplicationManagerPlugin.PLUGIN_NAME, context);
        }
        return xwikiApplicationManagerApi;
    }

    /**
     * Returns a map of all installed applications for the given workspace name. Keys of the map are
     * the names of the installed application (as given by their application descriptor, see
     * {@link com.xpn.xwiki.plugin.applicationmanager.doc.XWikiApplicationClass#FIELD_APPNAME}.
     * Values of the map are the wiki spaces in which the application are installed.
     * 
     * @param spaceName the name of the space to retrieve applications for
     * @throws SpaceManagerException
     */
    public Map<String, String> getApplicationsForSpace(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        String hql =
            "select distinct doc.web from XWikiDocument as doc, XWikiDocument as sp"
                + ", BaseObject as xws, BaseObject as prefs, StringProperty as parent"
                + " where sp.web='" + spaceName
                + "' and sp.name='WebPreferences' and xws.name=sp.fullName"
                + " and xws.className='" + WorkspacesManagerExtension.WORKSPACE_SPACE_CLASS_NAME
                + "'"
                + " and prefs.className='XWiki.XWikiPreferences' and prefs.name=doc.fullName"
                + " and parent.id.id=prefs.id and parent.id.name='parent' and parent.value='"
                + spaceName + "'";
        try {
            List<String> applicationWebs =
                context.getWiki().getStore().search(hql, 0, 0, context);
            Map<String, String> result = new HashMap<String, String>();
            for (String appWeb : applicationWebs) {
                // Retrieve the application name based on it URL
                // Convention for an app web : Space_Spacename_Appname
                String appName =
                    appWeb
                        .substring(appWeb
                            .lastIndexOf(WorkspacesManagerExtension.XWIKI_WORKSPACE_APPSEPARATOR) + 1);
                result.put(appName, appWeb);
            }
            return result;
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    public String getApplicationURL(String spaceName, String appName, String docName,
        String queryString, XWikiContext context) throws WorkspacesManagerException
    {
        try {
            String appWeb = getApplicationsForSpace(spaceName, context).get(appName);
            if (appWeb == null || appWeb.equals(""))
                throw new WorkspacesManagerException(WorkspacesManagerException.MODULE_PLUGIN_XWS,
                    WorkspacesManagerException.ERROR_XWSMGR_APPNOTFOUNDFORSPACE,
                    "Application could not be found for space");
            return context.getWiki().getURL(
                appWeb + WorkspacesManagerExtension.XWIKI_SPACE_SEPARATOR + docName, "view",
                queryString, context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    public Set<String> getAvailableApplicationsNames(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        Set<String> availableApps = getAllWorkspacesApps(context);
        Set<String> installedApps = getApplicationsForSpace(spaceName, context).keySet();
        availableApps.removeAll(installedApps);
        return availableApps;
    }

    private Set<String> getAllWorkspacesApps(XWikiContext context)
    {
        if (allWorkspacesApps == null) {
            allWorkspacesApps = new HashSet<String>();
            try {
                List<XWikiApplication> allApps =
                    getXWikiApplicationManagerApi(context).getApplicationDocumentList();
                for (XWikiApplication app : allApps) {
                    if (app.getObject("XWiki.XWSApplicationClass") != null) {
                        allWorkspacesApps.add(app.getAppName());
                    }
                }
            } catch (XWikiException e) {
                allWorkspacesApps = null;
                return Collections.emptySet();
            }
        }
        return allWorkspacesApps;
    }

    /**
     * Remove an application from a workspace application list.
     * 
     * @param appName the application to remove from the list
     * @param spaceName the name of the workspace to remove the application from
     * @param withData delete all data belonging to the application (located in its wiki space) if
     *            true, only hides the application from the list otherwise.
     */
    public void removeApplicationFromSpace(String appName, String spaceName, boolean withData,
        XWikiContext context) throws WorkspacesManagerException
    {
        try {
            XWikiApplication app =
                getXWikiApplicationManagerApi(context).getApplicationDocument(appName);

            if (app == null)
                throw new WorkspacesManagerException(WorkspacesManagerException.MODULE_PLUGIN_XWS,
                    WorkspacesManagerException.ERROR_XWSMGR_APPNOTFOUND_ON_INSTALL,
                    "Could not find application descriptor when trying to install application ["
                        + appName + "]" + "in space [" + spaceName + "]");

            String appSpace = spaceName + XWIKI_WORKSPACE_APPSEPARATOR + app.getAppName();

            if (!withData) {
                // not implemented yet.
                throw new WorkspacesManagerException();
            } else {
                ApplicationManagerExtension ext = getApplicationManagerExtension(app, context);
                // execute, if needed pre uninstall operations
                if (ext != null) {
                    ext.preUninstall(appSpace, context);
                }
                // remove all documents that belong to the wiki space in which lives the application
                for (XWikiDocument doc : context.getWiki().getStore().searchDocuments(
                    "where doc.web = '" + appSpace + "'", context)) {
                    context.getWiki().deleteAllDocuments(doc, false, context);
                }
                // execute, if needed post uninstall operations
                if (ext != null) {
                    ext.postUninstall(appSpace, context);
                }
            }
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    private ApplicationManagerExtension getApplicationManagerExtension(XWikiApplication app,
        XWikiContext context)
    {
        Object xwsApp = app.getObject("XWiki.XWSApplicationClass");
        if (xwsApp != null) {
            String cName = (String) xwsApp.get("application_manager_extension");
            if (cName != null && !cName.equals("")) {
                try {
                    java.lang.Object c = Class.forName(cName).newInstance();
                    if (c instanceof ApplicationManagerExtension) {
                        return (ApplicationManagerExtension) c;
                    } else {
                        String msg =
                            "could not load application manager extension  "
                                + " for application [{0}], as provided class [{1}] does not"
                                + " implements ApplicationManagerExtension interface.";
                        msg =
                            MessageFormat.format(msg, new java.lang.Object[] {app.getName(),
                            c.getClass().getName()});
                        LOG.error(msg);
                    }
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        }
        return null;
    }

    /**
     * Install an application in the given space, by copying or linking documents. Read the list of
     * documents to include (link) and copy from the {@link ApplicationManagerPlugin} and save their
     * content locally in a wiki space (web) composed of the space wiki name and the application
     * name. Also make the application web inherits its rights from the space root space (web).
     * 
     * @param appName the name of the application to install
     * @param spaceName the wiki name of the space to install the application in
     * @throws SpaceManagerException
     */
    public void installApplicationInSpace(String appName, String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        // TODO Note that this method makes a deviant usage of the application
        // manager plugin and XWiki application objects.
        // It uses the application field docsToInclude and documents
        // to make a local installation (as opposed as cross-wiki global
        // installation, which the application manager is initially designed
        // for).
        // In the future, the application manager should be able to
        // handle local installation/local copy parameters, and the
        // SpaceManagerPlugin implements a method to install a space
        // from an application or application list.

        // get the application manager api

        try {
            // Retrieve the application descriptor
            XWikiApplication app =
                getXWikiApplicationManagerApi(context).getApplicationDocument(appName);

            if (app == null)
                throw new WorkspacesManagerException(WorkspacesManagerException.MODULE_PLUGIN_XWS,
                    WorkspacesManagerException.ERROR_XWSMGR_APPNOTFOUND_ON_INSTALL,
                    "Could not find application descriptor when trying to install application ["
                        + appName + "]" + "in space [" + spaceName + "]");

            ApplicationManagerExtension ext = getApplicationManagerExtension(app, context);

            String appSpace = spaceName + XWIKI_WORKSPACE_APPSEPARATOR + app.getAppName();

            // execute, if needed pre install operations
            if (ext != null) {
                ext.preInstall(appSpace, context);
            }

            // Retrieve the application document list
            Set<String> appDocs = app.getDocumentsNames(false, false);

            // Retrieve the application documents to include
            Set<String> docsToInclude = app.getDocsNameToInclude(true);

            for (String docFullName : appDocs) {
                // If the doc is not in the include list,
                // We copy it to the target space
                if (!docsToInclude.contains(docFullName)) {
                    String docName = docFullName.substring(docFullName.indexOf('.') + 1);
                    String targetDocName = appSpace + XWIKI_SPACE_SEPARATOR + docName;
                    context.getWiki().copyDocument(docFullName, targetDocName, true, context);
                }
            }

            for (String docFullName : docsToInclude) {

                String docName = docFullName.substring(docFullName.indexOf('.') + 1);

                // Compute the target doc name based on application name, space
                // name and document
                // name
                // EX: Space_Wiki.WebHome for "Space" space name, "Wiki" appname
                // and "WebHome" doc
                String targetDocName = appSpace + XWIKI_SPACE_SEPARATOR + docName;
                XWikiDocument targetDoc = context.getWiki().getDocument(targetDocName, context);

                // Link the content with the application code
                targetDoc.setContent(MessageFormat.format("#includeInContext(\"{0}\")",
                    new java.lang.Object[] {docFullName}));

                // Save the document
                context.getWiki().saveDocument(targetDoc, context);
            }

            if (appDocs.size() > 0) {
                // if we've installed anything,
                // make the installed app inherit its right from the Workspace
                // root web
                XWikiDocument appPreferences =
                    context.getWiki().getDocument(
                        appSpace + XWIKI_SPACE_SEPARATOR + "WebPreferences", context);
                BaseObject pObj =
                    appPreferences.getObject("XWiki.XWikiPreferences", true, context);
                pObj.setStringValue("parent", spaceName);
                context.getWiki().saveDocument(appPreferences, context);
            }

            // execute, if needed post install operations
            if (ext != null) {
                ext.postInstall(appSpace, context);
            }

        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }
}

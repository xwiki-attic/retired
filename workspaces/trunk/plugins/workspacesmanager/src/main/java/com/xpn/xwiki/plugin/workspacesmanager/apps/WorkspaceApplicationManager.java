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

import java.util.Map;
import java.util.Set;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManagerException;

/**
 * XWiki Workspaces application manager interface.
 * 
 * @version $Id: $
 */ 
public interface WorkspaceApplicationManager
{

    /**
     * Returns a map of all installed applications for the given workspace name. Keys of the map are
     * the names of the installed application (as given by their application descriptor, see
     * {@link com.xpn.xwiki.plugin.applicationmanager.doc.XWikiApplicationClass#FIELD_APPNAME}.
     * Values of the map are the wiki spaces in which the application are installed.
     * 
     * @param spaceName the name of the space to retrieve applications for
     * @param context the XWiki context
     * @throws WorkspacesManagerException an exception that can occur while retrieving applications
     *      for the workspace.
     * @return the list of applications already installed in the workspace.
     */
    Map<String, String> getApplicationsForSpace(String spaceName, XWikiContext context)
        throws WorkspacesManagerException;

    /**
     * Return the URL of an application document.
     * 
     * @param spaceName the name of the workspace the application belongs to
     * @param appName the name of the application
     * @param docName the name of the concerned document in the application
     * @param queryString the query string to append to the URL.
     * @param context the XWiki context
     * @return the built URL.
     * @throws WorkspacesManagerException an exception that can occur while building the URL.
     */
    String getApplicationURL(String spaceName, String appName, String docName,
        String queryString, XWikiContext context) throws WorkspacesManagerException;

    /**
     * @param spaceName the name of the space to find available applications for.
     * @param context the XWiki context.
     * @return a set of applications candidates for installation (XWiki Workspaces applications that are not
     * already installed in the workspace)
     * @throws WorkspacesManagerException an exception that can occur while retrieving available applications
     *      for the workspace.
     */
    Set<String> getAvailableApplicationsNames(String spaceName, XWikiContext context)
        throws WorkspacesManagerException;

    /**
     * Remove an application from a workspace application list.
     * 
     * @param appName the application to remove from the list
     * @param spaceName the name of the workspace to remove the application from
     * @param withData delete all data belonging to the application (located in its wiki space) if
     *            true, only hides the application from the list otherwise.
     * @param context the XWiki context
     * @throws WorkspacesManagerException an exception that can occur while removing the application 
     *      from the workspace.
     */
    void removeApplicationFromSpace(String appName, String spaceName, boolean withData,
        XWikiContext context) throws WorkspacesManagerException;

    /**
     * Install an application in the given space, by copying or linking documents. Read the list of
     * documents to include (link) and copy from the {@link ApplicationManagerPlugin} and save their
     * content locally in a wiki space (web) composed of the space wiki name and the application
     * name. Also make the application web inherits its rights from the space root space (web).
     * 
     * @param appName the name of the application to install
     * @param spaceName the wiki name of the space to install the application in
     * @param context the XWiki context
     * @throws WorkspacesManagerException an exception that can occur while installing the application 
     *      in the workspace.
     */
    void installApplicationInSpace(String appName, String spaceName, XWikiContext context)
        throws WorkspacesManagerException;

}

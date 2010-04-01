package com.xpn.xwiki.plugin.workspacesmanager.apps;

import java.util.Map;
import java.util.Set;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.plugin.applicationmanager.ApplicationManagerPlugin;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManagerException;

public interface WorkspaceApplicationManager
{

    /**
     * Returns a map of all installed applications for the given workspace name. Keys of the map are
     * the names of the installed application (as given by their application descriptor, see
     * {@link com.xpn.xwiki.plugin.applicationmanager.doc.XWikiApplicationClass#FIELD_APPNAME}.
     * Values of the map are the wiki spaces in which the application are installed.
     * 
     * @param spaceName the name of the space to retrieve applications for
     * @throws SpaceManagerException
     */
    Map<String, String> getApplicationsForSpace(String spaceName, XWikiContext context)
        throws WorkspacesManagerException;

    String getApplicationURL(String spaceName, String appName, String docName,
        String queryString, XWikiContext context) throws WorkspacesManagerException;

    Set<String> getAvailableApplicationsNames(String spaceName, XWikiContext context)
        throws WorkspacesManagerException;

    /**
     * Remove an application from a workspace application list.
     * 
     * @param appName the application to remove from the list
     * @param spaceName the name of the workspace to remove the application from
     * @param withData delete all data belonging to the application (located in its wiki space) if
     *            true, only hides the application from the list otherwise.
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
     * @throws SpaceManagerException
     */
    void installApplicationInSpace(String appName, String spaceName, XWikiContext context)
        throws WorkspacesManagerException;

}

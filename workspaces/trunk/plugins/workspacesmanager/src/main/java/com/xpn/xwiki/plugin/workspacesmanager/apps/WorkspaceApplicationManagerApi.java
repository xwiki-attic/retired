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

import java.util.Collections;
import java.util.Set;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManagerException;

/**
 * API for the XWiki Workspaces application manager.
 * 
 * @version $Id: $
 */
public class WorkspaceApplicationManagerApi extends Api
{
    /**
     * The key used in the XWiki context map to indicate there has been an error. 
     */
    private static final String CONTEXT_HASERROR = "haserror";

    /**
     * The key used in the XWiki context map to push the last error message.
     */
    private static final String CONTEXT_LASTERROR = "lasterror";
    
    /**
     * The underlaying XWiki Workspaces application manager.
     */
    private WorkspaceApplicationManager appsManager;
    
    /**
     * Constructor for the API.
     * 
     * @param context the XWiki context.
     */
    public WorkspaceApplicationManagerApi(XWikiContext context)
    {
        super(context);
        appsManager = new DefaultWorkspaceApplicationManager();
    }

    /**
     * @see WorkspacesApplicationManager#getApplicationsForSpace(String, XWikiContext)
     * 
     * @param spaceName the name of the space to retrieve applications for.
     * @return  a set of applications already intalled in the workspace
     */
    public Set<String> getApplicationsNames(String spaceName)
    {
        try {
            return appsManager.getApplicationsForSpace(spaceName, context).keySet();
        } catch (WorkspacesManagerException e) {
            context.put(CONTEXT_HASERROR, "1");
            context.put(CONTEXT_LASTERROR, e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * @param spaceName the name of the space to find available applications for.
     * @return the set of names of applications not installed yet in the workspace, and that are
     *         thus available to be installed.
     */
    public Set<String> getAvailableApplicationsNames(String spaceName)
    {
        try {
            return appsManager.getAvailableApplicationsNames(spaceName, context);
        } catch (WorkspacesManagerException e) {
            context.put(CONTEXT_HASERROR, "1");
            context.put(CONTEXT_LASTERROR, e.getMessage());
            return Collections.emptySet();
        }
    }

    public String getApplicationURL(String spaceName, String appName)
    {
        return getApplicationURL(spaceName, appName, "WebHome");
    }

    public String getApplicationURL(String spaceName, String appName, String docName)
    {
        return getApplicationURL(spaceName, appName, docName, "");
    }

    public String getApplicationURL(String spaceName, String appName, String docName,
        String queryString)
    {
        try {
            return appsManager.getApplicationURL(spaceName, appName, docName, queryString,
                context);
        } catch (WorkspacesManagerException e) {
            context.put(CONTEXT_HASERROR, "1");
            context.put(CONTEXT_LASTERROR, e.getMessage());
            return new String();
        }
    }

    public boolean installApplication(String appName, String spaceName)
    {
        if (hasProgrammingRights()) {
            try {
                appsManager.installApplicationInSpace(appName, spaceName, context);
                return true;
            } catch (WorkspacesManagerException e) {
                context.put(CONTEXT_HASERROR, "1");
                context.put(CONTEXT_LASTERROR, e.getMessage());
            }
        }
        return false;
    }

    public boolean uninstallApplication(String appName, String spaceName)
    {
        if (hasProgrammingRights()) {
            try {
                appsManager.removeApplicationFromSpace(appName, spaceName, true, context);
                return true;
            } catch (WorkspacesManagerException e) {
                context.put(CONTEXT_HASERROR, "1");
                context.put(CONTEXT_LASTERROR, e.getMessage());
            }
        }
        return false;
    }

    public boolean hasApplication(String spaceName, String appName)
    {
        try {
            return appsManager.getApplicationsForSpace(spaceName, context).containsKey(appName);
        } catch (WorkspacesManagerException e) {
            context.put(CONTEXT_HASERROR, "1");
            context.put(CONTEXT_LASTERROR, e.getMessage());
            return false;
        }
    }

}

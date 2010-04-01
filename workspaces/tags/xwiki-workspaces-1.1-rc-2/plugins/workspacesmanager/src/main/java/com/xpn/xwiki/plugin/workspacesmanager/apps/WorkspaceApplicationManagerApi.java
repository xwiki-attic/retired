package com.xpn.xwiki.plugin.workspacesmanager.apps;

import java.util.Collections;
import java.util.Set;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManagerException;

public class WorkspaceApplicationManagerApi extends Api
{

    public WorkspaceApplicationManagerApi(XWikiContext context)
    {
        super(context);
        appsManager = new WorkspaceApplicationManagerImpl();
    }

    private WorkspaceApplicationManager appsManager;

    public Set<String> getApplicationsNames(String spaceName)
    {
        try {
            return appsManager.getApplicationsForSpace(spaceName, context).keySet();
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * @return the set of names of applications not installed yet in the workspace, and that are
     *         thus available to be installed.
     */
    public Set<String> getAvailableApplicationsNames(String spaceName)
    {
        try {
            return appsManager.getAvailableApplicationsNames(spaceName, context);
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
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
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
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
                context.put("haserror", "1");
                context.put("lasterror", e.getMessage());
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
                context.put("haserror", "1");
                context.put("lasterror", e.getMessage());
            }
        }
        return false;
    }

    public boolean hasApplication(String spaceName, String appName)
    {
        try {
            return appsManager.getApplicationsForSpace(spaceName, context).containsKey(appName);
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return false;
        }
    }

}

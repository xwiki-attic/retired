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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.plugin.spacemanager.api.Space;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.spacemanager.plugin.SpaceManagerPluginApi;
import com.xpn.xwiki.plugin.workspacesmanager.apps.WorkspaceApplicationManagerApi;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.WorkspacesActivityStreamPluginApi;

/**
 * API for the workspaces manager plugin. Expose the actual methods offered for velocity scripting
 * from the Wiki.
 * 
 * @version $Id: $
 */
public class WorkspacesManagerApi extends SpaceManagerPluginApi
{

    public WorkspacesManagerApi(WorkspacesManager plugin, XWikiContext context)
    {
        super(plugin, context);
    }

    /**
     * @return the plugin implementation instance
     */
    protected WorkspacesManager getWorkspacesManager()
    {
        return (WorkspacesManager) getProtectedPlugin();
    }

    /**
     * @return an WorkspaceApplicationManagerApi instance
     */
    public WorkspaceApplicationManagerApi getApplicationsManagerApi()
    {
        return new WorkspaceApplicationManagerApi(context);
    }

    /**
     * @param howMany the number of workspaces to return
     * @return the latest created workspaces
     */
    public Collection getLastSpaces(int howMany)
    {
        return getLastSpaces(howMany, 0);
    }

    /**
     * @see #getLastSpaces(int)
     * @param startAt the offset to start retrieving the latest workspaces at
     */
    public Collection getLastSpaces(int howMany, int startAt)
    {
        try {
            return getWorkspacesManager().getLastSpaces(howMany, startAt, context);
        } catch (SpaceManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @param spaceName the workspace wiki name to retrieve the latest members for
     * @param howMany the number of members to retrieve
     * @return the latest added members of the space
     */
    public Collection getLastMembers(String spaceName, int howMany)
    {
        try {
            int startAt = 0;
            int totalMb = getWorkspacesManager().countMembers(spaceName, context);
            if (howMany < totalMb)
                startAt = totalMb - howMany;
            return getWorkspacesManager().getMembers(spaceName, howMany, startAt, context);
        } catch (SpaceManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @return the user {@link Workspace} for the context user
     */
    public Space getMySpace()
    {
        return getSpaceForUser(context.getUser());
    }

    /**
     * @param spaceName the wiki name of the workspace to count the members of
     * @return the numbers of members of the given workspace, that is the total number of
     *         XWiki.XWikiGroups objects in the space members group document of the workspace.
     */
    public int countMembers(String spaceName)
    {
        try {
            return getWorkspacesManager().countMembers(spaceName, context);
        } catch (SpaceManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return 0;
        }
    }

    /**
     * @return the list of {@link Workspace} in which the context user is a member and has a
     *         specific role (i.e. admin, reader or writer).
     */
    public Collection getMyMemberships()
    {
        try {
            // look over spaces in which the context user belongs to the member
            // group
            return getWorkspacesManager().getSpaces(context.getUser(), null, context);
        } catch (SpaceManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * @see #getPotentialMembers(String, int, int)
     */
    public Collection getPotentialMembers(String spaceName)
    {
        return getPotentialMembers(spaceName, 0, 0);
    }

    /**
     * Lookup the wiki users that are not already members of the asked workspace. This method is
     * intended to be used when offering a UI to add wiki users to the workspace.
     * 
     * @param spaceName the wiki name of the workspace to retrieve the users that does not belong to
     *            it
     * @param howMany the number of potential members to retrieve
     * @param startAt the offset to start retrieving the potential members at
     * @return the potential members for the workspace, which are the users of the current wiki that
     *         does not belong to the member group of the workspace
     */
    public Collection getPotentialMembers(String spaceName, int howMany, int startAt)
    {
        try {
            return getWorkspacesManager().getPotentialMembersForSpace(spaceName, howMany,
                startAt, context);
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return Collections.EMPTY_LIST;
        }

    }

    /**
     * @param matching the sequence of letters that either the first name or the last name of the
     *            potential members to retrieve must contains.
     * @see #getPotentialMembers(String, int, int)
     * @return the potential members for the workspace, whose first name or last name is matching
     *         the passed pattern
     */
    public Collection getPotentialMembers(String spaceName, String matching, int howMany,
        int startAt)
    {
        try {
            return getWorkspacesManager().getPotentialMembersForSpace(spaceName, matching,
                howMany, startAt, context);
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return Collections.EMPTY_LIST;
        }

    }

    /**
     * @param spaceName the wiki name of the workspace to retrieve the writers of
     * @return the list of wiki name of members of the space with the role writer
     */
    public Collection getWriters(String spaceName)
    {
        try {
            return getWorkspacesManager().getWriters(spaceName, context);
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * @param spaceName the wiki name of the workspace to retrieve the readers of
     * @return the list of wiki name of members of the space with the role reader
     */
    public Collection getReaders(String spaceName)
    {
        try {
            return getWorkspacesManager().getReaders(spaceName, context);
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * @param userName the wiki name of the user to find the userspace of
     * @return if, exists, the unique user workspace for the given user wikiname, null if it does
     *         not exists.
     */
    public Space getSpaceForUser(String userName)
    {
        try {
            return getWorkspacesManager().getSpaceForUser(userName, context);
        } catch (SpaceManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
        }
        return null;
    }

    /**
     * @param web, the wiki space (a.k.a. web) to retrieve the root space for
     * @return the top level space (or web) for the workspace's given web, corresponding to the
     *         level the passed web inherits its rights from, through the 'parent' property of its
     *         WebPreference. As an example, the web 'Space_SomeSpace_Blog' will have
     *         'Space_SomeSpace' as its workspace root space.
     */
    public Space getRootSpace(String web)
    {
        try {
            return getWorkspacesManager().getRootSpace(web, context);
        } catch (SpaceManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
        }
        return null;
    }

    /**
     * @return if exists, the workspace that the current document belongs to.
     */
    public Space getSpace()
    {
        try {
            return getWorkspacesManager().getRootSpace(context.getDoc().getSpace(), context);
        } catch (SpaceManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return null;
        }
    }

    /**
     * @return if the current document belongs to an actual workspace, false otherwise
     */
    public boolean isSpace()
    {
        return getSpace() == null ? false : true;
    }

    /**
     * Add a user to the workspace, without allowing role downgrading.
     * 
     * @see #addReader(String, String, boolean)
     */
    public void addReader(String spaceName, String userName)
    {
        this.addReader(spaceName, userName, false);
    }

    /**
     * Add a user as a reader to the space, adding to the reader role group. If not already member,
     * adds it to the member group too.
     * 
     * @param spaceName the wiki name of the workspace to add the reader to
     * @param userName the wiki name of the user to add to the workspace as a reader
     * @param allowDowngrade allow or not, to downgrade the role of the user within the workspace,
     *            if this one already is member with a higher role (writer or admin)
     */
    public void addReader(String spaceName, String userName, boolean allowDowngrade)
    {
        if (hasProgrammingRights()) {
            try {
                getWorkspacesManager().addReader(spaceName, userName, allowDowngrade, context);
            } catch (XWikiException e) {
                context.put("haserror", "1");
                context.put("lasterror", e.getMessage());
            }
        }
    }

    /**
     * Remove an admin from a workspace, removing him as a member too
     */
    public void removeAdmin(String spaceName, String userName)
    {
        if (hasProgrammingRights()) {
            try {
                getWorkspacesManager().removeAdmin(spaceName, userName, context);
            } catch (XWikiException e) {
                context.put("haserror", "1");
                context.put("lasterror", e.getMessage());
            }
        }
    }

    /**
     * Remove a reader from a workspace, removing him as a member too
     */
    public void removeReader(String spaceName, String userName)
    {
        if (hasProgrammingRights()) {
            try {
                getWorkspacesManager().removeReader(spaceName, userName, context);
            } catch (XWikiException e) {
                context.put("haserror", "1");
                context.put("lasterror", e.getMessage());
            }
        }
    }

    /**
     * Remove a writer from a workspace, removing him as a member too
     */
    public void removeWriter(String spaceName, String userName)
    {
        if (hasProgrammingRights()) {
            try {
                getWorkspacesManager().removeWriter(spaceName, userName, context);
            } catch (XWikiException e) {
                context.put("haserror", "1");
                context.put("lasterror", e.getMessage());
            }
        }
    }

    public void addWriter(String spaceName, String userName, boolean allowDowngrade)
    {
        if (hasProgrammingRights()) {
            try {
                getWorkspacesManager().addWriter(spaceName, userName, allowDowngrade, context);
            } catch (XWikiException e) {
                context.put("haserror", "1");
                context.put("lasterror", e.getMessage());
            }
        }
    }

    public void addWriter(String spaceName, String userName)
    {
        addWriter(spaceName, userName, false);
    }

    /**
     * @deprecated Use {@link WorkspaceApplicationManagerApi#hasApplication}
     */
    public boolean hasApplication(String spaceName, String appName)
    {
        return getApplicationsManagerApi().hasApplication(spaceName, appName);
    }

    /**
     * @deprecated Use {@link WorkspaceApplicationManagerApi#getApplicationURL}
     */
    public String getApplicationURL(String spaceName, String appName, String docName,
        String queryString)
    {
        return getApplicationsManagerApi().getApplicationURL(spaceName, appName, docName,
            queryString);
    }

    /**
     * @deprecated use {@link activities.WorkspacesActivityStreamPluginApi#getEventsForWorkspace}
     *             instead
     */
    public List getStories(String spaceName)
    {
        return getStories(spaceName, 10, 0);
    }

    /**
     * @deprecated use {@link activities.WorkspacesActivityStreamPluginApi#getEventsForWorkspace}
     *             instead
     */
    public List getStories(String spaceName, int howMany)
    {
        return getStories(spaceName, howMany, 0);
    }

    /**
     * @deprecated use {@link activities.WorkspacesActivityStreamPluginApi#getEventsForWorkspace}
     *             instead
     */
    public List getStories(String spaceName, int howMany, int startAt)
    {
        WorkspacesActivityStreamPluginApi wasApi =
            (WorkspacesActivityStreamPluginApi) context.getWiki().getPluginApi(
                "xwsactivitystream", context);
        return wasApi.getEventsForWorkspace(spaceName, howMany, startAt);
    }

    /**
     * @deprecated Use {@link WorkspaceApplicationManagerApi#getApplicationURL}
     */
    public String getApplicationURL(String spaceName, String appName, String docName)
    {
        return getApplicationURL(spaceName, appName, docName, "");
    }

    /**
     * @deprecated Use {@link WorkspaceApplicationManagerApi#getApplicationURL}
     */
    public String getApplicationURL(String spaceName, String appName)
    {
        return getApplicationURL(spaceName, appName, "WebHome");
    }

    public String getApplicationName()
    {
        return getApplicationName(context.getDoc().getFullName());
    }

    public String getApplicationName(String docFullname)
    {
        try {
            return getWorkspacesManager().getApplicationName(docFullname, context);
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return new String();
        }
    }

    /**
     * @deprecated Use {@link WorkspaceApplicationManagerApi#getApplicationsNames}
     */
    public Collection getApplicationsNames(String spaceName)
    {
        return getApplicationsManagerApi().getApplicationsNames(spaceName);
    }

    public List<String> getWebsForSpace(String spaceName)
    {
        try {
            return getWorkspacesManager().getWebsForSpace(spaceName, context);
        } catch (SpaceManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    public List searchDocuments(String spaceName, String applicationName)
    {
        return searchDocuments(spaceName, applicationName, 0, 0);
    }

    public List searchDocuments(String spaceName, String applicationName, int howMany)
    {
        return searchDocuments(spaceName, applicationName, howMany, 0);
    }

    public List searchDocuments(String spaceName, String applicationName, int howMany, int startAt)
    {
        try {
            return getWorkspacesManager().searchDocuments(spaceName, applicationName, howMany,
                startAt, context);
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", "1");
            return Collections.EMPTY_LIST;
        }
    }

    public Collection getPublicSpaces()
    {
        try {
            return getWorkspacesManager().getPublicSpaces(context);
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    public Collection getOpenSpaces()
    {
        try {
            return getWorkspacesManager().getOpenSpaces(context);
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    public Collection getDeletedSpaces()
    {
        try {
            return getWorkspacesManager().getDeletedSpaces(context);
        } catch (WorkspacesManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    public boolean makeSpacePublic(String spaceName)
    {
        if (this.hasProgrammingRights()) {
            try {
                getWorkspacesManager().makeSpacePublic(spaceName, context);
                return true;
            } catch (WorkspacesManagerException e) {
                context.put("haserror", "1");
                context.put("lasterror", e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean makeSpaceOpen(String spaceName)
    {
        if (hasProgrammingRights()) {
            try {
                getWorkspacesManager().makeSpaceOpen(spaceName, context);
                return true;
            } catch (WorkspacesManagerException e) {
                context.put("haserror", "1");
                context.put("lasterror", e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean makeSpacePrivate(String spaceName)
    {
        if (this.hasProgrammingRights()) {
            try {
                getWorkspacesManager().makeSpacePrivate(spaceName, context);
                return true;
            } catch (WorkspacesManagerException e) {
                context.put("haserror", "1");
                context.put("lasterror", e.getMessage());
                return false;
            }
        }
        return false;
    }

    public Space getOrganizationSpace()
    {
        try {
            return getWorkspacesManager().getOrganizationSpace(context);
        } catch (SpaceManagerException e) {
            context.put("haserror", "1");
            context.put("lasterror", e.getMessage());
            return null;
        }
    }
}

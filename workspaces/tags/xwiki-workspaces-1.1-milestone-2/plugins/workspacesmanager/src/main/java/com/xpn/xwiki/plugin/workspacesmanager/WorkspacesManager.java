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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import com.xpn.xwiki.plugin.spacemanager.api.Space;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerExtension;
import com.xpn.xwiki.plugin.spacemanager.impl.SpaceManagerImpl;
import com.xpn.xwiki.plugin.workspacesmanager.apps.WorkspaceApplicationManager;
import com.xpn.xwiki.plugin.workspacesmanager.apps.WorkspaceApplicationManagerImpl;

/**
 * Workspaces manager plugin implementation.
 * 
 * @version $Id: $
 */
public class WorkspacesManager extends SpaceManagerImpl
{
    /**
     * Plugin name
     */
    public static final String WORKSPACESMANAGER_PLUGIN_NAME = "xwsmgr";

    private WorkspaceApplicationManager workspaceAppsManager;

    public WorkspacesManager(String name, String className, XWikiContext context)
    {
        super(name, className, context);
        workspaceAppsManager = new WorkspaceApplicationManagerImpl();
    }

    public WorkspaceApplicationManager getApplicationsManager()
    {
        return workspaceAppsManager;
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return WORKSPACESMANAGER_PLUGIN_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public void virtualInit(XWikiContext context)
    {
        super.virtualInit(context);
    }

    /**
     * {@inheritDoc}
     */
    public SpaceManagerExtension getSpaceManagerExtension(XWikiContext context)
        throws SpaceManagerException
    {
        if (spaceManagerExtension == null)
            spaceManagerExtension = new WorkspacesManagerExtension();
        return spaceManagerExtension;
    }

    /**
     * First, checks if the workspace creation is allowed, by looking over the request data to
     * ensure that :
     * <ol>
     * <li>Users can only have one own workspace (a.k.a the "User space")</li>
     * <li>There can only be one "organization" workspace</li>
     * </ol>
     * {@inheritDoc}
     */
    public Space createSpaceFromRequest(String templateSpaceName, XWikiContext context)
        throws SpaceManagerException
    {
        // Check if requested for a user space for a user that already owns one
        if (context.getRequest().getParameter(getWorkspaceSpaceClassName() + "_0_spacetype") != null
            && context.getRequest().getParameter(getWorkspaceSpaceClassName() + "_0_spacetype")
                .equals("userspace") && getSpaceForUser(context.getUser(), context) != null)
            throw new WorkspacesManagerException(WorkspacesManagerException.MODULE_PLUGIN_XWS,
                WorkspacesManagerException.ERROR_XWSMGR_USERSPACEALREADYEXISTS,
                "User alreadys has own workspace");
        // Check if requested for a organization space for an already existing organization
        if (context.getRequest().getParameter(getWorkspaceSpaceClassName() + "_0_spacetype") != null
            && context.getRequest().getParameter(getWorkspaceSpaceClassName() + "_0_spacetype")
                .equals("orgspace") && getOrganizationSpace(context) != null)
            throw new WorkspacesManagerException(WorkspacesManagerException.MODULE_PLUGIN_XWS,
                WorkspacesManagerException.ERROR_XWSMGR_ORGSPACEALREADYEXISTS,
                "The organization space has already been created");
        return super.createSpaceFromRequest(templateSpaceName, context);
    }

    /**
     * Since "admin" is considered as a role within XWS, overrides the space manager addAdmin
     * method, to :
     * <ul>
     * <li>Remove the user from possible other roles (readers, writers)</li>
     * <li>Add the user to the member group if he is not a space member</li>
     * </ul>
     * 
     * @see org.xwiki.plugin.spacemanager.impl.SpaceManagerImpl#addAdmin(String, String,
     *      XWikiContext)
     */
    public void addAdmin(String spaceName, String username, XWikiContext context)
        throws SpaceManagerException
    {
        try {
            // Add the user to the space member group.
            // Will do nothing if the user is already a member of the space.
            addUserToGroup(username, getMemberGroupName(spaceName), context);
            // Remove the user from possible roles
            if (this.isMemberOfGroup(username, getReaderGroup(spaceName), context))
                removeUserFromRole(spaceName, username, getReaderRole(), context);
            if (this.isMemberOfGroup(username, getWriterGroup(spaceName), context))
                removeUserFromRole(spaceName, username, getWriterRole(), context);
            // Actually adds it as an admin
            super.addAdmin(spaceName, username, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    public void addGlobalAdmin(String username, XWikiContext context)
        throws SpaceManagerException
    {
        try {
            addUserToGroup(username, getGlobalAdminGroupName(), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    public void addPowerUser(String username, XWikiContext context) throws SpaceManagerException
    {
        try {
            addUserToGroup(username, getPowerUsersGroupName(), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }
    
    public void addInsider(String username, XWikiContext context) throws SpaceManagerException
    {
        try {
            addUserToGroup(username, getInsidersGroupName(), context);
        } catch(XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }
    
    /**
     * @see WorkspacesManagerApi#addReader(String, String, boolean)
     * @throws WorkspacesManagerException
     */
    public void addReader(String spaceName, String userName, boolean allowDowngrade,
        XWikiContext context) throws WorkspacesManagerException
    {
        try {
            if (!this.isMember(spaceName, userName, context))
                this.addMember(spaceName, userName, context);
            boolean doAdd = true;
            if (this.isAdmin(spaceName, userName, context)) {
                if (allowDowngrade)
                    removeAdmin(spaceName, userName, context);
                else
                    doAdd = false;
            }
            if (this.isMemberOfGroup(userName, getWriterGroup(spaceName), context)) {
                if (allowDowngrade)
                    removeUserFromRole(spaceName, userName, getWriterRole(), context);
                else
                    doAdd = false;
            }
            if (doAdd)
                addUserToRole(spaceName, userName, getReaderRole(), context);
            else
                throw new WorkspacesManagerException(WorkspacesManagerException.MODULE_PLUGIN_XWS,
                    WorkspacesManagerException.ERROR_XWSMGR_DOWNGRADENOTALLOWED,
                    "Downgrade of user role is not allowed");
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * Removes the user from the "XWiki.XWikiAdminGroup"
     * 
     * @param userName
     * @param context
     * @throws SpaceManagerException
     */
    public void removeGlobalAdmin(String userName, XWikiContext context)
        throws SpaceManagerException
    {
        try {
            if(getGlobalAdmins(0, 0, context).size()>1)
            {
                XWikiDocument groupDoc =
                    context.getWiki().getDocument(getGlobalAdminGroupName(), context);
                if (removeUserFromGroup(userName, groupDoc, context)) {
                    context.getWiki().saveDocument(groupDoc, context);
                }
            }
            else
            {
                SpaceManagerException ex=new SpaceManagerException();
                ex.setMessage("At least one global admin is required!");
                throw ex;
            }
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    /**
     * @param userName
     * @param context
     * @throws SpaceManagerException
     */
    public void removePowerUser(String userName, XWikiContext context)
        throws SpaceManagerException
    {
        try {
            XWikiDocument groupDoc =
                context.getWiki().getDocument(getPowerUsersGroupName(), context);
            if (removeUserFromGroup(userName, groupDoc, context)) {
                context.getWiki().saveDocument(groupDoc, context);
            }
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }
    
    /**
     * @param userName
     * @param context
     * @throws SpaceManagerException
     */
    public void moveToOutsiders(String userName,XWikiContext context) 
        throws SpaceManagerException
    {
        try {
            XWikiDocument groupDoc = 
                context.getWiki().getDocument(getInsidersGroupName(), context);
            if(removeUserFromGroup(userName, groupDoc,context)) {
                context.getWiki().saveDocument(groupDoc,context);
            }
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }
    
    /**
     * @param userName
     * @param context
     * @throws SpaceManagerException
     */
    public void moveToInsiders(String userName, XWikiContext context) throws SpaceManagerException
    {
        try {
            addInsider(userName, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    /**
     * @param userName
     * @param groupDoc
     * @param context
     * @return boolean. If true then the objects where removed from the cache. You need to save the document so that the
     *         changes take efect.
     * @throws XWikiException
     */
    private boolean removeUserFromGroup(String userName, XWikiDocument groupDoc, XWikiContext context)
        throws XWikiException
    {
        boolean needUpdate = false;
        java.util.Vector<BaseObject> groupVector = groupDoc.getObjects("XWiki.XWikiGroups");
        if (groupVector != null) {
            for (BaseObject bobj : groupVector) {
                if (bobj != null) {
                    String member = bobj.getStringValue("member");
                    if (member.equals(userName)) {
                        needUpdate = groupDoc.removeObject(bobj);
                    }
                }
            }
        }
        return needUpdate;
    }

    /**
     * @see WorkspacesManagerApi#removeAdmin(String, String)
     * @throws WorkspacesManagerException
     */
    public void removeAdmin(String spaceName, String userName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            super.removeAdmin(spaceName, userName, context);
            super.removeMember(spaceName, userName, context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#removeReader(String, String)
     * @throws WorkspacesManagerException
     */
    public void removeReader(String spaceName, String userName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            removeUserFromRole(spaceName, userName, getReaderRole(), context);
            removeMember(spaceName, userName, context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#removeWriter(String, String)
     * @throws WorkspacesManagerException
     */
    public void removeWriter(String spaceName, String userName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            removeUserFromRole(spaceName, userName, getWriterRole(), context);
            removeMember(spaceName, userName, context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * Set-up the rights objects for the writer group of a workspace
     * 
     * @param spaceName the name of the space to set the writer rights of
     * @throws WorkspacesManagerException
     */
    public void setWritersRights(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            this.addRightToGroup(spaceName, getRoleGroupName(spaceName,
                WorkspacesManagerExtension.WORKSPACE_ROLE_WRITER_CODE), "view,comment,edit",
                true, true, context);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * Set-up the rights objects for the reader group of a workspace
     * 
     * @param spaceName the name of the space to set the reader rights of
     * @throws WorkspacesManagerException
     */
    public void setReadersRights(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            this.addRightToGroup(spaceName, getRoleGroupName(spaceName,
                WorkspacesManagerExtension.WORKSPACE_ROLE_READER_CODE), "view,comment", true,
                true, context);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#addWriter(String, String, boolean)
     * @throws WorkspacesManagerException
     */
    public void addWriter(String spaceName, String userName, boolean allowDowngrade,
        XWikiContext context) throws WorkspacesManagerException
    {
        try {
            if (!this.isMember(spaceName, userName, context))
                this.addMember(spaceName, userName, context);
            boolean doAdd = true;
            if (this.isAdmin(spaceName, userName, context)) {
                if (allowDowngrade)
                    removeAdmin(spaceName, userName, context);
                else
                    doAdd = false;
            }
            if (doAdd) {
                if (this.isMemberOfGroup(userName, getReaderGroup(spaceName), context))
                    removeUserFromRole(spaceName, userName, getReaderRole(), context);
                addUserToRole(spaceName, userName, getWriterRole(), context);
            } else
                throw new WorkspacesManagerException(WorkspacesManagerException.MODULE_PLUGIN_XWS,
                    WorkspacesManagerException.ERROR_XWSMGR_DOWNGRADENOTALLOWED,
                    "Downgrade of user role not allowed");
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getReaders(String)
     * @throws WorkspacesManagerException
     */
    public Collection getReaders(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            return getUsersForRole(spaceName, getReaderRole(), context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getWriters(String)
     * @throws WorkspacesManagerException
     */
    public Collection getWriters(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            return getUsersForRole(spaceName, getWriterRole(), context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @return the code for the reader role
     */
    private String getReaderRole()
    {
        return WorkspacesManagerExtension.WORKSPACE_ROLE_READER_CODE;
    }

    /**
     * @return the code for the writer role
     */
    private String getWriterRole()
    {
        return WorkspacesManagerExtension.WORKSPACE_ROLE_WRITER_CODE;
    }

    /**
     * @return the full wiki name of the page holding the writer group
     */
    private String getWriterGroup(String spaceName)
    {
        return getSpaceManagerExtension().getRoleGroupName(spaceName,
            WorkspacesManagerExtension.WORKSPACE_ROLE_WRITER_CODE);
    }

    /**
     * @return the full wiki name of the page holding the writer group
     */
    private String getReaderGroup(String spaceName)
    {
        return getSpaceManagerExtension().getRoleGroupName(spaceName,
            WorkspacesManagerExtension.WORKSPACE_ROLE_READER_CODE);
    }

    /**
     * @return the full name of the document that holds the XWiki.XWikiGroups XObjects with all the
     *         global admins
     */
    private String getGlobalAdminGroupName()
    {
        return "XWiki" + WorkspacesManagerExtension.XWIKI_SPACE_SEPARATOR + "XWikiAdminGroup";
    }

    /**
     * @return the full name of the document that holds the XWiki.XWikiGroups XObjects with all the
     *         power users
     */
    private String getPowerUsersGroupName()
    {
        return "XWiki" + WorkspacesManagerExtension.XWIKI_SPACE_SEPARATOR
            + "XWikiPowerUsersGroup";
    }
    
    /**
     * @return the full name of the document that holds the XWiki.XWikiGroups XObjects with all the
     *         organization insiders
     */
    private String getInsidersGroupName()
    {
        return "XWiki" + WorkspacesManagerExtension.XWIKI_SPACE_SEPARATOR + "XWikiInsiderGroup";
    }

    /**
     * @return the full name of the document that holds the XWiki.XWikiGroups XObjects with all the members
     */
    private String getAllMembersGroupName()
    {
        return "XWiki" + WorkspacesManagerExtension.XWIKI_SPACE_SEPARATOR + "XWikiAllGroup";
    }

    /**
     * @see WorkspacesManagerApi#getPotentialMembers(String, int, int)
     * @throws WorkspacesManagerException
     */
    public Collection getPotentialMembersForSpace(String spaceName, int howMany, int startAt,
        XWikiContext context) throws WorkspacesManagerException
    {
        String spaceMembers =
            "select prop.value from XWikiDocument as doc, BaseObject as obj, StringProperty as prop where"
                + " doc.fullName='" + getMemberGroupName(spaceName)
                + "' and obj.name=doc.fullName and obj.className='XWiki.XWikiGroups'"
                + " and prop.id.id=obj.id and prop.id.name='member'";
        String hql =
            "select userdoc.fullName from XWikiDocument as userdoc, BaseObject as userobj where"
                + " userdoc.web='XWiki' and userobj.name=userdoc.fullName and userobj.className='XWiki.XWikiUsers'"
                + " and userdoc.fullName not in (" + spaceMembers + ") order by userdoc.name asc";
        try {
            return context.getWiki().getStore().search(hql, howMany, startAt, context);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getPotentialMembers(String, String, int, int)
     * @throws WorkspacesManagerException
     */
    public Collection getPotentialMembersForSpace(String spaceName, String matching, int howMany,
        int startAt, XWikiContext context) throws WorkspacesManagerException
    {
        String spaceMembers =
            "select prop.value from XWikiDocument as doc, BaseObject as obj, StringProperty as prop where"
                + " doc.fullName='" + getMemberGroupName(spaceName)
                + "' and obj.name=doc.fullName and obj.className='XWiki.XWikiGroups'"
                + " and prop.id.id=obj.id and prop.id.name='member'";
        String hql =
            "select userdoc.fullName from XWikiDocument as userdoc, BaseObject as userobj, StringProperty as firstname,"
                + "StringProperty as lastname where userdoc.web='XWiki' and userobj.name=userdoc.fullName and userobj.className='XWiki.XWikiUsers'"
                + " and firstname.id.id=userobj.id and firstname.id.name='first_name' and lastname.id.id=userobj.id and lastname.id.name='last_name'"
                + " and (lower(firstname.value) like '%"
                + matching.toLowerCase()
                + "%' or lower(lastname.value) like '%"
                + matching.toLowerCase()
                + "%')"
                + " and userdoc.fullName not in (" + spaceMembers + ") order by userdoc.name asc";
        try {
            return context.getWiki().getStore().search(hql, howMany, startAt, context);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getDeletedSpaces()
     * @throws WorkspacesManagerException
     */
    public List getDeletedSpaces(XWikiContext context) throws WorkspacesManagerException
    {
        String className = getSpaceClassName();
        String sql =
            "select distinct doc.web from XWikiDocument as doc, BaseObject as obj, StringProperty as typeprop"
                + " where doc.fullName=obj.name and obj.className = '"
                + className
                + "' and obj.id=typeprop.id.id and typeprop.id.name='type' and typeprop.value='deleted'";
        try {
            List spaceNames = context.getWiki().getStore().search(sql, 0, 0, context);
            return getSpaceObjects(spaceNames, context);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getOrganizationSpace()
     * @throws WorkspacesManagerException
     */
    public Space getOrganizationSpace(XWikiContext context) throws SpaceManagerException
    {
        String from = ", BaseObject as workspace, StringProperty as spacetype";
        String where =
            " and workspace.name=doc.fullName and workspace.className=" + "'"
                + getWorkspaceSpaceClassName() + "'" + " and spacetype.id.id=workspace.id"
                + " and spacetype.id.name = '" + Workspace.WORKSPACE_SPACETYPE + "'"
                + " and spacetype.id.value = 'orgspace'";
        List result = searchSpaces(from, where, 1, 0, context);
        if (result.size() == 1)
            return (Space) result.get(0);
        return null;
    }

    public List<Space> getMembershipSpaces(String userName, int nb, int startAt,
        XWikiContext context) throws WorkspacesManagerException
    {
        try {
            return this.getSpaceObjects(getMembershipSpacesNamesForUser(userName, nb, startAt,
                context), context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    public List<String> getMembershipSpacesNamesForUser(String userName, int nb, int startAt,
        XWikiContext context) throws WorkspacesManagerException
    {
        String fromSql =
            ",  XWikiDocument as groupDoc, BaseObject as groupObj, StringProperty as memberProp, BaseObject as workspace, StringProperty as spacetype";
        String whereSql =
            " and groupDoc.name='MemberGroup' and groupDoc.fullName=groupObj.name and groupObj.className = 'XWiki.XWikiGroups'"
                + " and groupObj.id=memberProp.id.id and memberProp.id.name='member' and memberProp.value='"
                + userName
                + "' and groupDoc.web=doc.web and workspace.name=doc.fullName and workspace.className="
                + "'"
                + getWorkspaceSpaceClassName()
                + "'"
                + " and spacetype.id.id=workspace.id"
                + " and spacetype.id.name = '"
                + Workspace.WORKSPACE_SPACETYPE
                + "'"
                + " and spacetype.id.value = 'workspace'";
        try {
            return searchSpaceNames(fromSql, whereSql, "", nb, startAt, context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getPublicSpaces()
     * @throws WorkspacesManagerException
     */
    public List<Space> getPublicSpaces(XWikiContext context) throws WorkspacesManagerException
    {
        try {
            return getSpaceObjects(getPublicSpacesNames(context), context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    public List<String> getPublicSpacesNames(XWikiContext context)
        throws WorkspacesManagerException
    {
        String from =
            ", BaseObject as workspace, StringProperty as visibility, StringProperty as spacetype";
        String where =
            "  and workspace.name=doc.fullName and workspace.className=" + "'"
                + getWorkspaceSpaceClassName() + "'" + " and visibility.id.id=workspace.id"
                + " and visibility.id.name = '" + Workspace.WORKSPACE_ACCESSLEVEL + "'"
                + " and visibility.value = '" + Workspace.WORKSPACE_ACCESSLEVEL_PUBLIC
                + "' and spacetype.id.id=workspace.id and spacetype.id.name = '"
                + Workspace.WORKSPACE_SPACETYPE + "'" + " and spacetype.id.value = 'workspace'";
        try {
            return searchSpaceNames(from, where, 0, 0, context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getOpenSpaces()
     * @throws WorkspacesManagerException
     */
    public List<Space> getOpenSpaces(XWikiContext context) throws WorkspacesManagerException
    {
        try {
            return getSpaceObjects(getOpenSpacesNames(context), context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    public List<String> getOpenSpacesNames(XWikiContext context)
        throws WorkspacesManagerException
    {
        String from =
            ", BaseObject as workspace, StringProperty as visibility, StringProperty as spacetype";
        String where =
            "  and workspace.name=doc.fullName and workspace.className=" + "'"
                + getWorkspaceSpaceClassName() + "'" + " and visibility.id.id=workspace.id"
                + " and visibility.id.name = '" + Workspace.WORKSPACE_ACCESSLEVEL + "'"
                + " and visibility.value = '" + Workspace.WORKSPACE_ACCESSLEVEL_OPEN
                + "' and spacetype.id.id=workspace.id" + " and spacetype.id.name = '"
                + Workspace.WORKSPACE_SPACETYPE + "'" + " and spacetype.id.value = 'workspace'";
        try {
            return searchSpaceNames(from, where, 0, 0, context);
        } catch (SpaceManagerException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#makeSpacePrivate(String)
     * @throws WorkspacesManagerException
     */
    public void makeSpacePrivate(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            Workspace sp = (Workspace) getSpace(spaceName, context);
            ((WorkspacesManagerExtension) getSpaceManagerExtension()).makeSpacePrivate(spaceName,
                context);
            sp.setAccessLevel(Workspace.WORKSPACE_ACCESSLEVEL_PRIVATE);
            sp.save();
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#makeSpacePublic(String)
     * @throws WorkspacesManagerException
     */
    public void makeSpacePublic(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            Workspace sp = (Workspace) getSpace(spaceName, context);
            ((WorkspacesManagerExtension) getSpaceManagerExtension()).makeSpacePublic(spaceName,
                context);
            sp.setAccessLevel(Workspace.WORKSPACE_ACCESSLEVEL_PUBLIC);
            sp.save();
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#makeSpaceOpen(String)
     * @throws WorkspacesManagerException
     */
    public void makeSpaceOpen(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            Workspace sp = (Workspace) getSpace(spaceName, context);
            ((WorkspacesManagerExtension) getSpaceManagerExtension()).makeSpaceOpen(spaceName,
                context);
            sp.setAccessLevel(Workspace.WORKSPACE_ACCESSLEVEL_OPEN);
            sp.save();
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getSpaceForUser(String)
     * @throws SpaceManagerException
     */
    public Space getSpaceForUser(String userName, XWikiContext context)
        throws SpaceManagerException
    {
        String from = ", BaseObject as workspace, StringProperty as spacetype";
        String where =
            " and workspace.name=doc.fullName and workspace.className=" + "'"
                + getWorkspaceSpaceClassName() + "'" + " and spacetype.id.id=workspace.id"
                + " and spacetype.id.name = '" + Workspace.WORKSPACE_SPACETYPE + "'"
                + " and spacetype.id.value = 'userspace'" + " and doc.creator='"
                + context.getUser() + "'";
        List result = super.searchSpaces(from, where, 1, 0, context);
        if (result.size() == 1)
            return (Space) result.get(0);
        return null;
    }

    /**
     * @see WorkspacesManagerApi#countMembers(String)
     * @throws SpaceManagerException
     */
    public int countMembers(String spaceName, XWikiContext context) throws SpaceManagerException
    {
        // TODO: move to spacemanager
        try {
            return getGroupService(context).countAllMembersNamesForGroup(
                getMemberGroupName(spaceName), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getMembers(String)
     * @throws SpaceManagerException
     */
    public Collection getMembers(String spaceName, int howMany, int startAt, XWikiContext context)
        throws SpaceManagerException
    {
        // TODO move to spacemanager
        try {
            return getGroupService(context).getAllMembersNamesForGroup(
                getMemberGroupName(spaceName), howMany, startAt, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getMembers(String)
     * @throws SpaceManagerException
     */
    public Collection getGlobalAdmins(int howMany, int startAt, XWikiContext context)
        throws SpaceManagerException
    {
        try {
            return getGroupService(context).getAllMembersNamesForGroup(getGlobalAdminGroupName(),
                howMany, startAt, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getMembers(String)
     * @throws SpaceManagerException
     */
    public Collection getPowerUsers(int howMany, int startAt, XWikiContext context) throws SpaceManagerException
    {
        try {
            return getGroupService(context).getAllMembersNamesForGroup(getPowerUsersGroupName(), howMany, startAt,
                context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }
    
    /**
     * @see WorkspacesManagerApi#getMembers(String)
     * @throws SpaceManagerException
     */
    public Collection getMembers(int howMany, int startAt, XWikiContext context) throws SpaceManagerException
    {
        try {
            return getGroupService(context).getAllMembersNamesForGroup(getAllMembersGroupName(), howMany, startAt,
                context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }
    
    /**
     * @see WorkspacesManagerApi#getMembers(String)
     * @throws SpaceManagerException
     */
    public Collection getInsiders(int howMany, int startAt, XWikiContext context) throws SpaceManagerException
    {
        try {
            return getGroupService(context).getAllMembersNamesForGroup(getInsidersGroupName(), howMany, startAt,
                context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }
    
    /**
     * @see WorkspacesManagerApi#getMembers(String)
     * @throws SpaceManagerException
     */
    public Collection getOutsiders(int howMany, int startAt, XWikiContext context) throws SpaceManagerException
    {
        try {
            Collection insiders=getGroupService(context).getAllMembersNamesForGroup(getInsidersGroupName(), howMany, startAt,
                context);
            Collection allMembers=getMembers(0, 0, context);
            ArrayList<String> outsiders=new ArrayList<String>(); 
            for(Object member:allMembers)
            {
                String memberName=(String)member;
                if(!insiders.contains(memberName))
                {
                    outsiders.add(memberName);
                }
            }
            return outsiders;
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    /**
     * @see WorkspacesManagerApi#getLastSpaces(int, int)
     * @throws SpaceManagerException
     */
    public List getLastSpaces(int howMany, int startAt, XWikiContext context)
        throws SpaceManagerException
    {
        // I have to select on the creation date too, otherwise the query won't work on hsqldb
        // TODO investigate if their is a cleaner way to do so.
        String sql =
            "select distinct doc.web, doc.creationDate from XWikiDocument as doc, BaseObject as obj, StringProperty as typeprop"
                + " where doc.fullName=obj.name and obj.className = '"
                + getSpaceClassName()
                + "' and obj.id=typeprop.id.id and typeprop.id.name='type' and typeprop.value='"
                + getSpaceManagerExtension().getSpaceTypeName()
                + "' order by doc.creationDate desc";
        try {
            List result = context.getWiki().search(sql, context);
            List spaces = new ArrayList();
            for (Iterator it = result.iterator(); it.hasNext();) {
                Object[] r = (Object[]) it.next();
                spaces.add(getSpace((String) r[0], context));
            }
            return spaces;
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getWorkspaceSpaceClassName()
    {
        return ((WorkspacesManagerExtension) getSpaceManagerExtension())
            .getWorkspaceSpaceClassName();
    }

    /**
     * {@inheritDoc}
     */
    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context)
    {
        return new WorkspacesManagerApi((WorkspacesManager) plugin, context);
    }

    /**
     * @return the wiki name of the space the passed web belongs to. (for example : Space_Spacename
     *         for the web Space_Spacename_Wiki.
     */
    private Space getSpaceForWeb(String web, XWikiContext context) throws SpaceManagerException
    {
        String from = "";
        String where = "and doc.web = '" + web + "'";
        List result = super.searchSpaces(from, where, 1, 0, context);
        if (result.size() == 1)
            return (Space) result.get(0);
        return null;
    }

    /**
     * @return the top-level {@Space} that the passed web belongs to.
     * @throws SpaceManagerException
     */
    public Space getRootSpace(String web, XWikiContext context) throws SpaceManagerException
    {
        // Look in the current web
        Space sp = getSpaceForWeb(web, context);
        if (sp != null)
            return sp;

        // If nothing found, search the parent web
        try {
            if (context.getWiki().exists(web + ".WebPreferences", context)) {
                XWikiDocument webPref =
                    context.getWiki().getDocument(web + ".WebPreferences", context);
                BaseObject pref = webPref.getObject("XWiki.XWikiPreferences");
                if (pref != null) {
                    String parent = pref.getStringValue("parent");
                    return getSpaceForWeb(parent, context);
                }
            }
            return null;
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    public List searchDocuments(String spaceName, String applicationName, int howMany,
        int startAt, XWikiContext context) throws WorkspacesManagerException
    {
        String query;
        if (!applicationName.equals(""))
            // For a space application, we return every document from the application web,
            // except WebHome that holds the entry-point code for the application, and
            // WebPreferences
            // that is also not supposed to be seen
            query =
                "where doc.web='" + getApplicationWeb(spaceName, applicationName)
                    + "' and doc.name<>'WebPreferences' and doc.name<>'WebHome'";
        else
            // The top web of a space will only return the WebHome for that space,
            // as others documents, such as groups or WebPreferences are not supposed to be seen
            query = "where doc.web='" + spaceName + " and doc.name='WebHome'";
        try {
            return context.getWiki().getStore().searchDocumentsNames(query, howMany, startAt,
                context);
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    public String getApplicationName(String docFullname, XWikiContext context)
        throws WorkspacesManagerException
    {
        try {
            XWikiDocument doc = context.getWiki().getDocument(docFullname, context);
            Space rootSpace = getRootSpace(doc.getSpace(), context);
            if (rootSpace == null)
                throw new WorkspacesManagerException();
            Map<String, String> apps =
                workspaceAppsManager.getApplicationsForSpace(rootSpace.getSpaceName(), context);
            if (apps.containsValue(doc.getSpace()))
                for (String appName : apps.keySet()) {
                    if (doc.getSpace().equals(apps.get(appName)))
                        return appName;
                }
            return new String();
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    public List<String> getWebsForSpace(String spaceName, XWikiContext context)
        throws WorkspacesManagerException
    {
        List<String> webs;
        String hql =
            "select distinct doc.web from XWikiDocument as doc, XWikiDocument as sp"
                + ", BaseObject as xws, BaseObject as prefs, StringProperty as parent"
                + " where sp.web='"
                + spaceName
                + "' and sp.name='WebPreferences' and xws.name=sp.fullName"
                + " and xws.className='"
                + ((WorkspacesManagerExtension) getSpaceManagerExtension())
                    .getWorkspaceSpaceClassName() + "'"
                + " and prefs.className='XWiki.XWikiPreferences' and prefs.name=doc.fullName"
                + " and parent.id.id=prefs.id and parent.id.name='parent' and parent.value='"
                + spaceName + "'";
        try {
            webs = context.getWiki().getStore().search(hql, 0, 0, context);
            webs.add(spaceName);
            return webs;
        } catch (XWikiException e) {
            throw new WorkspacesManagerException(e);
        }
    }

    private String getApplicationWeb(String spaceName, String applicationName)
    {
        // convention based
        return spaceName + "_" + applicationName;
    }

    protected Space newSpace(String spaceName, String spaceTitle, boolean create,
        XWikiContext context) throws SpaceManagerException
    {
        return new Workspace(spaceName, spaceTitle, create, this, context);
    }

}

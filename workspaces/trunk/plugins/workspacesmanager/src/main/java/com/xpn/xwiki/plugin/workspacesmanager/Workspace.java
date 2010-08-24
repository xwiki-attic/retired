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

import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.spacemanager.impl.SpaceImpl;
import com.xpn.xwiki.plugin.spacemanager.impl.SpaceManagerImpl;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Workspaces implementation.
 * 
 * @version $Id$
 */
public class Workspace extends SpaceImpl
{

    /**
     * The name of the property in XWiki.WorkspaceSpaceClass class that defines the type of a space. Possible values :
     * <ul>
     * <li>"orgspace" : The organization's space</li>
     * <li>"userspace" : A user space</li>
     * <li>"workspace" : Any ither space</li>
     * </ul>
     */
    public static final String WORKSPACE_SPACETYPE = "spacetype";

    /**
     * The name of the property in XWiki.WorkspaceSpaceClass class that defines the access level of a space.
     */
    public static final String WORKSPACE_ACCESSLEVEL = "accesslevel";

    /**
     * The name of the property in XWiki.WorkspaceSpaceClass class that defines the color of a space.
     */
    public static final String WORKSPACE_COLOR = "color";

    /**
     * The access level value key for level "protected", used for XWiki.WorkspaceSpaceClass spacetype property
     * definition.
     */
    public static final String WORKSPACE_ACCESSLEVEL_RESTRICTED = "protected";

    /**
     * The access level value key for level "public", used for XWiki.WorkspaceSpaceClass
     * {@link Workspace.WORKSPACE_ACCESSLEVEL} property definition. Public access level allows everyone
     * (XWiki.XWikiAllGroup) to read and comment content in the workspace, but not edit it.
     */
    public static final String WORKSPACE_ACCESSLEVEL_PUBLIC = "public";

    /**
     * The access level value key for level "open", used for XWiki.WorkspaceSpaceClass
     * {@link Workspace.WORKSPACE_ACCESSLEVEL} property definition. Private access level makes a workspace visible (and
     * editable) only by its members.
     */
    public static final String WORKSPACE_ACCESSLEVEL_PRIVATE = "private";

    /**
     * The access level value key for level "protected", used for XWiki.WorkspaceSpaceClass
     * {@link Workspace.WORKSPACE_ACCESSLEVEL} property definition. Open access level allows everyone
     * (XWiki.XWikiAllGroup) to read, comment and edit the content in the workspace. Only members with role
     * administrator can administrate it.
     */
    public static final String WORKSPACE_ACCESSLEVEL_OPEN = "open";

    /**
     * The space type value key for user spaces, used for XWiki.WorkspaceSpaceClass
     * {@link Workspace.WORKSPACE_SPACETYPE} property definition.
     */
    public static final String WORKSPACE_SPACETYPE_USER = "userspace";

    /**
     * The space type value key for the organization space, used for XWiki.WorkspaceSpaceClass
     * {@link Workspace.WORKSPACE_SPACETYPE} property definition.
     */
    public static final String WORKSPACE_SPACETYPE_ORG = "orgspace";

    /**
     * The space type value key for workspaces, used for XWiki.WorkspaceSpaceClass {@link Workspace.WORKSPACE_SPACETYPE}
     * property definition.
     */
    public static final String WORKSPACE_SPACETYPE_WORK = "workspace";

    /**
     * The default {@link Workspace.WORKSPACE_SPACETYPE} for a workspace.
     */
    public static final String WORKSPACE_SPACETYPE_DEFAULT = WORKSPACE_SPACETYPE_WORK;

    /**
     * The default {@link Workspace.WORKSPACE_ACCESSLEVEL} for a workspace.
     */
    public static final String WORKSPACE_DEFAULTACCESSLEVEL = WORKSPACE_ACCESSLEVEL_PRIVATE;

    /**
     * The default {@link Workspace.WORKSPACE_ACCESSLEVEL} for a user space.
     */

    public static final String WORKSPACE_DEFAULTACCESSLEVEL_USER = WORKSPACE_ACCESSLEVEL_PRIVATE;

    /**
     * Validation error key pushed in the context when the title is too short.
     */
    public static final String VALIDATION_TITLE_SHORT = "title-short";

    /**
     * Validation error key pushed in the context when the title is too short.
     */
    public static final String VALIDATION_TITLE_LONG = "title-long";

    /**
     * Validation error key pushed in the context when a title already exists.
     */
    public static final String VALIDATION_TITLE_EXISTS = "title-exists";

    /**
     * The default hexa-decimal value for a space color.
     */
    public static final String WORKSPACE_COLOR_DEFAULT = "555555";

    /**
     * Table of possible hexa-decimal values for a space colors. TODO Replace by an enumeration.
     */
    public static final String[] WORKSPACE_COLOR_VALUES =
    {WORKSPACE_COLOR_DEFAULT, "0000FF", "003399", "009900", "009933", "660033", "663300", "990000", "996699",
        "99CCFF", "99FF00", "CC3300", "CC9900", "CC9999", "CC99FF", "CCCC66", "FF99CC", "FFCC33"};
    
    /**
     * A simple quote character string.
     */
    private static final String SIMPLE_QUOTE = "'";

    /**
     * @param spaceName the name of the space to create, that is the name of the space (web) in which the WebPreferences
     *            object will hold an object of class XWiki.WorkspaceSpaceClass with space informations.
     * @param spaceTitle the pretty name of the space
     * @param create if true, actually create the space documents if they do not exist in database.
     * @param manager the {@link com.xpn.xwiki.plugin.spacemanager.api.SpaceManager} from which this space is
     *            constructed.
     * @param context the XWiki context.
     * @throws SpaceManagerException any error that can occur while creating/retriving the space.
     */
    public Workspace(String spaceName, String spaceTitle, boolean create, SpaceManagerImpl manager, 
        XWikiContext context)
        throws SpaceManagerException
    {
        super(spaceName, spaceTitle, create, manager, context);
    }

    /**
     * Fill this workspace object with data retrieve from the HTTP request.
     * 
     * @throws SpaceManagerException any error that can occur during this operation.
     */
    public void updateSpaceFromRequest() throws SpaceManagerException
    {
        super.updateSpaceFromRequest();
        try {
            XWikiDocument doc = getDoc();
            // add Workspace own object...
            doc.updateObjectFromRequest(((WorkspacesManagerExtension) manager.getSpaceManagerExtension())
                .getWorkspaceSpaceClassName(), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    /**
     * Short-cut API to set the {@link Workspace.WORKSPACE_ACCESSLEVEL} of a Workspace.
     * 
     * @param level the level to set the workspace to.
     */
    public void setAccessLevel(String level)
    {
        getDoc().setStringValue(
            ((WorkspacesManagerExtension) manager.getSpaceManagerExtension()).getWorkspaceSpaceClassName(),
            WORKSPACE_ACCESSLEVEL, level);
    }

    /**
     * @return the {@link Workspace.WORKSPACE_ACCESSLEVEL} of the Workspace.
     */
    public String getAccessLevel()
    {
        return getDoc().getStringValue(
            ((WorkspacesManagerExtension) manager.getSpaceManagerExtension()).getWorkspaceSpaceClassName(),
            WORKSPACE_ACCESSLEVEL);
    }

    /**
     * @return the {@link Workspace.WORKSPACE_COLOR} of the Workspace.
     */
    public String getColor()
    {
        String color =
            getDoc().getStringValue(
                ((WorkspacesManagerExtension) manager.getSpaceManagerExtension()).getWorkspaceSpaceClassName(),
                WORKSPACE_COLOR);
        if (color == null || color.equals("")) {
            return WORKSPACE_COLOR_DEFAULT;
        }
        return color;
    }

    /**
     * Validate the data contained in this workspace. This is usually called before actual space creation.
     * When error are found, they are pushed in the context to be retrieved and displayed along the workspace 
     * creation form from velocity.
     * Possible invalid data:
     * <ul>
     * <li>Workspace title too short</li>
     * <li>Workspace title too long</li>
     * <li>Workspace title already exists</li>
     * </ul>
     * 
     * @return true if the workspace contains only valid data, false otherwise.
     * @throws SpaceManagerException a wrapped XWiki Exception that can be raised while searching 
     * for workspace of same name.
     */
    public boolean validateSpaceData() throws SpaceManagerException
    {
        boolean success = true;
        Map<String, String> errors = new HashMap<String, String>();

        try {
            success &= doc.validate(context);

            // title
            String title = this.getDisplayTitle();
            if (title.length() < 1) {
                errors.put(Workspace.VALIDATION_TITLE_SHORT, "1");
            }
            if (title.length() > 50) {
                errors.put(Workspace.VALIDATION_TITLE_LONG, "1");
            }
                
            // same title
            List list =
                context.getWiki().getStore().searchDocumentsNames(
                    ",BaseObject as obj, StringProperty as tprop where doc.fullName=obj.name and obj.className='"
                        + manager.getSpaceClassName() + "' and obj.id=tprop.id.id and tprop.id.name='"
                        + SPACE_DISPLAYTITLE + "' and tprop.value='"
                        + this.getDisplayTitle().replaceAll(SIMPLE_QUOTE, "''") + "' and doc.space<>'" + getSpaceName()
                        + SIMPLE_QUOTE, context);

            if (list != null && list.size() > 0) {
                errors.put(Workspace.VALIDATION_TITLE_EXISTS, "1");
            }
                
            if (errors.size() > 0) {
                context.put("validation", errors);
                success &= false;
            }

        } catch (XWikiException e) {
            success &= false;
            throw new SpaceManagerException(e);
        }

        return success;
    }

}

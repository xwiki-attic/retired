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

/**
 * Workspaces implementation.
 * 
 * @version $Id$
 */
public class Workspace extends SpaceImpl
{
    public static final String WORKSPACE_SPACETYPE = "spacetype";

    public static final String WORKSPACE_ACCESSLEVEL = "accesslevel";

    public static final String WORKSPACE_COLOR = "color";

    public static final String WORKSPACE_ACCESSLEVEL_RESTRICTED = "protected";

    public static final String WORKSPACE_ACCESSLEVEL_PUBLIC = "public";

    public static final String WORKSPACE_ACCESSLEVEL_PRIVATE = "private";

    public static final String WORKSPACE_SPACETYPE_USER = "userspace";

    public static final String WORKSPACE_SPACETYPE_ORG = "orgspace";

    public static final String WORKSPACE_SPACETYPE_WORK = "workspace";

    public static final String WORKSPACE_SPACETYPE_DEFAULT = WORKSPACE_SPACETYPE_WORK;

    public static final String WORKSPACE_DEFAULTACCESSLEVEL = WORKSPACE_ACCESSLEVEL_PRIVATE;

    public static final String WORKSPACE_DEFAULTACCESSLEVEL_USER = WORKSPACE_ACCESSLEVEL_PRIVATE;

    public static final String[] WORKSPACE_COLOR_VALUES =
        {"555555", "0000FF", "003399", "009900", "009933", "660033", "663300", "990000",
        "996699", "99CCFF", "99FF00", "CC3300", "CC9900", "CC9999", "CC99FF", "CCCC66", "FF99CC",
        "FFCC33"};

    public static final String WORKSPACE_COLOR_DEFAULT = "555555";

    public Workspace(String spaceName, String spaceTitle, boolean create,
        SpaceManagerImpl manager, XWikiContext context) throws SpaceManagerException
    {
        super(spaceName, spaceTitle, create, manager, context);
    }

    public void updateSpaceFromRequest() throws SpaceManagerException
    {
        super.updateSpaceFromRequest();
        try {
            XWikiDocument doc = getDoc();
            // add Workspace own object...
            doc.updateObjectFromRequest(((WorkspacesManagerExtension) manager
                .getSpaceManagerExtension()).getWorkspaceSpaceClassName(), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    public void setAccessLevel(String level)
    {
        getDoc().setStringValue(
            ((WorkspacesManagerExtension) manager.getSpaceManagerExtension())
                .getWorkspaceSpaceClassName(), WORKSPACE_ACCESSLEVEL, level);
    }

    public String getAccessLevel()
    {
        return getDoc().getStringValue(
            ((WorkspacesManagerExtension) manager.getSpaceManagerExtension())
                .getWorkspaceSpaceClassName(), WORKSPACE_ACCESSLEVEL);
    }

    public String getColor()
    {
        String color =
            getDoc().getStringValue(
                ((WorkspacesManagerExtension) manager.getSpaceManagerExtension())
                    .getWorkspaceSpaceClassName(), WORKSPACE_COLOR);
        if (color == null || color.equals(""))
            return WORKSPACE_COLOR_DEFAULT;
        return color;
    }

}

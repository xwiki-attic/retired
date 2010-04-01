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

import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;

/**
 * Workspaces manager plugin exceptions.
 * 
 * @version $Id: $
 */
public class WorkspacesManagerException extends SpaceManagerException
{
    /**
     * Module code.
     */
    public static final int MODULE_PLUGIN_XWS = 110;

    /**
     * Throwed when a user tries to create his own user space and already owns one.
     */
    public static final int ERROR_XWSMGR_USERSPACEALREADYEXISTS = 110001;

    /**
     * Throwed when trying to create an organization space and one already exist.
     */
    public static final int ERROR_XWSMGR_ORGSPACEALREADYEXISTS = 110002;

    /**
     * Throwed when the requested application could not be found.
     */
    public static final int ERROR_XWSMGR_APPNOTFOUNDFORSPACE = 110003;

    /**
     * Throwed when trying to downgrade a user while downgrading is not allowed.
     */
    public static final int ERROR_XWSMGR_DOWNGRADENOTALLOWED = 110004;
    
    /**
     * 
     */
    public static final int ERROR_XWSMGR_APPNOTFOUND_ON_INSTALL = 110005;

    /**
     * Constructor.
     */
    public WorkspacesManagerException()
    {
    }

    public WorkspacesManagerException(int module, int code, String message)
    {
        super(module, code, message);
    }

    public WorkspacesManagerException(int module, int code, String message, Exception e)
    {
        super(module, code, message, e);
    }

    public WorkspacesManagerException(XWikiException e)
    {
        super(e);
    }

    public WorkspacesManagerException(SpaceManagerException e)
    {
        super(e);
    }

}

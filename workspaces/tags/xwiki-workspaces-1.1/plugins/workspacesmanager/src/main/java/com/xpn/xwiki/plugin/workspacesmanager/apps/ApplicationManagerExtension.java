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

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManagerException;

/**
 * An interface that allows application to execute operations before/after a installation or
 * uninstallation operation. Applications willing to benefit from this possibility should declare in
 * their workspaces application descriptor the fully qualified class name of the extension
 * implementing this interface, in the field "application_manager_extension".
 * 
 * @version $Id$
 */
public interface ApplicationManagerExtension
{
    void preInstall(String web, XWikiContext context) throws WorkspacesManagerException;

    void postInstall(String web, XWikiContext context) throws WorkspacesManagerException;

    void preUninstall(String web, XWikiContext context) throws WorkspacesManagerException;

    void postUninstall(String web, XWikiContext context) throws WorkspacesManagerException;

}

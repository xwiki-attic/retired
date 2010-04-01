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
 * @version $Id: $
 */
public interface ApplicationManagerExtension
{
    /**
     * Operations to be executed before installing the application.
     * 
     * @param web the wiki space in which the application will be installed
     * @param context the XWiki context
     * @throws WorkspacesManagerException an exception that can occur while performing 
     *      pre installation operations.
     */
    void preInstall(String web, XWikiContext context) throws WorkspacesManagerException;

    /**
     * Operations to be executed after installing the application.
     * 
     * @param web the wiki space in which the application has been installed
     * @param context the XWiki context
     * @throws WorkspacesManagerException an exception that can occur while performing 
     *      post installation operations.
     */
    void postInstall(String web, XWikiContext context) throws WorkspacesManagerException;

    /**
     * Operations to be executed before un-installing the application.
     * 
     * @param web the wiki space from which the application will be un-installed
     * @param context the XWiki context
     * @throws WorkspacesManagerException an exception that can occur while performing 
     *      pre un-installation operations.
     */
    void preUninstall(String web, XWikiContext context) throws WorkspacesManagerException;

    /**
     * Operations to be executed after un-installing the application.
     * 
     * @param web the wiki space from which the application has been un-installed
     * @param context the XWiki context
     * @throws WorkspacesManagerException an exception that can occur while performing 
     *      post un-installation operations.
     */
    void postUninstall(String web, XWikiContext context) throws WorkspacesManagerException;

}

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
package com.xpn.xwiki.workspaces.it.selenium;

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the creation, archive and deletion of workspaces
 * 
 * @version $Id$
 */
public class SpaceLifecycleTest extends AbstractWorkspacesTest
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify workspaces lifecycle");
        suite.addTestSuite(SpaceLifecycleTest.class, WorkspacesSkinExecutor.class);
        return suite;
    }

    public void testCreateSpace()
    {
        createWorkspace("My Space Name", "My Space Description", "public");
        open("/xwiki/bin/view/Space_MySpaceName/");
        this.assertTextNotPresent("The requested document could not be found.");
        this.assertTextPresent("Welcome to your new space");
    }
}

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
 * Verify the management of members in a workspace
 * 
 * @version $Id$
 */
public class WorkspacesMembersManagementTest extends AbstractWorkspacesTest
{

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify workspaces members management");
        suite.addTestSuite(WorkspacesMembersManagementTest.class, WorkspacesSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        createJohnDoeUser();
    }

    public void testAddMemberToWorkspaceGroup()
    {
        // Open the "members" tab of the organization workspace administration
        this.openOrganizationWorkspaceAdmin();
        clickLinkWithLocator("link=Space members");

        // Add John Doe to the writer group
        clickLinkWithLocator("//div[@id='space-writer-header']/a"); // TODO look for a better
        // locator
        getSelenium().waitForCondition("selenium.page().bodyText().indexOf('John Doe') != -1;",
            "2000");
        clickLinkWithLocator("//a[@id='a_XWiki.JohnDoe']/img", false);
        clickLinkWithLocator("addUsersToSpace");

        // Check John Doe is listed in the workspace writers
        // (wait for ajax to load space members, and check john doe is there)
        getSelenium().waitForCondition("selenium.page().bodyText().indexOf('John Doe') != -1;",
            "2000");
    }

}

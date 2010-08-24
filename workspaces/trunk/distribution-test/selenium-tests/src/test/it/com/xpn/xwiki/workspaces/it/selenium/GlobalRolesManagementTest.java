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
 * Verify the management of global roles (simple members, power users, administrators).
 * 
 * @version $Id$
 */

public class GlobalRolesManagementTest extends AbstractWorkspacesTest
{

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify workspaces members and groups management");
        suite.addTestSuite(GlobalRolesManagementTest.class, WorkspacesSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        this.createUsers();
    }

    /**
     * This method makes the following tests :
     * <ul>
     * <li>Opens the Main of Workspaces.</li>
     * <li>Opens the users manager.</li>
     * <li>Tests a user in all 3 categories and the buttons 'Click to save' and 'Cancel'</li>
     * </ul>
     */
    public void testManageUsersForWorkspace()
    {
        open("/xwiki/bin/view/Main/");
        clickLinkWithXPath("//a[@href='/xwiki/bin/view/XWiki/XWikiPreferences?section=Users']", true);

        // moving user - Jean Valjean
        // 1.to power users
        assertElementPresent("//tr[@id='table1_line_XWiki.JeanValjean']");
        clickLinkWithXPath("//a[@id='a11_XWiki.JeanValjean']", false);
        assertElementPresent("//tr[@id='table2_line_XWiki.JeanValjean']");
        clickLinkWithXPath("//a[text()='Click here to save your selection']");

        // 2.to global admin
        assertElementPresent("//tr[@id='table2_line_XWiki.JeanValjean']");
        clickLinkWithXPath("//a[@id='a21_XWiki.JeanValjean']", false);
        assertElementPresent("//tr[@id='table3_line_XWiki.JeanValjean']");
        clickLinkWithXPath("//a[text()='Cancel']");

        // 3.to simple users
        assertElementPresent("//tr[@id='table2_line_XWiki.JeanValjean']");
        clickLinkWithXPath("//a[@id='a22_XWiki.JeanValjean']", false);
        assertElementPresent("//tr[@id='table1_line_XWiki.JeanValjean']");
        clickLinkWithXPath("//a[text()='Click here to save your selection']");
    }

    /**
     * This method makes the following tests :
     * <ul>
     * <li>Opens the User Directory.</li>
     * <li>Fills the firstname filter with Jean and asserts that he is displayed, but Adam isn't.</li>
     * </ul>
     */
    public void testUserDirectory()
    {
        open("/xwiki/bin/view/XWS/Directory");
        getSelenium().typeKeys("firstname", "Jean");
        getSelenium().waitForCondition("selenium.page().bodyText().indexOf('Valjean') != -1;", "3000");
        assertElementPresent("//td[@class='firstname']/a[text()='Jean']");
        assertElementNotPresent("//td[@class='firstname']/a[text()='Adam']");
    }
}

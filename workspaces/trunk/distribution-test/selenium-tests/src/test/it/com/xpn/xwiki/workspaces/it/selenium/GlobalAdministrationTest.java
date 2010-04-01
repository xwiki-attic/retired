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
 * Test XWiki Workspaces global administration feature.
 * 
 * @version $Id: $
 */
public class GlobalAdministrationTest extends AbstractWorkspacesTest
{
    private final String WRENCH_ICON_LOCATOR = "//div[@id='xwikitoolbar']";

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify workspaces members and groups management");
        suite.addTestSuite(GlobalAdministrationTest.class, WorkspacesSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * Test that it is possible to access XE Administration UI using the wrench magic box, and to navigate through
     * categories.
     */
    public void testDisplayEnterpriseAdministration()
    {
        this.openOrganizationWorkspaceAdmin();
        this.getSelenium().mouseMove(this.WRENCH_ICON_LOCATOR);
        this.clickLinkWithLocator(WRENCH_ICON_LOCATOR + "/a"); // "Administration"
        this.assertTextPresent("Wiki Preferences");
        // check at least some XE links are present...
        this.assertElementPresent("//li[@class='Programming']/a");
        this.assertElementPresent("//li[@class='Import']/a");
        this.assertElementPresent("//li[@class='Export']/a");
        // click a category
        this.clickLinkWithLocator("//li[@class='Rights']/a");
        this.assertTextPresent("Displaying rows from");
        // display the categories again
        this.clickLinkWithLocator("//a[@id='showsections']", false);
        this.waitForCondition("selenium.isTextPresent('Import')");
        this.clickLinkWithLocator("//li[@class='Import']/a");
    }

}

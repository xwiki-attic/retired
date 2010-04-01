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

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;

/**
 * An abstract test case for XWiki Workspaces
 * 
 * @version $Id: $
 */
public class AbstractWorkspacesTest extends AbstractXWikiTestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
        createOrganizationWorkspace();
    }

    protected void createOrganizationWorkspace()
    {
        openGlobalAdmin();
        if (this.getSelenium().isTextPresent("Create your organization")) {
            setFieldValue("XWiki.SpaceClass_0_displayTitle", "The great organization");
            setFieldValue("XWiki.SpaceClass_0_description", "The great organization description");
            submit();
            openGlobalAdmin();
            this.assertTextNotPresent("Create your organization");
        }
    }

    public void createWorkspace(String spaceName, String spaceDescription, String accessLevel)
    {
        openGlobalAdmin("section=Spaces");
        setFieldValue("XWiki.SpaceClass_0_displayTitle", spaceName);
        setFieldValue("XWiki.SpaceClass_0_description", spaceDescription);
        setFieldValue("XWiki.WorkspaceSpaceClass_0_accesslevel", accessLevel);
        submit();
    }

    public void addUserToWorkspaceGroup(String spaceName, String firstName, String lastName,
        String group)
    {
        open("/xwiki/bin/view/Space_" + spaceName + "/WebPreferences?s=m");
        clickLinkWithLocator("//div[@id='space-" + group + "-header']/a");
        // TODO look for a better locator
        getSelenium().waitForCondition(
            "selenium.page().bodyText().indexOf('" + firstName + " " + lastName + "') != -1;",
            "2000");
        clickLinkWithLocator("//a[@id='a_XWiki." + firstName + lastName + "']/img", false);
        clickLinkWithLocator("addUsersToSpace");
        getSelenium().waitForCondition(
            "selenium.page().bodyText().indexOf('" + firstName + " " + lastName + "') != -1;",
            "2000");

    }

    public void clearWorkspaceReaders(String spaceName)
    {
        open("/xwiki/bin/delete/Space_" + spaceName + "/ReaderGroup?confirm=1");
    }

    public void clearWorkspaceWriters(String spaceName)
    {
        open("/xwiki/bin/delete/Space_" + spaceName + "/WriterGroup?confirm=1");
    }

    public void clearWorkspaceMembers(String spaceName)
    {
        open("/xwiki/bin/delete/Space_" + spaceName + "/MemberGroup?confirm=1");
    }

    public void openOrganizationWorkspaceHome()
    {
        open("/xwiki/bin/view/Space_Thegreatorganization/WebHome");
        waitPage();
    }

    public void openApplication(String applicationName)
    {
        open("/xwiki/bin/view/Space_Thegreatorganization_" + applicationName + "/");
        waitPage();
    }

    public void openOrganizationWorkspaceAdmin()
    {
        open("/xwiki/bin/view/Space_Thegreatorganization/WebPreferences");
        waitPage();
    }

    protected void openGlobalAdmin()
    {
        open("/xwiki/bin/view/XWiki/XWikiPreferences");
        waitPage();
    }

    protected void openGlobalAdmin(String parameters)
    {
        open("/xwiki/bin/view/XWiki/XWikiPreferences?" + parameters);
        waitPage();
    }
    
    /**
     * Register a JohnDoe user
     */
    protected void createJohnDoeUser()
    {
        open("/xwiki/bin/view/XWiki/JohnDoe");
        if (this.getSelenium().isTextPresent("The requested document could not be found.")) {
            open("/xwiki/bin/register/XWiki/Register");
            setFieldValue("register_first_name", "John");
            setFieldValue("register_last_name", "Doe");
            setFieldValue("xwikiname", "JohnDoe");
            setFieldValue("register_password", "pass");
            setFieldValue("register2_password", "pass");
            setFieldValue("register_email", "john@xwiki.com");
            submit();
        }
    }

    protected void loginAsJohnDoe()
    {
        if (isAuthenticated()) {
            logout();
        }
        login("JohnDoe", "pass", false);
    }

}

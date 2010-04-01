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
        if (!this.isLinkPresent("The great organization")) {
            setFieldValue("XWiki.SpaceClass_0_displayTitle", "The great organization");
            setFieldValue("XWiki.SpaceClass_0_description", "The great organization description");
            submit();
            openGlobalAdmin();
            this.assertTextPresent("The great organization");
        }
    }

    public void createWorkspace(String spaceName, String spaceDescription, boolean privateSpace)
    {
        openGlobalAdmin();
        setFieldValue("XWiki.SpaceClass_0_displayTitle", spaceName);
        setFieldValue("XWiki.SpaceClass_0_description", spaceDescription);
        setFieldValue("XWiki.WorkspaceSpaceClass_0_accesslevel", privateSpace ? "private"
            : "public");
        submit();
    }

    public void openOrganizationWorkspaceHome()
    {
        openGlobalAdmin();
        clickLinkWithText("The great organization");
    }

    public void openApplication(String applicationName)
    {
        open("/xwiki/bin/view/Space_Thegreatorganization_" + applicationName + "/");
        waitPage();
    }

    public void openOrganizationWorkspaceAdmin()
    {
        // TODO replace this with a XPATH locator on the panel "admin" link
        open("/xwiki/bin/view/Space_Thegreatorganization/WebPreferences");
        waitPage();
    }

    protected void openGlobalAdmin()
    {
        open("/xwiki/bin/view/XWSAdmin/");
        waitPage();
    }

}

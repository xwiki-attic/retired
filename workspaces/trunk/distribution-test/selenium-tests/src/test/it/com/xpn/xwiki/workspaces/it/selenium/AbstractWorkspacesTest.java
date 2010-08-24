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

import java.util.List;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;

/**
 * An abstract test case for XWiki Workspaces.
 * 
 * @version $Id$
 */
public class AbstractWorkspacesTest extends AbstractXWikiTestCase
{
    /**
     * The id and name of the input field for a workspace acess level in edit mode.
     */
    private static final String XWIKI_WORKSPACESPACECLASS_0_ACCESSLEVEL = "XWiki.WorkspaceSpaceClass_0_accesslevel";

    /**
     * The id and name of the input field for a space description in edit mode.
     */
    private static final String XWIKI_SPACECLASS_0_DESCRIPTION = "XWiki.SpaceClass_0_description";
    
    /**
     * The id and name of the input field for a space display title in edit mode.
     */
    private static final String XWIKI_SPACECLASS_0_DISPLAY_TITLE = "XWiki.SpaceClass_0_displayTitle";
    /**
     * The section parameter value for the URL of the spaces section of the global administration. 
     */
    private static final String GLOBALADMIN_SECTION_SPACES = "Spaces";

    /**
     * Applications bundled by default with XWiki Workspaces.
     */
    enum BundledApplication
    {
        /**
         * The blog application.
         */
        BLOG("Blog"), 
        
        /**
         * The wiki application.
         */
        WIKI("Wiki"), 
        
        /**
         * The photo galleries application.
         */
        PHOTOS("Photos"), 
        
        /**
         * The files application.
         */
        FILES("Files"), 
        
        /**
         * The workstream application.
         */
        STREAM("Stream");

        /**
         * The name of the application.
         * This is also the suffix that is used in the wiki space name the application
         * is installed in a workspace, as in Space_Spacename_Wiki.
         */
        private String applicationName;

        /**
         * Constructor.
         * @param name the name of the application.
         */
        BundledApplication(String name)
        {
            this.applicationName = name;
        }

        /**
         * @return the name of the application
         */
        String getName()
        {
            return this.applicationName;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
        createOrganizationWorkspace();
    }

    /**
     * Create the organization workspace.
     */
    protected void createOrganizationWorkspace()
    {
        openGlobalAdmin(GLOBALADMIN_SECTION_SPACES);
        if (this.getSelenium().isTextPresent("Your organization's workspace has not been created yet")) {
            clickLinkWithText("create it here");
            setFieldValue(XWIKI_SPACECLASS_0_DISPLAY_TITLE, "The great organization");
            setFieldValue(XWIKI_SPACECLASS_0_DESCRIPTION, "The great organization description");
            clickLinkWithXPath("//button[@type='submit']");
            openGlobalAdmin();
            this.assertTextNotPresent("Create your organization");
        }
    }

    /**
     * Creates a XWS space.
     * 
     * @param spaceName the name of the space to create.
     * @param spaceDescription the description to fill for the space.
     * @param accessLevel the initial access level for the space.
     */
    public void createWorkspace(String spaceName, String spaceDescription, String accessLevel)
    {
        openGlobalAdmin(GLOBALADMIN_SECTION_SPACES);
        setFieldValue(XWIKI_SPACECLASS_0_DISPLAY_TITLE, spaceName);
        setFieldValue(XWIKI_SPACECLASS_0_DESCRIPTION, spaceDescription);
        setFieldValue(XWIKI_WORKSPACESPACECLASS_0_ACCESSLEVEL, accessLevel);
        clickLinkWithXPath("//button[@type='submit']");
    }
    
    /**
     * Creates a XWS space.
     * 
     * @param spaceName the name of the space to create.
     * @param spaceDescription the description to fill for the space.
     * @param accessLevel the initial access level for the space.
     * @param applicationNames the name of applications to install.
     */
    protected void createWorkspace(String spaceName, String spaceDescription, String accessLevel,
        List<String> applicationNames)
    {
        openGlobalAdmin(GLOBALADMIN_SECTION_SPACES);
        setFieldValue(XWIKI_SPACECLASS_0_DISPLAY_TITLE, spaceName);
        setFieldValue(XWIKI_SPACECLASS_0_DESCRIPTION, spaceDescription);
        setFieldValue(XWIKI_WORKSPACESPACECLASS_0_ACCESSLEVEL, accessLevel);
        // First, uncheck all the applications.
        for (BundledApplication application : BundledApplication.values()) {
            clickLinkWithLocator(application.getName(), false);
        }
        // The check the requested applications.
        for (int i = 0; i < applicationNames.size(); i++) {
            clickLinkWithLocator(applicationNames.get(i), false);
        }
        clickLinkWithXPath("//button[@type='submit']");
    }

    /**
     * Add a user to a specific space group (i.e. reader, writer or admin group).
     * 
     * @param spaceName the name of the space to add the user to.
     * @param firstName the first name of the user to add.
     * @param lastName the last name of the user to add.
     * @param group the group to add the user in.
     */
    public void addUserToWorkspaceGroup(String spaceName, String firstName, String lastName, String group)
    {
        open("/xwiki/bin/view/Space_" + spaceName + "/WebPreferences?s=m");
        clickLinkWithLocator("//div[@id='space-" + group + "-header']/a");
        // TODO look for a better locator
        getSelenium().waitForCondition(
            "selenium.page().bodyText().indexOf('" + firstName + " " + lastName + "') != -1;", "2000");
        clickLinkWithLocator("//a[@id='a_XWiki." + firstName + lastName + "']/img", false);
        clickLinkWithLocator("addUsersToSpace");
        getSelenium().waitForCondition(
            "selenium.page().bodyText().indexOf('" + firstName + " " + lastName + "') != -1;", "2000");

    }

    /**
     * Remove all users in the readers group from the specified space.
     * @param spaceName the name of the space to remove readers from.
     */
    public void clearWorkspaceReaders(String spaceName)
    {
        open("/xwiki/bin/delete/Space_" + spaceName + "/ReaderGroup?confirm=1");
    }

    /**
     * Remove all users in the writers group from the specified space.
     * @param spaceName the name of the space to remove readers from.
     */
    public void clearWorkspaceWriters(String spaceName)
    {
        open("/xwiki/bin/delete/Space_" + spaceName + "/WriterGroup?confirm=1");
    }

    /**
     * Remove all members from the specified space.
     * @param spaceName the name of the space to remove readers from.
     */
    public void clearWorkspaceMembers(String spaceName)
    {
        open("/xwiki/bin/delete/Space_" + spaceName + "/MemberGroup?confirm=1");
    }

    /**
     * Open the home page from the organization space.
     */
    public void openOrganizationWorkspaceHome()
    {
        open("/xwiki/bin/view/Space_Thegreatorganization/WebHome");
        this.assertTextPresent("Welcome to your new space");
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

    /**
     * Open the global administration page directly at the specified section.
     * @param section the section.
     */
    protected void openGlobalAdmin(String section)
    {
        open("/xwiki/bin/view/XWiki/XWikiPreferences?section=" + section);
        waitPage();
    }

    /**
     * Register a JohnDoe user.
     */
    protected void createJohnDoeUser()
    {
        open("/xwiki/bin/view/XWiki/JohnDoe");
        if (this.getSelenium().isTextPresent("The requested document could not be found.")) {
            open("/xwiki/bin/view/Main/");
            this.clickLinkWithText("Manage Users");
            this.clickLinkWithText("Register a new user");
            setFieldValue("register_first_name", "John");
            setFieldValue("register_last_name", "Doe");
            setFieldValue("xwikiname", "JohnDoe");
            setFieldValue("register_password", "password");
            setFieldValue("register2_password", "password");
            setFieldValue("register_email", "john@xwiki.com");
            clickLinkWithXPath("//button[@id='registersubmit']");
        }
    }

    protected void loginAsJohnDoe()
    {
        if (isAuthenticated()) {
            logout();
        }
        login("JohnDoe", "password", false);
    }

    protected void createUsers()
    {
        open("/xwiki/bin/view/Main/CreateSomeUsers");
        getSelenium().isTextPresent("The requested document could not be found.");
        if (getSelenium().isTextPresent("The requested document could not be found.")) {

            // I open a page that doesnt exist and execute a code that will create about 20-30 members
            clickLinkWithText("edit this page");
            clickLinkWithText(BundledApplication.WIKI.getName());
            this.typeInWiki("<%def users = [\n" + "[\"Jean\", \"Valjean\"],\n" + "[\"Georges\", \"Abitbol\"],\n"
                + "[\"Adam\",\"Harper\"],\n" + "[\"Viola\",\"Mcnamee\"],\n" + "[\"Wayne\",\"Maye\"],\n"
                + "[\"Wayne\",\"Ulrich\"],\n" + "[\"Yolanda\",\"Hood\"],\n" + "[\"Clayton\",\"Elbert\"],\n"
                + "[\"Clinton\",\"Dahmen\"],\n" + "[\"Clinton\",\"Ganley\"],\n" + "[\"Clinton\",\"Mcnaught\"],\n"
                + "[\"Cody\",\"Ridout\"],\n" + "[\"Cody\",\"Vigo\"],\n" + "[\"Dale\",\"Hernandes\"],\n"
                + "[\"Dane\",\"Hasychak\"],\n" + "[\"Daniel\",\"Colunga\"],\n" + "[\"Darren\",\"Tacey\"],\n"
                + "[\"Darren\",\"Vanaman\"],\n" + "[\"Darryl\",\"Buchta\"],\n" + "[\"Debbie\",\"Schlueter\"],\n"
                + "[\"Doris\",\"Adkins\"],\n" + "[\"Eileen\",\"Bard\"],\n" + "[\"Elinor\",\"Rita\"],\n"
                + "[\"Elizabeth\",\"Hendricks\"],\n" + "]\n"
                + "def allGroup = xwiki.getDocument('XWiki.XWikiAllGroup')\n" + "for(user in users){\n"
                + "fullName = user.get(0) + user.get(1)\n" + "udoc = xwiki.getDocument(\"XWiki.\"+fullName)\n"
                + "uobj = udoc.getObject(\"XWiki.XWikiUsers\", true)\n" + "uobj.set(\"first_name\", user.get(0))\n"
                + "uobj.set(\"last_name\" , user.get(1))\n" + "print(\"* Registering *\" + udoc.fullName + \"*\")\n"
                + "udoc.save()\n" + "gObj = allGroup.newObject('XWiki.XWikiGroups')\n"
                + "gObj.set('member',udoc.fullName)\n" + "}\n" + "allGroup.save()\n"
                + "doc.setContent(\"the users are already registered\");\n" + "doc.save();%>");
            clickEditSaveAndView();
        }
    }

    public void installApplication(String spaceName, String applicationName)
    {
        spaceName = spaceName.replaceAll(" ", "");

        open("/xwiki/bin/view/Main/");
        clickLinkWithText("All My Spaces");
        assertTextPresent(spaceName);
        clickLinkWithText(spaceName);
        clickLinkWithText("Manage applications");
        if (!this.isElementPresent("//a[@href='/xwiki/bin/view/Space_" + spaceName
            + "/WebPreferences?s=a&action=uninstall&app=" + applicationName + "']")) {
            clickLinkWithLocator("//a[@href='/xwiki/bin/view/Space_" + spaceName
                + "/WebPreferences?s=a&action=install&app=" + applicationName + "']");
            clickLinkWithText("Back to the workspace");
        }
    }

    public void uninstallApplication(String spaceName, String applicationName)
    {
        spaceName = spaceName.replaceAll(" ", "");

        open("/xwiki/bin/view/Main/");
        clickLinkWithText("All My Spaces");
        assertTextPresent(spaceName);
        clickLinkWithText(spaceName, true);
        clickLinkWithText("Manage applications");
        if (!this.isElementPresent("//a[@href='/xwiki/bin/view/Space_" + spaceName
            + "/WebPreferences?s=a&action=install&app=" + applicationName + "']")) {
            clickLinkWithLocator("//a[@href='/xwiki/bin/view/Space_" + spaceName
                + "/WebPreferences?s=a&action=uninstall&app=" + applicationName + "']");
            clickLinkWithText("Confirm");
            clickLinkWithText("Back to the workspace");
        }
    }
}

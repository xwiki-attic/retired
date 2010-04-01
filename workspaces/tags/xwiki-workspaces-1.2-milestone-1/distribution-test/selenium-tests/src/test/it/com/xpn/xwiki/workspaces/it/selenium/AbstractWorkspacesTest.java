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
        openGlobalAdmin("section=Spaces");
        if (this.getSelenium().isTextPresent("Your organization's workspace has not been created yet")) {
	    clickLinkWithText("create it here");
            setFieldValue("XWiki.SpaceClass_0_displayTitle", "The great organization");
            setFieldValue("XWiki.SpaceClass_0_description", "The great organization description");
            clickLinkWithXPath("//button[@type='submit']");
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
        clickLinkWithXPath("//button[@type='submit']");
    }

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
        open("/xwiki/bin/view/Main/apagethatIhopedoesntexist");
        getSelenium().isTextPresent("The requested document could not be found.");
        if (getSelenium().isTextPresent("The requested document could not be found.")) {

            // I open a page that doesnt exist and execute a code that will create about 20-30 members
            clickLinkWithText("edit this page");
            clickLinkWithText("Wiki");
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
                + "gObj.set('member',udoc.fullName)\n" + "}\n" + "allGroup.save()"
                + "doc.setContent(\"the users are already registered\");" + "doc.save();%>");
            clickEditSaveAndView();
        }
    }

    protected void createWorkspace(String spaceName, String spaceDescription, String accessLevel,
        List<String> applicationNames)
    {
        openGlobalAdmin("section=Spaces");
        setFieldValue("XWiki.SpaceClass_0_displayTitle", spaceName);
        setFieldValue("XWiki.SpaceClass_0_description", spaceDescription);
        setFieldValue("XWiki.WorkspaceSpaceClass_0_accesslevel", accessLevel);
        for (int i = 0; i < applicationNames.size(); i++) {
            clickLinkWithLocator(applicationNames.get(i), false);
        }
        clickLinkWithXPath("//button[@type='submit']");
    }

    public void installApplication(String spaceName, String applicationName)
    {
        //eliminating any space from the space name
        String spaceN="";
        for(int i=0;i<spaceName.length();i++)
        {
            if(spaceName.charAt(i)>=65 && spaceName.charAt(i)<=90)
                spaceN+=spaceName.charAt(i);
            if(spaceName.charAt(i)>=97 && spaceName.charAt(i)<=122)
                spaceN+=spaceName.charAt(i);
        }
        
        open("/xwiki/bin/view/Main/");
        clickLinkWithText("All My Spaces");
        assertTextPresent(spaceName);
        clickLinkWithText(spaceName);
        clickLinkWithText("Manage applications");
        if (!this
            .isElementPresent("//a[@href='/xwiki/bin/view/Space_"+spaceN+"/WebPreferences?s=a&action=uninstall&app="+applicationName+"']")) {
            clickLinkWithLocator("//a[@href='/xwiki/bin/view/Space_"+spaceN+"/WebPreferences?s=a&action=install&app="+applicationName+"']");
            clickLinkWithText("Back to the workspace");
        }
    }
    
    public void uninstallApplication(String spaceName, String applicationName)
    {
        //eliminating any space from the space name
        String spaceN="";
        for(int i=0;i<spaceName.length();i++)
        {
            if(spaceName.charAt(i)>=65 && spaceName.charAt(i)<=90)
                spaceN+=spaceName.charAt(i);
            if(spaceName.charAt(i)>=97 && spaceName.charAt(i)<=122)
                spaceN+=spaceName.charAt(i);
        }
        
        open("/xwiki/bin/view/Main/");
        clickLinkWithText("All My Spaces");
        assertTextPresent(spaceName);
        clickLinkWithText(spaceName,true);
        clickLinkWithText("Manage applications");
        if (!this
            .isElementPresent("//a[@href='/xwiki/bin/view/Space_"+spaceN+"/WebPreferences?s=a&action=install&app="+applicationName+"']")) {
            clickLinkWithLocator("//a[@href='/xwiki/bin/view/Space_"+spaceN+"/WebPreferences?s=a&action=uninstall&app="+applicationName+"']");
            clickLinkWithText("Confirm");
            clickLinkWithText("Back to the workspace");
        }
    }
}

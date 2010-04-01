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

import java.util.ArrayList;

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the management of applications in a workspace
 * 
 * @version $Id: $
 */
public class SpaceApplicationsTest extends AbstractWorkspacesTest
{

    /**
     * The name of the space use along this space
     */
    private final String SPACE_NAME = "TestApplications";

    private final String WAIT_LIGHTBOX_TIME = "3000";

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify workspaces members management");
        suite.addTestSuite(SpaceApplicationsTest.class, WorkspacesSkinExecutor.class);
        return suite;
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        open("/xwiki/bin/view/Main/");
        clickLinkWithText("All My Spaces");
        if (!getSelenium().isTextPresent(SPACE_NAME)) {
            // Create a new application that doesn't have installed any application
            this.createWorkspace(SPACE_NAME, "Test Workspaces Applications", "public", new ArrayList<String>());
        }
    }

    /**
     * This method makes the following tests :
     * <ul>
     * <li>Opens the "The great organization space".</li>
     * <li>Opens the Applications Manager.</li>
     * <li>Installs the Blog application.</li>
     * <li>Goes back to the workspaces.</li>
     * <li>Creates a new blog page.</li>
     * <li>Tests Edit/Delete(recovery/purge) page.</li>
     * <li>Goes to Dashboard and check if the created page appear there as recent activity.</li>
     * <li>UnInstalls the Blog application.</li>
     * </ul>
     */
    public void testBlogApplication()
    {
        this.installApplication(SPACE_NAME, "Blog");
        clickLinkWithXPath("//a[text()='Blog']");
        clickLinkWithText("Write a blog post +", false);
        this.getSelenium().waitForCondition("selenium.page().bodyText().indexOf('Add a blog article') != -1;",
            WAIT_LIGHTBOX_TIME);
        getSelenium().type("title", "About me");
        clickLinkWithLocator("submit", true);
        typeInWysiwyg("this is a wonderful wonderful day selenium test.");
        clickLinkWithLocator("formactionsave");

        // Check if the created post appears on the Dashboard
        clickLinkWithText("My Dashboard");
        assertTextPresent("About me");
        clickLinkWithText(SPACE_NAME);
        clickLinkWithText("Blog");
        assertTextPresent("About me");
        clickLinkWithText("About me");
        clickLinkWithText("Edit article", true);
        typeInWysiwyg("this is a second wonderful wonderful for selenium test.");
        clickLinkWithLocator("formactionsave");
        clickLinkWithText("Delete article");
        clickLinkWithXPath("//input[@value='yes']");
        clickLinkWithText("Home");
        assertTextPresent("About me");

        this.uninstallApplication(SPACE_NAME, "Blog");

    }

    /**
     * Test the Photo application in a workspace :
     * <ul>
     * <li>Install the photo application</li>
     * <li>Create a photo gallery</li>
     * <li>Check the corresponding activity event appears on the space dashboard</li>
     * <li>Edit the photo gallery</li>
     * <li>Check the corresponding activity event appears on the space dashboard</li>
     * <li>Delete the photo gallery</li>
     * <li>Check the corresponding activity event appears on the space dashboard</li>
     * <li>Check all activity events do no have clickable links to the deleted gallery</li>
     * <li>Uninstall the application</li>
     * </ul>
     */
    public void testPhotosApplication()
    {
        // 1 create gallery
        this.installApplication(SPACE_NAME, "Photos");
        this.clickLinkWithText(SPACE_NAME);
        assertElementPresent("//a[text()='Photos']");
        this.clickLinkWithText("Create a photo gallery +", false);
        this.getSelenium().waitForCondition("selenium.page().bodyText().indexOf('Create a new gallery') != -1;",
            WAIT_LIGHTBOX_TIME);
        this.getSelenium().typeKeys("title", "test gallery");
        clickLinkWithLocator("submit", true);
        this.clickEditCancelEdition(); // When creating a gallery, the page is already saved once when we arrive in the
                                        // "inline edit" mode.
        // (This to know where to upload images to.) Thus, no need to save here.
        this.clickLinkWithText(SPACE_NAME);
        this.assertTextPresent("Administrator created the photo gallery test gallery");

        // 2 edit gallery
        this.clickLinkWithText("test gallery");
        this.clickLinkWithText("Edit gallery");
        this.clickEditSaveAndView();
        this.clickLinkWithText(SPACE_NAME);
        this.assertTextPresent("Administrator updated the photo gallery test gallery");

        // 3 delete gallery
        this.clickLinkWithText("test gallery");
        this.clickLinkWithText("Delete");
        this.clickLinkWithText("Confirm deletion");
        this.clickLinkWithText(SPACE_NAME);
        this.assertTextPresent("Administrator deleted the photo gallery test gallery");
        String galleryLocator = "//a[@href='/xwiki/bin/view/Space_" + SPACE_NAME + "_Photos/testgallery']";
        this.assertElementNotPresent(galleryLocator); // check there is no link to the deleted gallery

        // clean
        this.uninstallApplication(SPACE_NAME, "Photos");
    }

    /**
     * This method makes the following tests :
     * <ul>
     * <li>Opens the "The great organization space".</li>
     * <li>Opens the Applications Manager.</li>
     * <li>Installs the WorkStream application.</li>
     * <li>Create 2 posts: one from the form inside Workstream page. The other one from Create new post + link.</li>
     * <li>Lists all my posts.</li>
     * <li>UnInstalls the WorkStream application.</li>
     * </ul>
     */
    public void testWorkstreamApplication()
    {
        this.installApplication(SPACE_NAME, "Stream");
        assertElementPresent("//a[text()='Workstream']");
        clickLinkWithText("Workstream");
        getSelenium().type("status-box", "this is a text for this test");
        clickLinkWithXPath("//input[@value='Update']", true);
        getSelenium().click("link=Add an entry +");
        getSelenium().type("status-box", "ok...I do understand");
        clickLinkWithLocator("story", false);
        submit();
        // List Administrator posts
        clickLinkWithXPath("//span[@class='userpicture']/a[@href='/xwiki/bin/view/Space_" + SPACE_NAME
            + "_Stream/?uid=XWiki.Admin']");

        this.uninstallApplication(SPACE_NAME, "Stream");
    }

    /**
     * This method makes the following tests :
     * <ul>
     * <li>Opens the "The great organization space".</li>
     * <li>Opens the Applications Manager.</li>
     * <li>Installs the Wiki application.</li>
     * <li>Create a page. Edit,History,Versions,Delete(recovery,purge) on the page.</li>
     * <li>Goes to Dashboard and checks for the page at recent activity.</li>
     * <li>UnInstalls the Wiki application.</li>
     * </ul>
     */
    public void testWikiApplication()
    {
        this.installApplication(SPACE_NAME, "Wiki");
        assertElementPresent("//a[text()='Wiki']");
        clickLinkWithXPath("//a[@href='/xwiki/bin/view/Space_" + SPACE_NAME + "_Wiki/']");
        assertTextPresent("WelcomeOnThisWiki");

        // Creating the page
        getSelenium().click("link=Add a wiki page +");
        this.getSelenium().waitForCondition("selenium.page().bodyText().indexOf('Create a wiki page') != -1;",
            WAIT_LIGHTBOX_TIME);
        getSelenium().type("title", "My First Wiki Page");
        clickLinkWithLocator("submit", true);
        typeInWysiwyg("some text");
        clickLinkWithLocator("formactionsave");

        // Check if the created page appears on the Dashboard
        clickLinkWithText("My Dashboard");
        assertTextPresent("My First Wiki Page");
        clickLinkWithText(SPACE_NAME);
        clickLinkWithXPath("//a[@href='/xwiki/bin/view/Space_" + SPACE_NAME + "_Wiki/']");
        assertTextPresent("My First Wiki Page");

        // Editing the page
        clickLinkWithText("My First Wiki Page");
        clickLinkWithText("Edit");
        typeInWysiwyg("some text ...more");
        clickLinkWithLocator("formactionsave");

        // Editing the page - minor edit
        clickLinkWithText("My First Wiki Page");
        clickLinkWithText("Edit");
        clickLinkWithText("Wiki");
        typeInWiki("some text ...more");
        clickLinkWithLocator("minorEdit", false);
        clickLinkWithLocator("formactionsave");

        // Working with history
        clickLinkWithText("History", true);
        clickLinkWithLocator("rev1", false);
        clickLinkWithXPath("//input[@name='rev2' and @value='1.1']", false);
        clickLinkWithXPath("//input[@value='Compare selected Versions']");
        assertTextPresent("some");
        clickLinkWithText("History");
        clickLinkWithLocator("viewminorversions");
        clickLinkWithLocator("hideminorversions");
        clickLinkWithLocator("rev1", false);
        clickLinkWithLocator("rev2", false);
        clickLinkWithLocator("deleteversions");
        clickLinkWithXPath("//input[@value='yes']", true);

        // Delete the page for restore
        clickLinkWithText("Delete", true);
        clickLinkWithXPath("//input[@value='yes']", true);
        clickLinkWithXPath("//a[@href='/xwiki/bin/view/Space_" + SPACE_NAME + "_Wiki/']");
        assertTextNotPresent("My First Wiki Page");
        open("/xwiki/bin/view/Space_" + SPACE_NAME + "_Wiki/MyFirstWikiPage");
        clickLinkWithText("Restore");
        clickLinkWithXPath("//a[@href='/xwiki/bin/view/Space_" + SPACE_NAME + "_Wiki/']");
        assertTextPresent("My First Wiki Page");

        // Delete the page forever
        clickLinkWithText("My First Wiki Page");
        clickLinkWithText("Delete", true);
        clickLinkWithXPath("//input[@value='yes']", true);
        open("/xwiki/bin/view/Space_" + SPACE_NAME + "_Wiki/MyFirstWikiPage");
        clickLinkWithXPath("//a[@onclick=\"if (confirm('This action is not reversible. Are you sure you wish to continue?')) {this.href += '&confirm=1'; return true;} return false;\"]");
        assertTrue(getSelenium().getConfirmation().matches(
            "^This action is not reversible\\. Are you sure you wish to continue[\\s\\S]$"));

        this.uninstallApplication(SPACE_NAME, "Wiki");

    }

}

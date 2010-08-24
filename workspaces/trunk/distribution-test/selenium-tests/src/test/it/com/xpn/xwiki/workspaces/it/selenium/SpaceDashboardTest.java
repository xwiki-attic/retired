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
 * Verify the activity dashboard of XWS Spaces.
 * 
 * @version $Id$
 */
public class SpaceDashboardTest extends AbstractWorkspacesTest
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify workspaces activity stream");
        suite.addTestSuite(SpaceDashboardTest.class, WorkspacesSkinExecutor.class);
        return suite;
    }

    /**
     * Test the activity stream is installed and works as expected.
     */
    public void testActivityStream()
    {
        open("xwiki/bin/edit/Test/ActivityStream?&editor=wiki");
        waitPage();
        StringBuilder sb = new StringBuilder();
        sb.append("#set($as = $xwiki.xwsactivitystream)\n");
        sb.append("#set ($void = $as.addActivityEvent('testStream','type','a test event'))\n");
        sb.append("#foreach($ev in $as.getEvents('testStream',false, 1, 0))\n");
        sb.append("$ev.displayBody\n");
        sb.append("#end\n");
        setFieldValue("content", sb.toString());
        clickEditSaveAndView();
        assertTextPresent("a test event");
    }

    /**
     * Verify activity events created on the dashboard by the wiki application.
     */
    public void testWikiApplicationEvents()
    {
        // 1- test wiki page creation

        // create and edit a new wiki page
        openOrganizationWorkspaceHome();
        clickLinkWithLocator("link=Add a wiki page +", false);
        this.getSelenium().waitForCondition("selenium.page().bodyText().indexOf('Create a wiki page') != -1;", "2000");
        setFieldValue("title", "A test page");
        submit();
        typeInWysiwyg("Some content");
        clickEditSaveAndView();
        assertTextPresent("Some content");

        // check the event is reported on the workspace home
        clickLinkWithText("The great organization");
        assertTextPresent("created the wiki page A test page");

        // 2- test wiki page edition

        // edit an existing wiki page
        openApplication("Wiki");
        clickLinkWithLocator("link=WelcomeOnThisWiki");
        clickLinkWithLocator("link=Edit");
        typeInWysiwyg("Some other content");
        clickEditSaveAndView();
        assertTextPresent("Some other content");

        // check the event on the workspace home
        clickLinkWithText("The great organization");
        assertTextPresent("modified the wiki page WelcomeOnThisWiki");

        // 3- test wiki page deletion

        // delete an existing wiki page
        openApplication("Wiki");
        clickLinkWithLocator("link=WelcomeOnThisWiki");
        clickLinkWithLocator("link=Delete");
        clickLinkWithLocator("//input[@value='yes']");

        // check the event on the workpsace home
        clickLinkWithLocator("link=Home");
        assertTextPresent("deleted the wiki page WelcomeOnThisWiki");
    }

    /**
     * Verify activity events created on the dashboard by the blog application.
     */
    public void testBlogApplicationEvents()
    {
        // 1- create a new blog post
        openOrganizationWorkspaceHome();
        clickLinkWithLocator("link=Write a blog post +", false);
        this.getSelenium().waitForCondition("selenium.page().bodyText().indexOf('Add a blog article') != -1;", "2000");
        setFieldValue("title", "A test blog post");
        submit();
        setFieldValue("XWiki.ArticleClass_0_content", "Hello, world");
        clickEditSaveAndView();
        assertTextPresent("A test blog post");

        clickLinkWithText("The great organization");
        assertTextPresent("published the blog post A test blog post");

        // 2- edit a blog post

        open("/xwiki/bin/inline/Space_Thegreatorganization_Blog/Atestblogpost");
        waitPage();
        typeInWysiwyg("New content");
        clickEditSaveAndView();
        assertTextPresent("New content");

        // check the event on the workspace home
        clickLinkWithText("The great organization");
        assertTextPresent("modified the blog post A test blog post");

        // 3- delete a blog post
        open("/xwiki/bin/view/Space_Thegreatorganization_Blog/Atestblogpost");
        waitPage();
        clickLinkWithLocator("link=Delete");
        clickLinkWithLocator("//input[@value='yes']");
        clickLinkWithLocator("link=Home");
        assertTextPresent("deleted the blog post A test blog post");
    }

}

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
 * Verify the different possible rights setups of a XWS space.
 * 
 * @version $Id$
 */
public class SpaceRightsTest extends AbstractWorkspacesTest
{
    private static String NOT_ALLOWED_MESSAGE =
        "You are not allowed to view this document or perform this action.";

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify workspace members rights");
        suite.addTestSuite(SpaceRightsTest.class, WorkspacesSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        createJohnDoeUser();
    }
    
    public void testOpenWorkspaceRights()
    {
    	// Create an open workspace
    	createWorkspace("Open", "A space to test open publication mode", "open");
    	
    	// Assert JohnDoe (authenticated user, but not space member) can view, and not edit
        loginAsJohnDoe();
        
        verifySpaceRights("Open", true, true);
    }

    public void testPublicWorkspaceRights()
    {
        // Create a public workspace
        createWorkspace("Public", "A space to test public publication mode", "public");

        // Assert JohnDoe (authenticated user, but not space member) can view, and not edit
        loginAsJohnDoe();

        verifySpaceRights("Public", true, false);

        doTestWorkspaceRolesRights("Public");
    }

    public void testPrivateWorkspaceRights()
    {
        // Create public workspace
        createWorkspace("Private", "A space to test private publication mode", "private");
        
        // Assert JohnDoe (authenticated user, but not space member) cannot view, neither edit
        loginAsJohnDoe();

        verifySpaceRights("Private", false, false);
        
        doTestWorkspaceRolesRights("Private");
    }

    private void doTestWorkspaceRolesRights(String spaceName)
    {
        // Add JohnDoe as a workspace reader
        loginAsAdmin();
        addUserToWorkspaceGroup(spaceName, "John", "Doe", "reader");
        
        loginAsJohnDoe();	

        // Assert he can still view, not edit
        verifySpaceRights(spaceName, true, false);

        loginAsAdmin();

        // Add JohnDoe as a workspace writer
        clearWorkspaceReaders(spaceName);
        clearWorkspaceMembers(spaceName);
        addUserToWorkspaceGroup(spaceName, "John", "Doe", "writer");
        
        // Assert John can view and edit
        verifySpaceRights(spaceName, true, true);
    }
    
    private void verifySpaceRights(String spaceName, boolean viewAllowed, boolean editAllowed)
    {
        open("/xwiki/bin/view/Space_" + spaceName + "/");
        if (viewAllowed)
            assertTextNotPresent(NOT_ALLOWED_MESSAGE);
        else
            assertTextPresent(NOT_ALLOWED_MESSAGE);

        open("/xwiki/bin/view/Space_" + spaceName + "_Wiki/WelcomeOnThisWiki");
        if (viewAllowed)
            assertTextNotPresent(NOT_ALLOWED_MESSAGE);
        else
            assertTextPresent(NOT_ALLOWED_MESSAGE);

        open("/xwiki/bin/edit/Space_" + spaceName + "/");
        if (editAllowed)
            assertTextNotPresent(NOT_ALLOWED_MESSAGE);
        else
            assertTextPresent(NOT_ALLOWED_MESSAGE);

        open("/xwiki/bin/edit/Space_" + spaceName + "_Wiki/WelcomeOnThisWiki");
        if (editAllowed)
            assertTextNotPresent(NOT_ALLOWED_MESSAGE);
        else
            assertTextPresent(NOT_ALLOWED_MESSAGE);
    }
}

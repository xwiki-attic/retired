package com.xpn.xwiki.workspaces.it.selenium;

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

public class WorkspaceRightsTest extends AbstractWorkspacesTest
{
    private static String NOT_ALLOWED_MESSAGE =
        "You are not allowed to view this document or perform this action.";

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify workspace members rights");
        suite.addTestSuite(WorkspaceRightsTest.class, WorkspacesSkinExecutor.class);
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

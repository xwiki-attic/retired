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

import junit.framework.Assert;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.SkinExecutor;

/**
 * Implementation of skin-related actions for the Workspaces skin.
 * 
 * @version $Id$
 */
public class WorkspacesSkinExecutor extends AlbatrossSkinExecutor implements SkinExecutor
{

    private AbstractWorkspacesTest test;

    public WorkspacesSkinExecutor(AbstractXWikiTestCase test)
    {
        super(test);
        this.test = (AbstractWorkspacesTest) test;
    }

    private AbstractWorkspacesTest getTest()
    {
        return this.test;
    }

    /**
     * Overrides {@link com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor#isAuthenticated()}
     * to test if the user is authenticated in XWS
     */
    public boolean isAuthenticated()
    {
        return !this.getTest().getSelenium().isTextPresent("Log-in");
    }

    /**
     * Overrides {@link com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor#logout()} to
     * logout from XWS
     */
    public void logout()
    {
        Assert.assertTrue("User wasn't authenticated.", isAuthenticated());
        getTest().clickLinkWithLocator("//a[@id='logoutbutton']/span");
        Assert.assertFalse("The user is still authenticated after a logout.", isAuthenticated());
    }

    /**
     * Overrides
     * {@link com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor#login(String, String, boolean)}
     * to login into workspaces.
     */
    public void login(String username, String password, boolean rememberme)
    {
        getTest().open("/xwiki/bin/view/Main/");

        if (isAuthenticated()) {
            logout();
        }

        getTest().open("/xwiki/bin/view/XWiki/XWikiLogin");

        getTest().setFieldValue("j_username", username);
        getTest().setFieldValue("j_password", password);
        if (rememberme) {
            getTest().checkField("rememberme");
        }
        getTest().submit();
    }

}

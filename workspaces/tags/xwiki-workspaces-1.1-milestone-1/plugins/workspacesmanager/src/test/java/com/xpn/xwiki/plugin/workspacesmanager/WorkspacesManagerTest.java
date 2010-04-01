package com.xpn.xwiki.plugin.workspacesmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.jmock.Mock;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;
import org.jmock.core.stub.CustomStub;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.workspacesmanager.WorkspacesManager;

import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiRequest;

public class WorkspacesManagerTest extends AbstractWorkspacesTest
{
    /**
     * The workspaces manager instance
     */
    protected WorkspacesManager xwsManager;

    /**
     * Request parameters to simulate a space creation from HTTP request
     */
    Map<String, String[]> params = new HashMap<String, String[]>();

    /**
     * A list of workspaces names
     */
    List<String> xwsList = new ArrayList<String>();

    /**
     * Set-up mocks for testing workspaces
     * 
     * @see org.xwiki.plugin.spacemanager.SpaceManagerImplTest#setUp()
     */
    protected void setUp() throws XWikiException
    {

        super.setUp();

        this.xwsManager =
            new WorkspacesManager("xwsmgr", WorkspacesManager.class.getName(), context);

        this.xwsManager.setMailNotification(false);
        this.xwsManager.init(context);
    }

    /**
     * Assert that a user cannot create himself multiple personnal spaces from http request.
     */
    public void testMultipleWorkspacesForUser() throws SpaceManagerException
    {

        xwsList.add(new String("A Space Name"));

        context.setRequest(createWorkspaceHTTPRequest("Admin", "userspace"));
        this.mockXWikiStore.stubs().method("search").will(
            onConsecutiveCalls(returnValue(Collections.EMPTY_LIST), returnValue(xwsList)));

        // First request, should not fail
        try {
            xwsManager.createSpaceFromRequest("", context);
        } catch (SpaceManagerException e) {
            fail("Failed to create user space from request");
        }

        // Second request, should fail
        try {
            xwsManager.createSpaceFromRequest("", context);
            fail("Should not allow two personnal user spaces");
        } catch (SpaceManagerException e) {
            // test passed
        }
    }

    /**
     * Assert that it's not possible to create multiple organization spaces from http requests.
     */
    public void testMultipleOrganizationWorkspaces() throws SpaceManagerException
    {
        xwsList.add(new String("A Space Name"));

        context.setRequest(createWorkspaceHTTPRequest("Organization", "orgspace"));
        this.mockXWikiStore.stubs().method("search").will(
            onConsecutiveCalls(returnValue(Collections.EMPTY_LIST), returnValue(xwsList)));

        // First request, should pass
        try {
            xwsManager.createSpaceFromRequest("", context);
        } catch (SpaceManagerException e) {
            fail("Failed to create organization space from request");
        }

        // Second request, should fail
        try {
            xwsManager.createSpaceFromRequest("", context);
            fail("Should not allow two organization spaces");
        } catch (SpaceManagerException e) {
            // test passed
        }
    }

    private XWikiRequest createWorkspaceHTTPRequest(String spaceName, String spaceType)
    {
        Mock mockRequest = mock(XWikiRequest.class);

        params.put("XWiki.SpaceClass_0_displayTitle", new String[] {spaceName});
        params.put("XWiki.SpaceClass_0_description", new String[] {"Some space description"});
        params.put("XWiki.WorkspaceSpaceClass_0_accesslevel", new String[] {"private"});
        params.put("XWiki.WorkspaceSpaceClass_0_spacetype", new String[] {spaceType});

        mockRequest.stubs().method("getParameterMap").will(returnValue(params));

        Stub returnRequestValue = new CustomStub("Implements XWikiRequest.get")
        {
            public Object invoke(Invocation invocation) throws Throwable
            {
                String param = (String) invocation.parameterValues.get(0);
                if (params.containsKey(param))
                    return params.get(param)[0];
                return new String();
            }
        };

        mockRequest.stubs().method("get").will(returnRequestValue);
        mockRequest.stubs().method("getParameter").will(returnRequestValue);

        mockRequest.stubs().method("getCookies").will(returnValue(new Cookie[0]));
        mockRequest.stubs().method("getHeader").will(returnValue(new String()));

        return (XWikiRequest) mockRequest.proxy();
    }

}

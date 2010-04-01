package com.xpn.xwiki.plugin.workspacesmanager.activities;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent;

public class WorkspaceHomeActivityBuilder extends AbstractActivityBuilder implements
    ActivityBuilder
{

    private final String WORKSPACE_ACTIVITY_HOMEPAGE_UPDATED_TITLE_KEY =
        "xws.activities.homepageupdated";

    private final String WORKSPACE_ACTIVITY_HOMEPAGE_UPDATED_LINK_KEY = "xws.workspace.homepage";

    public ActivityEvent createActivity(String streamName, String applicationName,
        XWikiDocument olddoc, XWikiDocument newdoc, XWikiContext context)
    {
        ActivityEvent activityEvent =
            super.prepareActivity(streamName, applicationName, olddoc, newdoc, context);
        activityEvent.setTitle(WORKSPACE_ACTIVITY_HOMEPAGE_UPDATED_TITLE_KEY);
        activityEvent.setBody(WORKSPACE_ACTIVITY_HOMEPAGE_UPDATED_TITLE_KEY);
        activityEvent.setParam1(context.getMessageTool().get(
            WORKSPACE_ACTIVITY_HOMEPAGE_UPDATED_LINK_KEY));
        return activityEvent;
    }

    protected String getDocumentTitle(XWikiDocument olddoc, XWikiDocument newdoc,
        XWikiContext context)
    {
        return new String();
    }

}

package com.xpn.xwiki.plugin.workspacesmanager.activities;

import com.xpn.xwiki.plugin.activitystream.api.ActivityStreamException;

public class WorkspaceActivityStreamException extends ActivityStreamException
{

    public WorkspaceActivityStreamException(int module, int code, String message)
    {
        super(module, code, message);
    }

    public static final int XWSACTIVITYSTREAM_NO_ACTIVITY_FOR_NOTIFICATION = 102100;

}

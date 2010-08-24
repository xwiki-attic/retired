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
package com.xpn.xwiki.plugin.workspacesmanager.apps.activities.builders;

import java.util.ArrayList;
import java.util.List;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.WorkspaceActivityStreamException;

/**
 * Activity builder default implementation
 * 
 * @version $Id$
 */
public class ApplicationDefaultActivityBuilder extends AbstractActivityBuilder implements
    ActivityBuilder
{

    public ActivityEvent createActivity(String streamName, String applicationName,
        XWikiDocument newdoc, XWikiDocument olddoc, XWikiContext context)
        throws WorkspaceActivityStreamException
    {
        ActivityEvent activityEvent =
            prepareActivity(streamName, applicationName, newdoc, olddoc, context);
        List<String> params = new ArrayList<String>();
        String title =
            "xws.activities." + applicationName.toLowerCase() + "." + activityEvent.getType();
        params.add(getDocumentTitle(newdoc, olddoc, context));
        activityEvent.setTitle(title);
        activityEvent.setBody(title);
        activityEvent.setParams(params);
        return activityEvent;
    }

    protected String getDocumentTitle(XWikiDocument newdoc, XWikiDocument olddoc,
        XWikiContext context)
    {
        String title = new String();
        title = newdoc.getTitle();
        if (title.equals("") && olddoc != null)
            title = olddoc.getTitle();
        if (title.equals("") && !newdoc.isNew())
            title = newdoc.getName();
        else if (title.equals(""))
            title = olddoc.getName();
        return title;
    }

}

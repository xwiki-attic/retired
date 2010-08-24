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

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEventPriority;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEventType;
import com.xpn.xwiki.plugin.activitystream.impl.ActivityEventImpl;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.WorkspaceActivityStreamException;

/**
 * Abstract activity builder that knows how to prepare an event based on passed documents
 * 
 * @version $Id$
 */
public abstract class AbstractActivityBuilder implements ActivityBuilder
{

    public abstract ActivityEvent createActivity(String streamName, String applicationName, XWikiDocument olddoc,
        XWikiDocument newdoc, XWikiContext context) throws WorkspaceActivityStreamException;

    protected abstract String getDocumentTitle(XWikiDocument olddoc, XWikiDocument newdoc, XWikiContext context);

    protected ActivityEvent prepareActivity(String streamName, String applicationName, XWikiDocument newdoc,
        XWikiDocument olddoc, XWikiContext context)
    {
        ActivityEvent activityEvent = new ActivityEventImpl();
        activityEvent.setStream(streamName);
        activityEvent.setPage(newdoc.getFullName());
        if (newdoc.getDatabase() != null) {
            activityEvent.setWiki(newdoc.getDatabase());
        }
        activityEvent.setPriority(ActivityEventPriority.NOTIFICATION);
        String eventType;
        if (newdoc != null && (olddoc == null || olddoc.isNew())) {
            eventType = ActivityEventType.CREATE;
        } else if (olddoc != null && newdoc.isNew() == true) {
            eventType = ActivityEventType.DELETE;
        } else {
            eventType = ActivityEventType.UPDATE;
        }
        activityEvent.setDate(newdoc.getDate());
        activityEvent.setType(eventType);
        activityEvent.setApplication(applicationName);
        return activityEvent;
    }

}

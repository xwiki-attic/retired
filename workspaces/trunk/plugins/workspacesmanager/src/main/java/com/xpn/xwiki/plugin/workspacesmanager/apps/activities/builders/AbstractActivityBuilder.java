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
 * Abstract activity builder that knows how to prepare an event based on passed documents.
 * 
 * @version $Id$
 */
public abstract class AbstractActivityBuilder implements ActivityBuilder
{

    /**
     * The full name of the document holding the class definition for page comments.
     */
    private static final String XWIKI_COMMENTS_CLASS = "XWiki.XWikiComments";

    /**
     * {@inheritDoc}
     */
    public abstract ActivityEvent createActivity(String streamName, String applicationName, XWikiDocument olddoc,
        XWikiDocument newdoc, XWikiContext context) throws WorkspaceActivityStreamException;

    /**
     * Get the document title to fill in the activity event based on the passed documents.
     * The underlaying implementation is free to return the title according to its own criteria : name of the document,
     * title of the document, property in an XWiki object, etc.
     * 
     * @param olddoc the old version of the document that triggers the notification
     * @param newdoc the new version of the document that triggers the notification
     * @param context the XWiki context
     * @return the title to be filled in the activity event.
     */
    protected abstract String getDocumentTitle(XWikiDocument olddoc, XWikiDocument newdoc, XWikiContext context);

    /**
     * Prepare an activity event, filling-in data based on passed arguments and un-variable values. 
     * Fields that are filled are:
     * <ul>
     * <li>the stream name</li>
     * <li>the page</li>
     * <li>the wiki</li> 
     * <li>the priority (to {@link com.xpn.xwiki.plugin.activitymanager.ActivityEventPriority.NOTIFICATION})</li>
     * <li>the type of the event : create, delete or update</li>
     * <li>the date of the event, set to "now"</li>
     * <li>the application</li>
     * </ul>
     * 
     * @param streamName the name of the stream the event will flow in
     * @param applicationName the name of the applicaiton the event is fired from
     * @param olddoc the old version of the document that triggers the notification
     * @param newdoc the new version of the document that triggers the notification
     * @param context the XWiki context
     * @return the prepared activity event
     */
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
        } else if (olddoc != null && newdoc.isNew()) {
            eventType = ActivityEventType.DELETE;
        } else {
            if (newdoc.getObjectNumbers(XWIKI_COMMENTS_CLASS) > olddoc.getObjectNumbers(XWIKI_COMMENTS_CLASS)) {
                eventType = ActivityEventType.CREATE_COMMENT;
            } else {
                eventType = ActivityEventType.UPDATE;
            }
        }
        activityEvent.setDate(newdoc.getDate());
        activityEvent.setType(eventType);
        activityEvent.setApplication(applicationName);
        return activityEvent;
    }

}

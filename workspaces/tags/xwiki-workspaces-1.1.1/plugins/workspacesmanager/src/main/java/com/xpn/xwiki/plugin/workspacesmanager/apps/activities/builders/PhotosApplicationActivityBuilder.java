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
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.WorkspaceActivityStreamException;

/**
 * Activity builder for the photos application
 * 
 * @version $Id: $
 */
public class PhotosApplicationActivityBuilder extends ApplicationDefaultActivityBuilder
{

    public ActivityEvent createActivity(String streamName, String applicationName, XWikiDocument newdoc,
        XWikiDocument olddoc, XWikiContext context) throws WorkspaceActivityStreamException
    {
        if ((olddoc!=null && olddoc.getObject("XWSCode.PhotoGalleryClass", false, context) != null)
            || (newdoc != null && newdoc.getObject("XWSCode.PhotoGalleryClass", false, context) != null)) {
            return super.createActivity(streamName, applicationName, newdoc, olddoc, context);
        } else {
            throw new WorkspaceActivityStreamException(WorkspaceActivityStreamException.MODULE_PLUGIN_ACTIVITY_STREAM,
                WorkspaceActivityStreamException.XWSACTIVITYSTREAM_NO_ACTIVITY_FOR_NOTIFICATION,
                "No activity is generated when adding a photo to a galery");
        }
    }

}

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
 *
 */
package org.xwiki.eclipse.core;

import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.xwiki.eclipse.core.model.ModelObject;
import org.xwiki.eclipse.core.notifications.CoreEvent;
import org.xwiki.eclipse.core.notifications.ICoreEventListener;
import org.xwiki.eclipse.core.notifications.NotificationManager;

public class XWootManager implements ICoreEventListener
{
    public final String SYNCHRONIZE_COMMAND = "/Synchronize?action=synchronize";

    private static XWootManager sharedInstance;

    public static synchronized XWootManager getDefault()
    {
        if (sharedInstance == null)
            sharedInstance = new XWootManager();

        return sharedInstance;
    }

    private XWootManager()
    {
        NotificationManager.getDefault()
            .addListener(
                this,
                new CoreEvent.Type[] {CoreEvent.Type.OBJECT_REMOVED,
                CoreEvent.Type.OBJECT_STORED, CoreEvent.Type.PAGE_RENAMED,
                CoreEvent.Type.PAGE_REMOVED, CoreEvent.Type.PAGE_STORED});
    }

    public void synchronize(String xwootUrl)
    {
        if (xwootUrl == null)
            return;

        HttpURLConnection connection = null;
        
        try {
            URL synchronizeUrl = new URL(xwootUrl + SYNCHRONIZE_COMMAND);
            connection = (HttpURLConnection) synchronizeUrl.openConnection();
            connection.setRequestMethod("GET");
            int response = connection.getResponseCode();
            if (response != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP Response code: " + response);
            }

        } catch (Exception e) {
            CoreLog.logError("Error while synchronizing with XWoot: " + e.getMessage());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public void handleCoreEvent(CoreEvent event)
    {
        ModelObject object = ((ModelObject) event.getData());
        final DataManager dataManager = object.getDataManager();

        if (dataManager.isConnected()) {
            Job synchronizeJob = new Job("XWoot Synchronize job")
            {
                protected IStatus run(IProgressMonitor monitor)
                {
                    try {
                        if (dataManager.isXwootAutosynchEnabled())
                            synchronize(dataManager.getXwootEndpoint());
                    } catch (CoreException e) {
                        CoreLog.logError("Failed to synchronize with XWoot: " + e.getMessage());
                    }
                    return Status.OK_STATUS;
                }

            };
            synchronizeJob.setPriority(Job.SHORT);
            synchronizeJob.schedule();
        }
    }
}

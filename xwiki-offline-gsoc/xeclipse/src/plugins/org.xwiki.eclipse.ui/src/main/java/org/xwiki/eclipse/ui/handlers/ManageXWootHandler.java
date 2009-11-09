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
package org.xwiki.eclipse.ui.handlers;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.core.CoreLog;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.ui.editors.XWootEditor;
import org.xwiki.eclipse.ui.editors.XWootEditorInput;
import org.xwiki.eclipse.ui.utils.UIUtils;

public class ManageXWootHandler extends AbstractHandler
{

    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);

        Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selection);
        for (Object selectedObject : selectedObjects) {
            if (selectedObject instanceof DataManager) {
                final DataManager dataManager = (DataManager) selectedObject;

                String xwootEndpoint = null;
                try {
                    xwootEndpoint = dataManager.getXwootEndpoint();
                } catch (CoreException e) {
                    CoreLog.logError("Unable to get xwoot endpoint.");
                }

                if (xwootEndpoint == null) {
                    UIUtils
                        .showMessageDialog(
                            Display.getDefault().getActiveShell(),
                            "XWoot endpoint not defined",
                            "Please go to the P2P section in the Properties page for this connection and specify the XWoot endpoint associated with the XWiki.");
                    return null;
                }

                try {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .openEditor(new XWootEditorInput(dataManager), XWootEditor.ID, true);
                } catch (PartInitException e) {
                    UIUtils.showMessageDialog(Display.getDefault().getActiveShell(),
                        "Error opening editor", "There was an error while opening the editor: "
                            + e.getMessage());
                }

            }
        }

        return null;
    }

}

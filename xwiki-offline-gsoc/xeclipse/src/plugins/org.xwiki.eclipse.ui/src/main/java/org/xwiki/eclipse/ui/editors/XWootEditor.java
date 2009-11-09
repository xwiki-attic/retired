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
package org.xwiki.eclipse.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.xwiki.eclipse.core.CoreLog;

public class XWootEditor extends EditorPart
{
    public static final String ID = "org.xwiki.eclipse.ui.editors.XWoot";

    private Browser browser;

    @Override
    public void doSave(IProgressMonitor monitor)
    {
    }

    @Override
    public void doSaveAs()
    {
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
    }

    @Override
    protected void setInput(IEditorInput input)
    {
        if (!(input instanceof XWootEditorInput)) {
            throw new IllegalArgumentException("Invalid input for editor");
        }

        super.setInput(input);
    }

    @Override
    public boolean isDirty()
    {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    @Override
    public void createPartControl(Composite parent)
    {
        browser = new Browser(parent, SWT.NONE);
        String xwootEndpoint = null;
        try {
            xwootEndpoint =
                ((XWootEditorInput) this.getEditorInput()).getDataManager().getXwootEndpoint();
        } catch (CoreException e) {
            CoreLog.logError("Unable to get xwoot endpoint.");
        }

        browser.setUrl(xwootEndpoint);
    }

    @Override
    public void setFocus()
    {
        browser.getDisplay().asyncExec(new Runnable(){
            public void run(){
                browser.setFocus();
            }
        });
        
    }

}

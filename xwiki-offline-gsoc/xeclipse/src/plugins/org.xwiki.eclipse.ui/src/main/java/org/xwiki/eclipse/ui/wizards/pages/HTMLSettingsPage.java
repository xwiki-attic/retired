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
package org.xwiki.eclipse.ui.wizards.pages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.xwiki.eclipse.ui.wizards.AbstractExportWizard;

/**
 * @author Eduard Moraru
 *
 */
public class HTMLSettingsPage extends ExportSettingsPage
{
    private Text descriptionText;
    
    public HTMLSettingsPage(String pageName)
    {
        super(pageName);
        setTitle("Export as HTML page");
        
        // TODO: set a banner for this wizard page.
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        exportWizardState = ((AbstractExportWizard) getWizard()).getExportWizardState();
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(composite);
        
        /* Package Name */
        addPackageNameText(composite);
        
        /* Description */
        label = new Label(composite, SWT.NONE);
        label.setText("Description:");
        
        descriptionText = new Text(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(descriptionText);
        descriptionText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                exportWizardState.setExportMetaDataValue("description", descriptionText.getText().trim());
                getContainer().updateButtons();
            }
        });
        descriptionText.setToolTipText("A short description of the export. This can be viewed later, when reimporting into an XWiki instance.");
        
        /* Save Location */
        addSaveLocationComposite(composite);
        
        setControl(composite);
    }

    @Override
    public boolean isPageComplete()
    {
        if (exportWizardState.getExportURL() == null) {            
            try {
                parseSelection();
            } catch (Exception e) {
                return false;
            }
        }
        
        // If the selection is ok, enable the controls to input further data.
        setControlsEnabled(true);
        
        if (!validateInput())
            return false;

        /*
         * TODO: some extra input validation check for invalid chars that might
         * damage the export URL.
         */
        
        /* Clear error messages if everything's fine. */
        setErrorMessage(null);
        
        return true;
    }
    
    protected void setControlsEnabled(boolean enabled)
    {
        if (packageNameText != null)
            packageNameText.setEnabled(enabled);
        
        if (descriptionText != null)
            descriptionText.setEnabled(enabled);
        
        if (saveLocationText != null)
            saveLocationText.setEnabled(enabled);
        
        if (browseButton != null)
            browseButton.setEnabled(enabled);
    }
}

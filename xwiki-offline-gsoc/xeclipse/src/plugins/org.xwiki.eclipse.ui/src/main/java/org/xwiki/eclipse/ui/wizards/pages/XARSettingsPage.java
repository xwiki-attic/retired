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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.ui.wizards.AbstractExportWizard;

/**
 * @author Eduard Moraru
 *
 */
public class XARSettingsPage extends ExportSettingsPage
{    
    private Text authorText, descriptionText, licenseText, versionText;
    
    private Button historyCheck, backupCheck;
    
    public XARSettingsPage(String pageName)
    {
        super(pageName);
        setTitle("Export as XAR archieve.");

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
        
        /* Author */
        label = new Label(composite, SWT.NONE);
        label.setText("Author:");
        
        authorText = new Text(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(authorText);
        authorText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                exportWizardState.setExportMetaDataValue("author", authorText.getText().trim());
                getContainer().updateButtons();
            }
        });
        Display.getCurrent().asyncExec(new Runnable(){
            public void run() {
                authorText.setText("XWiki");
            }
        });
        authorText.setToolTipText("The name of the creator of this export. This can be viewed later, when reimporting into an XWiki instance.");
        
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
        
        /* License */
        label = new Label(composite, SWT.NONE);
        label.setText("License:");
        
        licenseText = new Text(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(licenseText);
        licenseText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                exportWizardState.setExportMetaDataValue("license", licenseText.getText().trim());
                getContainer().updateButtons();
            }
        });
        Display.getCurrent().asyncExec(new Runnable(){
            public void run() {
                licenseText.setText("LGPL");
            }
        });
        licenseText.setToolTipText("The license under which this export will be created. This can be viewed later, when reimporting into an XWiki instance.");
        
        /* Version */
        label = new Label(composite, SWT.NONE);
        label.setText("Version:");
        
        versionText = new Text(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(versionText);
        versionText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                exportWizardState.setExportMetaDataValue("version", versionText.getText().trim());
                getContainer().updateButtons();
            }
        });
        Display.getCurrent().asyncExec(new Runnable(){
            public void run() {
                versionText.setText("1.0.0");
            }
        });
        versionText.setToolTipText("The version of this export if other similar exports have been done before.");
        
        /* History */
        label = new Label(composite, SWT.NONE);
        historyCheck = new Button(composite, SWT.CHECK);
        historyCheck.setText("History");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(historyCheck);
        historyCheck.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                exportWizardState.setExportMetaDataValue(
                    "history", historyCheck.getSelection() ? "true" : null);
            }
        });
        historyCheck.setToolTipText("Wheather or not to include the page history of the exported pages.");
        
        /* Backup */
        label = new Label(composite, SWT.NONE);
        backupCheck = new Button(composite, SWT.CHECK);
        backupCheck.setText("Backup");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(backupCheck);
        backupCheck.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                exportWizardState.setExportMetaDataValue(
                    "backup", backupCheck.getSelection() ? "true" : null);
            }
        });
        backupCheck.setToolTipText("If checked then the XWiki document authors of each page remain the" +
        		" same as the ones exported when reimporting into an XWiki instance.");
        
        /* Save Location */
        addSaveLocationComposite(composite);
        
        setControl(composite);
    }
    
    @Override
    public boolean isPageComplete()
    {
        if (exportWizardState.getExportURL() == null) {
            
            DataManager dataManager = null;
            
            try {
                dataManager = parseSelection();
            } catch (Exception e) {
                return false;
            }
            
            exportWizardState.setDataManager(dataManager);
        }
        
        // If the selection is ok, enable the controls to input further data.
        setControlsEnabled(true);
        
        if (!validateInput()) {
            return false;
        }

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
        
        if (authorText != null)
            authorText.setEnabled(enabled);
        
        if (descriptionText != null)
            descriptionText.setEnabled(enabled);
        
        if (licenseText != null)
            licenseText.setEnabled(enabled);
        
        if (versionText != null)
            versionText.setEnabled(enabled);
        
        if (historyCheck != null)
            historyCheck.setEnabled(enabled);
        
        if (backupCheck != null)
            backupCheck.setEnabled(enabled);
        
        if (saveLocationText != null)
            saveLocationText.setEnabled(enabled);
        
        if (browseButton != null)
            browseButton.setEnabled(enabled);
    }
}

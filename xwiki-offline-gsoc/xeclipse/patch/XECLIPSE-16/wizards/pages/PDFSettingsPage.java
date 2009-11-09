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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
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
import org.xwiki.eclipse.core.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.core.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.ui.wizards.AbstractExportWizard;

/**
 * @author Eduard Moraru
 *
 */
public class PDFSettingsPage extends ExportSettingsPage
{
    protected Text includeChildrenText, includeLinksText;
    
    protected Button pageBreaksCheck, commentsCheck, attachmentsCheck;

    public PDFSettingsPage(String pageName)
    {
        super(pageName);
        setTitle("Export as PDF document");
        // TODO: add banner
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
        
        /* Include Children */
        label = new Label(composite, SWT.NONE);
        label.setText("Include Children:");
        
        includeChildrenText = new Text(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(includeChildrenText);
        includeChildrenText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                if (includeChildrenText.getText().trim().equals("0"))
                    exportWizardState.setExportMetaDataValue("includechilds", null);
                else
                    exportWizardState.setExportMetaDataValue("includechilds", includeChildrenText.getText().trim());
                
                getContainer().updateButtons();
            }
        });
        Display.getCurrent().asyncExec(new Runnable(){
            public void run() {
                includeChildrenText.setText("0");
            }
        });
        includeChildrenText.setToolTipText("A number specifying the depth of children " +
        		"pages to include in the PDF export. For example 2 will include all children pages " +
        		"of the page to export (i.e. pages which have the page as its parent) and all the " +
        		"children's children.");
        
        /* Include Links */
        label = new Label(composite, SWT.NONE);
        label.setText("Include Links:");
        
        includeLinksText = new Text(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(includeLinksText);
        includeLinksText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                if (includeLinksText.getText().trim().equals("0"))
                    exportWizardState.setExportMetaDataValue("includelinks", null);
                else
                    exportWizardState.setExportMetaDataValue("includelinks", includeLinksText.getText().trim());
                
                getContainer().updateButtons();
            }
        });
        Display.getCurrent().asyncExec(new Runnable(){
            public void run() {
                includeLinksText.setText("0");
            }
        });
        includeLinksText.setToolTipText("A number specifying the depth of linked pages to include " +
        		"in the PDF export. For example 2 will include all pages linked from the page to " +
        		"export and the links in the linked pages.");
        
        /* Page Breaks */
        label = new Label(composite, SWT.NONE);
        pageBreaksCheck = new Button(composite, SWT.CHECK);
        pageBreaksCheck.setText("Page Breaks");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(pageBreaksCheck);
        pageBreaksCheck.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                exportWizardState.setExportMetaDataValue(
                    "pagebreaks", (pageBreaksCheck.getSelection() ? "1" : null));
            }
        });
        pageBreaksCheck.setToolTipText("If a page break should be inserted between each page exported.");
        
        /* Comments */
        label = new Label(composite, SWT.NONE);
        commentsCheck = new Button(composite, SWT.CHECK);
        commentsCheck.setText("Comments");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(commentsCheck);
        commentsCheck.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                exportWizardState.setExportMetaDataValue(
                    "comments", (commentsCheck.getSelection() ? "1" : null));
            }
        });
        commentsCheck.setToolTipText("Weather or not to export comments also.");
        
        /* Attachments */
        label = new Label(composite, SWT.NONE);
        attachmentsCheck = new Button(composite, SWT.CHECK);
        attachmentsCheck.setText("Attachments");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(attachmentsCheck);
        attachmentsCheck.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                exportWizardState.setExportMetaDataValue(
                    "attachments", (attachmentsCheck.getSelection() ? "1" : null));
            }
        });
        attachmentsCheck.setToolTipText("If a page break should be inserted between each page exported.");
        
        /* Save Location */
        addSaveLocationComposite(composite);
        
        setControl(composite);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isPageComplete()
    {        
        if (exportWizardState.getExportURL() == null) {            
            Set selectedObjects = exportWizardState.getSelectedObjects();
            
            // There DataManager of all the selected pages/spaces. It can be only one.
            DataManager dataManager = null;
            
            DataManager dataManagerOfSelectedObject = null;
            
            List selectedSpaces = new LinkedList();
            List selectedPages = new LinkedList();
            
            for(Object selectedObject : selectedObjects) {
                if (selectedObject instanceof XWikiEclipseSpaceSummary) {
                    XWikiEclipseSpaceSummary selectedSpace = (XWikiEclipseSpaceSummary) selectedObject;
                    dataManagerOfSelectedObject = selectedSpace.getDataManager();
                    
                    selectedSpaces.add(selectedObject);
                }
                
                else
                    
                if (selectedObject instanceof XWikiEclipsePageSummary) {
                    XWikiEclipsePageSummary selectedPage = (XWikiEclipsePageSummary) selectedObject;
                    dataManagerOfSelectedObject = selectedPage.getDataManager();
                    
                    selectedPages.add(selectedObject);
                }
                
                else
                // Remove from the selection objects that are not the target of this export
                selectedObjects.remove(selectedObject);
                
                
                if (dataManager == null) {
                    dataManager = dataManagerOfSelectedObject;
                } else {
                    if (!dataManager.equals(dataManagerOfSelectedObject)) {
                        setErrorMessage("Please select pages to export from only one Connection at a time.");
                        setControlsEnabled(false);
                        return false;
                    }
                }
            }
            
            if (selectedObjects.size() == 0) {
                setErrorMessage("You must select the pages you want to export from the XWiki Navigator." +
                        " Go back and try again.");
                setControlsEnabled(false);
                return false;
            }
            
            if (!dataManager.isConnected()) {
                setErrorMessage("You must be connected to the XWiki from which you want to export.");
                setControlsEnabled(false);
                return false;
            }            
    
            String exportURL = null;
            try {
                exportURL = dataManager.getEndpoint();
            } catch (CoreException e) {
                setErrorMessage("An error occured while initializing. Please try again.");
                setControlsEnabled(false);
                return false;
            }

            int indexToKeep = exportURL.indexOf("xwiki")+"xwiki/".length();
            exportURL = exportURL.substring(0, indexToKeep) + "bin/export/Not/Important";
            
            exportWizardState.setExportURL(exportURL);
            exportWizardState.setSelectedSpaces(selectedSpaces);
            exportWizardState.setSelectedPages(selectedPages);
        }
        
        // If the selection is ok, enable the controls to input further data.
        setControlsEnabled(true);
        
        if (!validateInput())
            return false;

        try{
            int value1 = Integer.parseInt(includeChildrenText.getText());
            int value2 = Integer.parseInt(includeLinksText.getText());
            if (value1 < 0 || value2 < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            setErrorMessage("The values must be pozitive integers.");
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

    /*
     * Enables or disables the user interactive controls. Used when the user did not make a proper
     * selection prior to opening this export wizard.
     */
    protected void setControlsEnabled(boolean enabled)
    {
        if (packageNameText != null)
            packageNameText.setEnabled(enabled);
        
        if (includeChildrenText != null)
            includeChildrenText.setEnabled(enabled);
        
        if (includeLinksText != null)
            includeLinksText.setEnabled(enabled);
        
        if (pageBreaksCheck != null)
            pageBreaksCheck.setEnabled(enabled);
        
        if (attachmentsCheck != null)
            attachmentsCheck.setEnabled(enabled);
        
        if (commentsCheck != null)
            commentsCheck.setEnabled(enabled);
        
        if (saveLocationText != null)
            saveLocationText.setEnabled(enabled);
        
        if (browseButton != null)
            browseButton.setEnabled(enabled);
    }
}

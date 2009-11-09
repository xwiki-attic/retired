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

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.core.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.ui.wizards.states.ExportWizardState;

/**
 * Abstract class containing common procedures in most of the export wizard pages.
 * 
 * @author Eduard Moraru
 */
public abstract class ExportSettingsPage extends WizardPage
{
    protected ExportWizardState exportWizardState;
    
    protected Text packageNameText, saveLocationText;
    
    protected Label label;
    
    protected Button browseButton;
    
    protected ExportSettingsPage(String pageName)
    {
        super(pageName);
    }
    
    /**
     * Parses the selection in the XWiki Navigator and separates the items by type.
     * 
     * @return the DataManager of the selected items. Used for XAR export authentication.
     * @throws Exception when something goes wrong, the error is set and an exception is thrown.
     */
    @SuppressWarnings("unchecked")
    public DataManager parseSelection() throws Exception
    {
        Set selectedObjects = exportWizardState.getSelectedObjects();
        
        // There DataManager of all the selected pages/spaces. It can be only one.
        DataManager dataManager = null;
        
        DataManager dataManagerOfSelectedObject = null;
        
        // The DataManager that is actually selected by the user. 
        DataManager selectedDataManager = null;
        List selectedSpaces = new LinkedList();
        List selectedPages = new LinkedList();
        
        for(Object selectedObject : selectedObjects) {            
            if (selectedObject instanceof DataManager) {
                dataManagerOfSelectedObject = (DataManager) selectedObject;
                
                selectedDataManager = (DataManager) selectedObject;
            }
            
            else 
            
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
                    throw new Exception();
                }
            }
        }
        
        if (selectedObjects.size() == 0) {
            setErrorMessage("You must select the pages you want to export from the XWiki Navigator." +
                    " Go back and try again.");
            setControlsEnabled(false);
            throw new Exception();
        }
        
        if (!dataManager.isConnected()) {
            setErrorMessage("You must be connected to the XWiki from which you want to export.");
            setControlsEnabled(false);
            throw new Exception();
        }            

        String exportURL = null;
        try {
            exportURL = dataManager.getEndpoint();
        } catch (CoreException e) {
            setErrorMessage("An error occured while initializing. Please try again.");
            setControlsEnabled(false);
            throw new Exception();
        }

        int indexToKeep = exportURL.indexOf("xwiki")+"xwiki/".length();
        exportURL = exportURL.substring(0, indexToKeep) + "bin/export/Not/Important";
        
        exportWizardState.setExportURL(exportURL);
        exportWizardState.setSelectedDataManager(selectedDataManager);
        exportWizardState.setSelectedSpaces(selectedSpaces);
        exportWizardState.setSelectedPages(selectedPages);
        
        return dataManager;
    }
    
    
    /**
     * Validates the name and save location inputs.
     * @return false if the input is invalid and the error is displayed.
     */
    protected boolean validateInput()
    {
        String packageName = packageNameText.getText().trim();
        if (packageName.length() == 0){
            setErrorMessage("Please specify a file name to save the exported page(s).");
            return false;
        }
        
        if (!packageName.matches("[_a-zA-Z\\-+0-9]+")) {
            setErrorMessage("The provided name contains illegal characters.");
            return false;
        }
        
        String saveLocation = saveLocationText.getText();
        if (saveLocation.length() == 0) {
            setErrorMessage("You must specify the save location for the exported pages.");
            return false;
        }
        
        File saveLocationDirectory = new File(saveLocation);
        if (!saveLocationDirectory.canWrite()) {
            setErrorMessage("Please choose a save location that you have write access to.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Add the name input.
     * 
     * @param parent
     */
    protected void addPackageNameText(Composite parent)
    {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Package name:");

        packageNameText = new Text(parent, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(packageNameText);
        packageNameText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                exportWizardState.setExportMetaDataValue("name", packageNameText.getText().trim());
                getContainer().updateButtons();
            }
        });
        packageNameText.setToolTipText("The name of the file that will contain the exported pages (no extension).");
    }
    
    /**
     * Add the browse directory button.
     * 
     * @param parent
     */
    protected void addBrowseButton(final Composite parent)
    {
        browseButton = new Button(parent, SWT.PUSH);
        browseButton.setText("Browse");
        browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                String dirName = new DirectoryDialog(parent.getShell()).open();
                if (dirName != null) {
                    saveLocationText.setText(dirName);
                }
            }
        });
    }
    
    
    /**
     * Add save location composite containing browse button and input text.
     * 
     * @param parent
     */
    protected void addSaveLocationComposite(Composite parent)
    {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Save location:");
        
        final Composite saveLocationComposite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(saveLocationComposite);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(saveLocationComposite);
        
        saveLocationText = new Text(saveLocationComposite, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(saveLocationText);
        saveLocationText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                exportWizardState.setSaveLocation(saveLocationText.getText());
                getContainer().updateButtons();
            }
        });
        Display.getCurrent().asyncExec(new Runnable(){
            public void run() {
                String userHomeDirectory = System.getProperty("user.home");
                if (userHomeDirectory != null) {
                    saveLocationText.setText(userHomeDirectory);
                }
            }
        });
        saveLocationText.setEditable(false);
        saveLocationText.setToolTipText("The directory on disk where to save the exported pages.");
        
        addBrowseButton(saveLocationComposite);
    }
    
    /**
     * Enables or disables the user interactive controls. Used when the user did not make a proper
     * selection prior to opening this export wizard.
     * @param enabled
     */
    protected abstract void setControlsEnabled(boolean enabled);
}

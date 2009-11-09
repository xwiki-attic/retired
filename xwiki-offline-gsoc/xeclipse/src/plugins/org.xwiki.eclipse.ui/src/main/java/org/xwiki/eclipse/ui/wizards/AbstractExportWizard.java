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
package org.xwiki.eclipse.ui.wizards;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.internal.preferences.Base64;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.ui.utils.UIUtils;
import org.xwiki.eclipse.ui.wizards.states.ExportWizardState;

/**
 * Abstract class containing common procedures in all the export wizards.
 * 
 * @author Eduard Moraru
 */
public abstract class AbstractExportWizard extends Wizard implements IExportWizard
{
    protected ExportWizardState exportWizardState;

    protected String expectedContentType;
    
    protected String format, extension;
    
    public AbstractExportWizard()
    {
        super();
        exportWizardState = new ExportWizardState();
        setNeedsProgressMonitor(true);
    }
    
    /**
     * @return the exportWizardState
     */
    public ExportWizardState getExportWizardState()
    {
        return exportWizardState;
    }

    /**
     * Force the superclasses to implement.
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public abstract void addPages();
    
    /**
     * Force the superclasses to implement.
     * 
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean canFinish()
    {
        Set selectedObjects = exportWizardState.getSelectedObjects();
        
        if (selectedObjects.size() == 0){
            return false;
        }
        
        return super.canFinish();
    }
    
    /**
     * Force the superclasses to implement.
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public abstract boolean performFinish();

    /**
     * Save the selection from the XWiki Eclipse Navigator.
     */
    @SuppressWarnings("unchecked")
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        Set selectedObjects = UIUtils.getSelectedObjectsFromSelection(selection);
        exportWizardState.setSelectedObjects(selectedObjects);
    }

    /**
     * @return a string with concatenated pairs of the form "&variable=value"
     */
    protected String appendMetaData()
    {
        String result = "";
        
        Set<String> variables = exportWizardState.getExportMetaDataVariables();
        for (String variable : variables) {
            String value = exportWizardState.getExportMetaDataValue(variable);
            if (value != null && value.length() != 0) {
                result += "&" + variable + "=" + value; 
            }
        }
        
        return result;
    }
    
    /**
     * Prepare the export URL and call the download procedure
     * 
     * @param pagesUrlVariable the string containing concatenated "&pages=Space.Page" values or
     * more specific values, depending on the exported format.
     * @throws Exception when something goes wrong and displays the error message in the current
     * wizzardPage.
     */
    protected void doExport(String pagesUrlVariable) throws Exception
    {
        // Prepare the URL
        String exportURL = exportWizardState.getExportURL();
        exportURL += "?format=" + getFormat();
        
        // Append meta-data 
        exportURL += appendMetaData();
        
        // Append "pages" variable if it is the case
        if (pagesUrlVariable.length() != 0)
            exportURL += pagesUrlVariable;

        String fileName = exportWizardState.getExportMetaDataValue("name") + "." + getExtension();
        String savePath = exportWizardState.getSaveLocation() + File.separatorChar + fileName;
        
        // Prompt the user for overwrite confirmation if file already exists on disk
        File outputFile = new File(savePath);
        if (outputFile.exists()) {
            MessageBox messageBox = new MessageBox(getContainer().getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
            messageBox.setMessage(String.format("The file named %s already exists. Overwrite?", fileName));
            messageBox.setText("Overwrite existing file?");
            
            int result = messageBox.open();
            if (result == SWT.NO) {
                throw new Exception("User canceled.");
            }
        }
        
        // Connect and download the file
        saveExportedFileFromURL(exportURL, savePath);
    }

    /**
     * Download the file.
     * 
     * @param exportURL the URL from which to download the exported pages as one file.
     * @param savePath the location of the file on disc where the save the exported pages.
     * @throws Exception when something wrong happens, the error on the current page is set and an
     * exception is thrown.
     */
    protected void saveExportedFileFromURL(final String exportURL, final String savePath) throws Exception
    {
        final WizardPage currentPage = ((WizardPage) getContainer().getCurrentPage());
        
        // Container to store exceptions thrown in the IRunnableWithProgress.
        final List<Exception> exceptionContainer = new ArrayList<Exception>();
        
        try {
            getContainer().run(true, false, new IRunnableWithProgress()
            {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
                {
                    monitor.beginTask("Exporting pages", IProgressMonitor.UNKNOWN);
                    monitor.subTask("Initializing");
                    
                    File outputFile = new File(savePath);
                    
                    InputStream input = null;
                    OutputStream output = null;
                    
                    try {
                        URL url = new URL(exportURL);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        
                        // Set HTTP Basic Authentification for XAR export when needed.
                        DataManager dataManager = exportWizardState.getDataManager();
                        if (dataManager != null) {
                            String userPass = new String(
                                Base64.encode((dataManager.getUserName() + ":" + dataManager.getPassword())
                                    .getBytes()));
                            connection.setRequestProperty("Authorization", "Basic " + userPass);
                        }
                        
                        int responseCode = connection.getResponseCode();
                        if (responseCode != HttpURLConnection.HTTP_OK) {
                            if (responseCode == HttpURLConnection.HTTP_REQ_TOO_LONG)
                                throw new Exception("Too many pages selected for export.");
                            else
                                throw new Exception("Wrong response.");
                        }
                        
                        if (expectedContentType != null && 
                            !connection.getContentType().startsWith(expectedContentType))
                            throw new Exception("Invalid data received.");
                        
                        input = connection.getInputStream();
                        output = new BufferedOutputStream(new FileOutputStream(outputFile));
                        
                        monitor.subTask("Exporting...");
                        
                        // Download the file
                        int bufferSize = 1024,
                            nrOfReadBytes;
                        byte data[] = new byte[bufferSize];
                        int totalRead = 0;
                        while ((nrOfReadBytes = input.read(data)) != -1) {
                            output.write(data, 0, nrOfReadBytes);
                            totalRead += nrOfReadBytes;
                        }
                        
                        monitor.subTask("Done");
                        
                    } catch (MalformedURLException e) {
                        currentPage.setErrorMessage("An error has occured while initializing. Please make sure " +
                                "that your entered values do not contain special characters.");
                        exceptionContainer.add(e);
                    } catch (Exception e) {
                        currentPage.setErrorMessage("An error has occured while performing the export: " +
                            e.getMessage() + " Please try again later.");
                        exceptionContainer.add(e);
                    } finally {
                        try {
                            if (output != null) {
                                output.close();
                            }
                            if (input != null) {
                                input.close();
                            }
                        } catch (Exception e) { }
                        
                        monitor.done();
                    }
                }
            });
        } catch (Exception e) {
            currentPage.setErrorMessage(
                String.format("An unexpected error has occured" + 
                    (e.getMessage() != null ? ": %s" : ".")  + 
                    " Please try again.", e.getMessage()));
            throw new Exception();
        }
        
        // Check if something went wrong and alert the caller trough an Exception
        if (exceptionContainer.size() != 0)
            throw new Exception();
    }

    /**
     * @see AbstractExportWizard#setExpectedContentType(String)
     * @return the expectedContentType
     */
    public String getExpectedContentType()
    {
        return expectedContentType;
    }

    /**
     * The content type to be expected for the file containing the exported pages.
     *  
     * @param expectedContentType the expectedContentType to set
     */
    public void setExpectedContentType(String expectedContentType)
    {
        if (expectedContentType != null)
            this.expectedContentType = expectedContentType;
    }

    /**
     * @see AbstractExportWizard#getFormat()
     * @return the format
     */
    public String getFormat()
    {
        return format;
    }

    /**
     * The format variable to be sent to the export URL.
     * 
     * @param format the format to set
     */
    public void setFormat(String format)
    {
        this.format = format;
    }

    /**
     * @return the extension
     */
    public String getExtension()
    {
        return extension;
    }

    /**
     * The file extension the downloaded file will have.
     * @param extension the extension to set
     */
    public void setExtension(String extension)
    {
        this.extension = extension;
    }
}

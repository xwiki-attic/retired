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
package org.xwiki.eclipse.ui.wizards.states;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.xwiki.eclipse.core.DataManager;

/**
 * Contains data shared by a Wizard and the pages it contains.
 * 
 * @author Eduard Moraru
 */
public class ExportWizardState
{
    @SuppressWarnings("unchecked")
    private Set selectedObjects;
    
    @SuppressWarnings("unchecked")
    private List selectedSpaces, selectedPages;
    
    private DataManager selectedDataManager;
    
    private String exportURL;
    
    // MetaData specific to the export format.
    private Hashtable<String, String> exportMetaData;
    
    private String saveLocation;
    
    private DataManager dataManager;
    
    public ExportWizardState() {
        exportMetaData = new Hashtable<String, String>();
    }
    
    /**
     * @return the URL variables stored
     */
    public Set<String> getExportMetaDataVariables() {
        return exportMetaData.keySet();
    }
    
    /**
     * @param key the URL variable
     * @return the value of the URL variable
     */
    public String getExportMetaDataValue(String key) {
        return exportMetaData.get(key);
    }
    
    /**
     * The export's metadata composed by the specific URL variables.
     * 
     * @param key the export URL variable
     * @param value
     */
    public void setExportMetaDataValue(String key, String value) {
        exportMetaData.put(key, value);
    }
    
    /**
     * @return the saveLocation
     */
    public String getSaveLocation()
    {
        return saveLocation;
    }

    /**
     * @param saveLocation the saveLocation to set
     */
    public void setSaveLocation(String saveLocation)
    {
        this.saveLocation = saveLocation;
    }

    /**
     * @return the exportURL
     */
    public String getExportURL()
    {
        return exportURL;
    }

    /**
     * @param exportURL the exportURL to set
     */
    public void setExportURL(String exportURL)
    {
        this.exportURL = exportURL;
    }

    /**
     * @return the selectedSpaces
     */
    @SuppressWarnings("unchecked")
    public List getSelectedSpaces()
    {
        return selectedSpaces;
    }

    /**
     * @param selectedSpaces the selectedSpaces to set
     */
    @SuppressWarnings("unchecked")
    public void setSelectedSpaces(List selectedSpaces)
    {
        this.selectedSpaces = selectedSpaces;
    }

    /**
     * @return the selectedPages
     */
    @SuppressWarnings("unchecked")
    public List getSelectedPages()
    {
        return selectedPages;
    }

    /**
     * @param selectedPages the selectedPages to set
     */
    @SuppressWarnings("unchecked")
    public void setSelectedPages(List selectedPages)
    {
        this.selectedPages = selectedPages;
    }

    /**
     * @return the selectedDataManager
     */
    public DataManager getSelectedDataManager()
    {
        return selectedDataManager;
    }

    /**
     * @param selectedDataManager the selectedDataManager to set
     */
    public void setSelectedDataManager(DataManager selectedDataManager)
    {
        this.selectedDataManager = selectedDataManager;
    }

    /**
     * @return the selectedObjects
     */
    @SuppressWarnings("unchecked")
    public Set getSelectedObjects()
    {
        return selectedObjects;
    }

    /**
     * @param selectedObjects the selectedObjects to set
     */
    @SuppressWarnings("unchecked")
    public void setSelectedObjects(Set selectedObjects)
    {
        this.selectedObjects = selectedObjects;
    }

    /**
     * @return the dataManager
     * 
     * @see org.xwiki.eclipse.ui.wizards.states.ExportWizardState#setDataManager(DataManager)
     */
    public DataManager getDataManager()
    {
        return dataManager;
    }

    /**
     * Used when exporting a XAR archive in order to retrieve the user and password
     * for authentication.
     * 
     * @param dataManager the dataManager to set
     */
    public void setDataManager(DataManager dataManager)
    {
        this.dataManager = dataManager;
    }
}

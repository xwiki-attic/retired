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

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.xwiki.eclipse.core.CoreLog;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.XWikiEclipseException;
import org.xwiki.eclipse.core.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.core.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.ui.wizards.pages.XARSettingsPage;

/**
 * @author Eduard Moraru
 *
 */
public class ExportXARWizard extends AbstractExportWizard
{
    public ExportXARWizard()
    {
        setFormat("xar");
        setExtension("xar");
        setExpectedContentType("application/zip");
    }
    
    /* (non-Javadoc)
     * @see org.xwiki.eclipse.ui.wizards.AbstractExportWizard#addPages()
     */
    @Override
    public void addPages()
    {
        addPage(new XARSettingsPage("XAR Settings"));
    }

    /* (non-Javadoc)
     * @see org.xwiki.eclipse.ui.wizards.AbstractExportWizard#performFinish()
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean performFinish()
    {
        WizardPage currentPage = (WizardPage) getContainer().getCurrentPage();
        
        // Create the pages variables pointing out to all the pages defined by our selection
        String pagesUrlVariable = "";
        DataManager dataManager = exportWizardState.getSelectedDataManager();
        if (dataManager != null) {
            // Ignore the rest of the selections because the data manager does already contain them.
        } else {
            List<XWikiEclipseSpaceSummary> spaces = exportWizardState.getSelectedSpaces();
            List<XWikiEclipsePageSummary> pages = exportWizardState.getSelectedPages();
            
            for (XWikiEclipseSpaceSummary space : spaces) {
                try {
                    List<XWikiEclipsePageSummary> pagesInThisSpace = space.getDataManager().getPages(space.getData().getKey());
                    for (XWikiEclipsePageSummary page : pagesInThisSpace) {
                        pagesUrlVariable += "&pages=" + page.getData().getId();
                    }
                } catch (XWikiEclipseException e) {
                    CoreLog.logError("Error while getting pages from a space.", e);
                    currentPage.setErrorMessage("An error has occured while initializing the export process: " + e.getMessage());
                    return false;
                }
            }
            
            for (XWikiEclipsePageSummary page : pages) {
                pagesUrlVariable += "&pages=" + page.getData().getId();
            }
        }
        
        try {
            doExport(pagesUrlVariable);
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
}

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

import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.core.model.XWikiEclipseSpaceSummary;
import org.xwiki.eclipse.ui.wizards.pages.HTMLSettingsPage;

/**
 * @author Eduard Moraru
 *
 */
public class ExportHTMLWizard extends AbstractExportWizard
{
    public ExportHTMLWizard()
    {
        setFormat("html");
        setExtension("zip");
        setExpectedContentType("application/zip");
    }
    
    /* (non-Javadoc)
     * @see org.xwiki.eclipse.ui.wizards.AbstractExportWizard#addPages()
     */
    @Override
    public void addPages()
    {
        addPage(new HTMLSettingsPage("HTML Settings"));
    }

    /* (non-Javadoc)
     * @see org.xwiki.eclipse.ui.wizards.AbstractExportWizard#performFinish()
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean performFinish()
    {   
        // Create the pages variables pointing out to all the pages defined by our selection
        String pagesUrlVariable = "";
        DataManager dataManager = exportWizardState.getSelectedDataManager();
        if (dataManager != null) {
            pagesUrlVariable += "&pages=%25";
        } else {
            List<XWikiEclipseSpaceSummary> spaces = exportWizardState.getSelectedSpaces();
            List<XWikiEclipsePageSummary> pages = exportWizardState.getSelectedPages();
            
            for (XWikiEclipseSpaceSummary space : spaces) {
                pagesUrlVariable += "&pages=" + space.getData().getKey() + ".%25";
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

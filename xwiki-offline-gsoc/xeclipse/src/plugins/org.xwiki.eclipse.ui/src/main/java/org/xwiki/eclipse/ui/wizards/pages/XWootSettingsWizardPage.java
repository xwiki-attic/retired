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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;
import org.xwiki.eclipse.ui.wizards.NewConnectionWizard;
import org.xwiki.eclipse.ui.wizards.states.NewConnectionWizardState;

public class XWootSettingsWizardPage extends WizardPage
{
    private NewConnectionWizardState newConnectionWizardState;

    private Text xwootEndpointText;

    private Button xwootAutosynchEnabledButton;
    
    public XWootSettingsWizardPage(String pageName)
    {
        super(pageName);
        setTitle("XWiki P2P settings (optional)");
        setImageDescriptor(UIPlugin.getImageDescriptor(UIConstants.CONNECTION_SETTINGS_BANNER));
    }

    public void createControl(Composite parent)
    {
        newConnectionWizardState = ((NewConnectionWizard) getWizard()).getNewConnectionWizardState();

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(composite);

        /* XWoot Url */
        Label label = new Label(composite, SWT.NONE);
        label.setText("XWoot URL:");

        xwootEndpointText = new Text(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(xwootEndpointText);
        xwootEndpointText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {                
                newConnectionWizardState.setXwootUrl(xwootEndpointText.getText());
                getContainer().updateButtons();
            }
        });
        newConnectionWizardState.setXwootUrl("");
        xwootEndpointText.setToolTipText("Example: http://localhost:8080/xwootApp");
        
        /* layout spacer */
        label = new Label(composite, SWT.NONE);

        /* Autosynch */
        xwootAutosynchEnabledButton = new Button(composite, SWT.CHECK);
        xwootAutosynchEnabledButton.setText("Auto synchronize");
        xwootAutosynchEnabledButton.setToolTipText("Wether to synchronize your work with the P2P network automatically.");

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(xwootAutosynchEnabledButton);
        xwootAutosynchEnabledButton.addSelectionListener(new SelectionListener()
        {
            public void widgetSelected(SelectionEvent e)
            {
                newConnectionWizardState.setXwootAutosynch(xwootAutosynchEnabledButton.getSelection());
                getContainer().updateButtons();
            }

            public void widgetDefaultSelected(SelectionEvent e)
            {
                // empty method
            }
        });

        setControl(composite);
    }

    @Override
    public boolean isPageComplete()
    {        
        /* XWoot URL */
        String xwootUrl = xwootEndpointText.getText().trim();
        try{
            if (xwootUrl.length() != 0 && new URL(xwootUrl).getHost().equals("")){
                throw new MalformedURLException("Invalid hostname.");
            }
        }catch(MalformedURLException me){
            setErrorMessage("The specified address is not a valid URL.");
            return false;
        }

        /* Clear error messages if everything's fine. */
        setErrorMessage(null);

        return true;
    }
}

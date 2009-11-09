package org.xwiki.eclipse.ui.properties;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.ui.utils.UIUtils;
import org.xwiki.eclipse.ui.utils.XWikiEclipseSafeRunnable;

public class P2PPropertiesPage extends PropertyPage
{
    private DataManager dataManager;

    private Text xwootEndpointText;
    
    private Button autoSynchronizeCheck;
    
    public final String defaultMessage = "No xwoot endpoint defined.";
    
    @Override
    protected Control createContents(Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);

        SafeRunner.run(new XWikiEclipseSafeRunnable()
        {
            public void run() throws Exception
            {
                dataManager = (DataManager) getElement().getAdapter(DataManager.class);

                Label label = new Label(composite, SWT.BORDER);
                label.setText("XWoot Endpoint:");
                xwootEndpointText = new Text(composite, SWT.BORDER);
                GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(xwootEndpointText);
                String endpoint = dataManager.getXwootEndpoint();
                xwootEndpointText.setText(endpoint != null ? endpoint : defaultMessage);
                xwootEndpointText.setToolTipText("Example: http://www.example.com:8080/xwootApp");

                label = new Label(composite, SWT.None);
                autoSynchronizeCheck = new Button(composite, SWT.CHECK);
                autoSynchronizeCheck.setText("Auto synchronize");
                GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(autoSynchronizeCheck);
                autoSynchronizeCheck.setSelection(dataManager.isXwootAutosynchEnabled());
                autoSynchronizeCheck.setToolTipText("Wether to synchronize your work with the P2P network automatically.");
            }

        });
        return composite;
    }
    
    @Override
    public boolean performOk()
    {
        String enteredXwootEndpoint = xwootEndpointText.getText().trim();
        
        try{
            if (enteredXwootEndpoint.equals("") || enteredXwootEndpoint.equals(defaultMessage)){
                dataManager.setXwootEndpoint(null);
            }
            else{
                // test if the url is valid
                if (new URL(enteredXwootEndpoint).getHost().equals(""))
                    throw new MalformedURLException("Invalid hostname.");
                
                dataManager.setXwootEndpoint(enteredXwootEndpoint);
            }
            
            dataManager.setXwootAutosynchEnabled(autoSynchronizeCheck.getSelection());
            
        }catch(MalformedURLException me){
            UIUtils.showMessageDialog(Display.getDefault().getActiveShell(), "Invalid URL",
                "The entered XWoot endpoit is invalid:\n" +
                " " + me.getMessage());
            return false;
        }catch(CoreException ce){
            UIUtils.showMessageDialog(Display.getDefault().getActiveShell(), "Error",
                "There was an error while saving a value:\n" + 
                " " + ce.getMessage());
        }

        return true;
    }
    
    protected void performDefaults()
    {
        SafeRunner.run(new XWikiEclipseSafeRunnable()
        {
            public void run() throws Exception
            {
                String xwootEndpoint = dataManager.getXwootEndpoint();
                xwootEndpointText.setText(xwootEndpoint != null ? xwootEndpoint : defaultMessage);
                autoSynchronizeCheck.setSelection(dataManager.isXwootAutosynchEnabled());
            }
        });
    }

}


package com.xpn.xwiki.wiked.internal.ui.action;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.RepositoryView;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;
import com.xpn.xwiki.wiked.repository.IRepositoryManager;
import com.xpn.xwiki.wiked.repository.RepositoryDescriptor;

public class AddRepositoryAction extends RepositoryViewAction {

    private RepositoryDescriptor descriptor;
    private IWorkbenchWindow window;
    
    public AddRepositoryAction(RepositoryView view, RepositoryDescriptor descriptor) {
        super(view, descriptor.getName());
        this.descriptor = descriptor;
        ImageDescriptor image = descriptor.getIcon();
        setImageDescriptor((image != null) ? image : 
            WikedPlugin.getInstance().getImageRegistry().getDescriptor(
                WikedImageRegistry.ADD_REPOSITORY));
        setEnabled(true);
    }
    
	public void run() {
        IRepositoryManager manager = WikedPlugin.getInstance().getRepositoryManager();
        String factoryType = this.descriptor.getType();
        try {
            IWizard wizard = this.descriptor.getConnectionWizard();
    		WizardDialog dialog = new WizardDialog(getShell(), wizard);
            dialog.create();
            dialog.setTitle("New Repository");
            dialog.open();
        } catch (Exception ex) {
        	WikedPlugin.handleError("cannot conenct to repository", ex);
        }
	}

    protected Shell getShell() {
        return (window != null) ? window.getShell() : super.getShell();
    }

}

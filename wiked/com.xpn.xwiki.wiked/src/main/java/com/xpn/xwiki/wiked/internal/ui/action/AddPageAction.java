
package com.xpn.xwiki.wiked.internal.ui.action;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.NewPageWizard;
import com.xpn.xwiki.wiked.internal.ui.RepositoryView;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;
import com.xpn.xwiki.wiked.repository.ISpace;

public class AddPageAction extends RepositoryViewAction {

    public AddPageAction(RepositoryView view, String label) {
        super(view, label);
        setImageDescriptor(WikedPlugin.getInstance().getImageRegistry().
             getDescriptor(WikedImageRegistry.ADD_PAGE));
        setEnabled(false);
    }

    public void selectionChanged(IStructuredSelection selection) {
		super.selectionChanged(selection);
        setEnabled(selection.size() == 1 && 
            selection.getFirstElement() instanceof ISpace);
	}
    
	public void run() {
        ISpace space = (ISpace)getStructuredSelection().getFirstElement();
        NewPageWizard wizard = new NewPageWizard(space);
        WizardDialog dialog = new WizardDialog(getShell(), wizard);
        dialog.create();
        dialog.setTitle("New Page");
        dialog.open();
	}

}

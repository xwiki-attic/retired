
package com.xpn.xwiki.wiked.internal.ui.action;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.NewSpaceWizard;
import com.xpn.xwiki.wiked.internal.ui.RepositoryView;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;
import com.xpn.xwiki.wiked.repository.IRepository;

public class AddSpaceAction extends RepositoryViewAction {

    public AddSpaceAction(RepositoryView view, String label) {
        super(view, label);
        setImageDescriptor(WikedPlugin.getInstance().getImageRegistry().
             getDescriptor(WikedImageRegistry.ADD_SPACE));
        setEnabled(false);
    }

    public void selectionChanged(IStructuredSelection selection) {
        super.selectionChanged(selection);
        setEnabled(selection.size() == 1 && 
            selection.getFirstElement() instanceof IRepository);
    }
    
	public void run() {
        IRepository repository = (IRepository)getStructuredSelection().getFirstElement();
        NewSpaceWizard wizard = new NewSpaceWizard(repository);
        WizardDialog dialog = new WizardDialog(getShell(), wizard);
        dialog.create();
        dialog.setTitle("New Space");
        dialog.open();
	}

}

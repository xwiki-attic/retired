
package com.xpn.xwiki.wiked.internal.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.RepositoryView;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;

public class RefreshAction extends Action {

    public RefreshAction(String label) {
        setText(label);
        setImageDescriptor(WikedPlugin.getInstance().getImageRegistry().
             getDescriptor(WikedImageRegistry.REFRESH));
    }

    public void run() {
		try {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			RepositoryView view = (RepositoryView)page.showView(RepositoryView.ID);
			view.refresh();
		} catch (PartInitException ex) {
			IllegalStateException isex = new IllegalStateException();
            isex.initCause(ex);
            throw isex;
        }
	}

}


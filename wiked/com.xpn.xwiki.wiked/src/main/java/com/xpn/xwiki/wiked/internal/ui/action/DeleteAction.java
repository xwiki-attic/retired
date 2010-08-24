
package com.xpn.xwiki.wiked.internal.ui.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.RepositoryView;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;
import com.xpn.xwiki.wiked.repository.IPage;
import com.xpn.xwiki.wiked.repository.IRepository;
import com.xpn.xwiki.wiked.repository.IRepositoryManager;
import com.xpn.xwiki.wiked.repository.ISpace;
import com.xpn.xwiki.wiked.repository.RepositoryException;

public class DeleteAction extends RepositoryViewAction {

    private IRepositoryManager bookmarkManager;
    private Object object;
    
    public DeleteAction(RepositoryView view, String label) {
        super(view, label);
        this.bookmarkManager = WikedPlugin.getInstance().getRepositoryManager();
        setImageDescriptor(WikedPlugin.getInstance().getImageRegistry().
             getDescriptor(WikedImageRegistry.DELETE));
        setEnabled(false);
    }

    public void selectionChanged(IStructuredSelection selection) {
        super.selectionChanged(selection);
        this.object = selection.getFirstElement();
        setEnabled(selection.size() == 1 && 
            (this.object instanceof IRepository ||
            this.object instanceof ISpace ||
            this.object instanceof IPage));
    }
    
    public void run() {
        if (object instanceof IRepository) {
        	deleteRepository();
        } else if (object instanceof ISpace) {
        	deleteSpace();
        } else if (object instanceof IPage) {
        	deletePage();
        }
    }

	/**
	 * Deletes selected repository
	 */
	private void deleteRepository() {
		if (MessageDialog.openQuestion(getShell(), "Delete Repository", 
            "Do you want to delete the repository?")) {
        	bookmarkManager.removeRepository(((IRepository)object).getName());
        }
	}

    /**
     * Deletes selected repository
     */
    private void deleteSpace() {
        if (MessageDialog.openQuestion(getShell(), "Delete Space", 
            "Do you want to delete the space?")) {
            try {
                ISpace space = (ISpace)object;
                space.getRepository().removeSpace(space.getName());
            } catch (RepositoryException ex) {
            	WikedPlugin.handleError("Cannot remove space", ex);
            }
        }
    }

    /**
     * Deletes selected repository
     */
    private void deletePage() {
        if (MessageDialog.openQuestion(getShell(), "Delete Page", 
            "Do you want to delete the page?")) {
            try {
                IPage page = (IPage)object;
                page.getSpace().removePage(page.getName());
            } catch (RepositoryException ex) {
                WikedPlugin.handleError("Cannot remove space", ex);
            }
        }
    }
}    

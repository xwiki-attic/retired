
package com.xpn.xwiki.wiked.internal.ui.action;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.RepositoryView;

/**
 * Shows wiki repository for selected IProject
 * 
 * @author psenicka_ja
 */
public class ShowWikiAction implements IObjectActionDelegate {

	private ISelection selection;

	public final void setActivePart(IAction aaction, IWorkbenchPart part) {
	}

	public final void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	public final void run(IAction action) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			Iterator iter = sSelection.iterator();
            while (iter.hasNext()) {
                Object element = iter.next();
                if (element instanceof IProject) {
                    try {
                        IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getActivePage();
                        workbenchPage.showView(RepositoryView.ID);
                        return;
                    } catch (PartInitException ex) {
                        WikedPlugin.handleError("Cannot open view", ex);
                    }
                }
            }
        }
	}
}

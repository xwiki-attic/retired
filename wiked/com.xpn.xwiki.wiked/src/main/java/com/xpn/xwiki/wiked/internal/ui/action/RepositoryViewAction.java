
package com.xpn.xwiki.wiked.internal.ui.action;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;

import com.xpn.xwiki.wiked.internal.ui.RepositoryView;

/**
 * Superclass of all actions provided by the resource navigator.
 */
public abstract class RepositoryViewAction extends SelectionProviderAction {
	
	private RepositoryView view;

	/**
	 * Creates a new instance of the class.
	 */
	public RepositoryViewAction(RepositoryView view, String label) {
		super(view.getViewer(), label);
		this.view = view;
	}

	/**
	 * Returns the resource navigator for which this action was created.
	 */
	public RepositoryView getView() {
		return view;
	}

	/**
	 * Returns the resource viewer
	 */
	protected Viewer getViewer() {
		return getView().getViewer();
	}

	/**
	 * Returns the shell to use within actions.
	 */
	protected Shell getShell() {
		return getView().getSite().getShell();
	}

	/**
	 * Returns the workbench.
	 */
	protected IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}

	/**
	 * Returns the workbench window.
	 */
	protected IWorkbenchWindow getWorkbenchWindow() {
		return getView().getSite().getWorkbenchWindow();
	}
}

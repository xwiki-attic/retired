
package com.xpn.xwiki.wiked.internal.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.xpn.xwiki.wiked.internal.WikedPlugin;

/**
 * An action able to handle the IWorkbenchWindowActionDelegate
 */
public class WorkbenchWindowAction extends Action {

    private IWorkbenchWindow workbenchWindow;
    private IWorkbenchWindowActionDelegate delegate;
    
    public WorkbenchWindowAction(IWorkbenchWindowActionDelegate delegate) {
		this(null, delegate);
	}

    public WorkbenchWindowAction(IWorkbenchWindow workbenchWindow, 
        IWorkbenchWindowActionDelegate delegate) {
        this.workbenchWindow = workbenchWindow;
        this.delegate = delegate;
    }

	public void run() {
        IWorkbenchWindow window = (this.workbenchWindow != null) ?
            this.workbenchWindow : WikedPlugin.getActiveWorkbenchWindow();
        this.delegate.init(window);
        this.delegate.run(this);
	}

    public void dispose() {
        workbenchWindow = null;
        delegate.dispose();
    }
}

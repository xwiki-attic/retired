
package com.xpn.xwiki.wiked.internal.ui.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.wikip.INavigable;

/** 
 * Outline page object.
 * The ouline takes all INavigable objects and navigates to appropriate
 * line number when double-clicked.
 * @see com.xpn.xwiki.wiked.internal.wikip.INavigable
 */
public class WikiMarkupOutline extends ContentOutlinePage {

    private IEditorInput input;
    
    public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
        WikiOutlineProvider outlineProvider = new WikiOutlineProvider();
		viewer.setContentProvider(outlineProvider);
		viewer.setLabelProvider(outlineProvider);
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object adapter = selection.getFirstElement();
                if (adapter instanceof INavigable) {
                	navigateToEditor(((INavigable)adapter).getLineNumber());
                }
			}
		});
        if (input != null) {
        	viewer.setInput(input);
            viewer.expandAll();
        }
	}
    
    public void setEditorInput(IEditorInput input) {
        TreeViewer tree = getTreeViewer();
        if (tree != null) {
            getTreeViewer().setInput(input);
            getTreeViewer().expandAll();
        } else {
        	this.input = input;
        }
    }
    
	private void navigateToEditor(int line) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart part = page.getActiveEditor();
        INavigableEditor editor = (INavigableEditor)part.getAdapter(INavigableEditor.class);
        if (editor != null) {
            try {
                editor.navigateTo(line);
            } catch (BadLocationException ex) {
                WikedPlugin.logError("failed to navigate to editor line from content outline tree", ex);
            }
        }
	}

}

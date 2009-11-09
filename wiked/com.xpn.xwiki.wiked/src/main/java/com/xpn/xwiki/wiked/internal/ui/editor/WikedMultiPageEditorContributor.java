package com.xpn.xwiki.wiked.internal.ui.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.xpn.xwiki.wiked.internal.ui.action.RefreshAction;

/**
 * Manages the installation/deinstallation of global actions for multi-page
 * editors. Responsible for the redirection of global actions to the active
 * editor. Multi-page contributor replaces the contributors for the individual
 * editors in the multi-page editor.
 */
public class WikedMultiPageEditorContributor 
    extends MultiPageEditorActionBarContributor {

	private IEditorPart activeEditorPart;
//	private FormatTextContentAction formatContentAction;
	private RefreshAction refreshAction;
    
	public WikedMultiPageEditorContributor() {
//        this.formatContentAction = new FormatTextContentAction();
        this.refreshAction = new RefreshAction("Refresh");
	}

	/**
	 * Sets an active page and registers all actions
	 * AbstractMultiPageEditorActionBarContributor.
	 */
	public void setActivePage(IEditorPart part) {
		if (activeEditorPart == part) {
			return;
        }
		activeEditorPart = part;
		IActionBars actionBars = getActionBars();
		if (actionBars != null) {
			ITextEditor editor = (part instanceof ITextEditor) ? 
                (ITextEditor) part : null;
			actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				getAction(editor, ITextEditorActionConstants.DELETE));
			actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
				getAction(editor, ITextEditorActionConstants.UNDO));
			actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
				getAction(editor, ITextEditorActionConstants.REDO));
			actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(),
				getAction(editor, ITextEditorActionConstants.CUT));
			actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
				getAction(editor, ITextEditorActionConstants.COPY));
			actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(),
				getAction(editor, ITextEditorActionConstants.PASTE));
			actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(),
				getAction(editor, ITextEditorActionConstants.SELECT_ALL));
            actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(),
                getAction(editor, ITextEditorActionConstants.FIND));
            actionBars.setGlobalActionHandler(ActionFactory.REFRESH.getId(),
                this.refreshAction);
			actionBars.updateActionBars();
//			formatContentAction.setEditor(editor);
//			formatContentAction.update();
		}
	}

	public void contributeToMenu(IMenuManager manager) {
		IMenuManager editMenu = manager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null) {
			editMenu.add(new Separator());
//			editMenu.add(formatContentAction);
		}
	}

	public void contributeToToolBar(IToolBarManager manager) {
	}

    /**
     * Returns the action registed with the given text editor.
     * @return IAction or null if editor is null.
     */
    protected IAction getAction(ITextEditor editor, String actionID) {
        return (editor == null ? null : editor.getAction(actionID));
    }

}


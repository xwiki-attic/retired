
package com.xpn.xwiki.wiked.internal.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.DrillDownAdapter;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.action.AddPageAction;
import com.xpn.xwiki.wiked.internal.ui.action.AddRepositoryAction;
import com.xpn.xwiki.wiked.internal.ui.action.AddSpaceAction;
import com.xpn.xwiki.wiked.internal.ui.action.CollapseAllAction;
import com.xpn.xwiki.wiked.internal.ui.action.DeleteAction;
import com.xpn.xwiki.wiked.internal.ui.action.RefreshAction;
import com.xpn.xwiki.wiked.repository.IPage;
import com.xpn.xwiki.wiked.repository.IRepositoryManager;

public class MainActionGroup extends ActionGroup implements IShellProvider {

    private RepositoryView view;
    private DrillDownAdapter drillDownAdapter;
    
    private PropertyDialogAction propertyDialogAction;
    private CollapseAllAction collapseAllAction;
    private Action cutAction;
    private Action pasteAction;
    
    private Action refreshAction;
    private Action[] addRemoteRepositoryActions;
    private Action addSpaceAction;
    private Action addPageAction;
    private IAction newOtherAction;
    
    private OpenFileAction openFileAction;
    private Action deleteAction;

    public static final String OPEN_WITH_ID = PlatformUI.PLUGIN_ID+ 
        ".OpenWithSubMenu"; 

    public MainActionGroup(final RepositoryView view) {
        
        this.view = view;
    	Shell shell = view.getSite().getShell();
        this.drillDownAdapter = new DrillDownAdapter(view.getViewer());

        this.propertyDialogAction = new PropertyDialogAction(this, view.getViewer());
        this.collapseAllAction = new CollapseAllAction("Collapse All");
        
        this.refreshAction = new RefreshAction("Refresh");
        this.refreshAction.setImageDescriptor(WikedPlugin.getInstance().
            getImageRegistry().getDescriptor(WikedImageRegistry.REFRESH));
        
        IRepositoryManager mgr = WikedPlugin.getInstance().getRepositoryManager();
        String[] rtypes = mgr.getRepositoryTypes();
        this.addRemoteRepositoryActions = new AddRepositoryAction[rtypes.length];
        for (int i = 0; i < rtypes.length; i++) {
			this.addRemoteRepositoryActions[i] = 
                new AddRepositoryAction(view, mgr.getRepositoryDescriptor(rtypes[i]));
		}
                
        this.addSpaceAction = new AddSpaceAction(view, "Space");
        this.addSpaceAction.setImageDescriptor(WikedPlugin.getInstance().
            getImageRegistry().getDescriptor(WikedImageRegistry.ADD_SPACE));
        
        this.addPageAction = new AddPageAction(view, "Page");
        this.addPageAction.setImageDescriptor(WikedPlugin.getInstance().
            getImageRegistry().getDescriptor(WikedImageRegistry.ADD_PAGE));

        this.newOtherAction = ActionFactory.NEW.create(view.getSite().getWorkbenchWindow());
        this.deleteAction = new DeleteAction(view, "Delete");
        this.openFileAction = new OpenFileAction(view.getSite().getPage());
        this.cutAction = new Action() {
            public void run() {
                IStructuredSelection selection = (IStructuredSelection)
                    view.getViewer().getSelection();
                if (selection.size() > 0) {
                    cutAction.setEnabled(true); 
                }
            }};
        this.pasteAction = new Action() {
            public void run() {
                IStructuredSelection selection = (IStructuredSelection)
                    view.getViewer().getSelection();
                doDragAndDrop(selection, DND.DROP_MOVE);
                pasteAction.setEnabled(false);
            }
        };
        pasteAction.setEnabled(false);
    }

    public Shell getShell() {
    	return view.getSite().getShell();
    }
    
    public void fillContextMenu(IMenuManager menu, IStructuredSelection selection) {
        MenuManager subMenu = new MenuManager("New");
        menu.add(subMenu);
        for (int i = 0; i < this.addRemoteRepositoryActions.length; i++) {
        	subMenu.add(this.addRemoteRepositoryActions[i]);
		}
        subMenu.add(new Separator());
        subMenu.add(this.addSpaceAction);
        subMenu.add(this.addPageAction);
        subMenu.add(new Separator());
        subMenu.add(this.newOtherAction);
        openFileAction.selectionChanged(selection);
        menu.add(openFileAction);
        fillOpenWithMenu(menu, selection.getFirstElement());
        menu.add(new Separator());
        drillDownAdapter.addNavigationActions(menu);
        menu.add(new Separator());
        menu.add(this.deleteAction);
        menu.add(new Separator());
        menu.add(this.refreshAction); 
        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        menu.add(new Separator());
        menu.add(this.propertyDialogAction);
    }

    /**
     * Adds the actions in this group and its subgroups to the action bars.
     */
    public void fillActionBars(IActionBars actionBars) {
        actionBars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), propertyDialogAction);
            
        IToolBarManager toolBar = actionBars.getToolBarManager();
        drillDownAdapter.addNavigationActions(toolBar);
        toolBar.add(new Separator());
        toolBar.add(this.collapseAllAction);     
        toolBar.add(this.refreshAction);     
        
        IMenuManager menu = actionBars.getMenuManager();
        menu.add(new Separator());
        menu.add(this.refreshAction);
    }

    /**
     * Updates the actions which were added to the action bars,
     * delegating to the subgroups as necessary.
     */
    public void updateActionBars() {
        IStructuredSelection selection =
            (IStructuredSelection) getContext().getSelection();
        propertyDialogAction.setEnabled(selection.size() == 1);
    } 
    
    /**
	 * @see org.eclipse.ui.actions.ActionGroup#dispose()
	 */
	public void dispose() {
        propertyDialogAction.dispose();
		super.dispose();
	}

    /**
     * Runs the default action (open file).
     */
    public void runDefaultAction(IStructuredSelection selection) {
        Object element = selection.getFirstElement();
        if (element instanceof IPage) {
            openFileAction.selectionChanged(selection);
            openFileAction.run();
        }
    }

    private void fillOpenWithMenu(IMenuManager menu, Object object) {
        if (object instanceof IAdaptable) {
            MenuManager submenu = new MenuManager("Open With", OPEN_WITH_ID);
            submenu.add(new OpenWithMenu(view.getSite().getPage(), (IAdaptable)object));
            menu.add(submenu);
        }
    }

    public void registerMenu(IMenuManager menu) {
        view.getViewSite().getActionBars().setGlobalActionHandler(
            ActionFactory.CUT.getId(), this.cutAction);
        view.getViewSite().getActionBars().setGlobalActionHandler(
            ActionFactory.PASTE.getId(), this.pasteAction);
    }
    
    protected void doDragAndDrop(IStructuredSelection sel, int oper) {
// TODO implement drag and drop        
//        TreeAdapter[] adapters = selectionToAdapters(srcAdapters);
//        // no droping into a selected node
//        for (int i = 0; i < adapters.length; i++) {
//            TreeAdapter adapter = adapters[i];
//            if (adapter == targetAdapter) {
//                MessageDialog.openInformation(getSite().getShell(),
//                        "Invalid Page Tranfer",
//                        "Same Page Can Not Be Both Target And Source");
//                return false;
//            }
//
//        }
//
//        StringBuffer title = new StringBuffer("Selecting OK Will ");
//        title.append(operation == DND.DROP_COPY ? "Copy" : "Move");
//        title.append(" The Following Pages To : ");
//        title.append(targetAdapter.getText());
//        ListSelectionDialog dialog = new ListSelectionDialog(getSite()
//                .getShell(), adapters, new ArrayContentProvider(), 
//                treeLabelProvider, title.toString());
//        dialog.setInitialSelections(adapters);
//        if (dialog.open() == Window.OK) {
//            final Object[] pagesToCopy = dialog.getResult();
//            ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(
//                    getSite().getShell());
//            try {
//                progressMonitor.run(true, false, new IRunnableWithProgress() {
//                    public void run(IProgressMonitor monitor)
//                            throws InvocationTargetException,
//                            InterruptedException {
//                        model.transferPages(targetAdapter, pagesToCopy,
//                                operation == DND.DROP_MOVE, monitor);
//                    }
//                });
//            } catch (Exception e) {
//                GUIUtil.reportException("Tranfering Pages Failed", e);
//                return false;
//            }
//            viewer.refresh();
//            viewer.setSelection(new StructuredSelection(targetAdapter), true);
//            return true;
//        }
//        return false;
    }

//        List actionList = new ArrayList();
//        Action action = new Action() {
//            public void run() {
//                openPage();
//            }
//        };
//        action.setText("Edit");
//        action.setImageDescriptor(plugin.loadImageDescriptor(TimTamPlugin.IMG_EDIT_PAGE));
//        actionList.add(action);
//        action = new Action() {
//            public void run() {
//                deletePage();
//            }
//        };
//        action.setText("Delete ...");
//        action.setToolTipText("Deletes The Selected Pages");
//        action.setImageDescriptor(plugin
//                .loadImageDescriptor(TimTamPlugin.IMG_DEL_PAGE));
//        actionList.add(action);
//        action = new Action() {
//            public void run() {
//                addChildPage();
//            }
//        };
//        action.setText("Add Child Page...");
//        action.setToolTipText("Adds a Child Page To The Selected Page");
//        action.setImageDescriptor(plugin
//                .loadImageDescriptor(TimTamPlugin.IMG_ADD_CHILD_PAGE));
//        actionList.add(action);
//        action = new Action() {
//            public void run() {
//                renamePage();
//            }
//        };
//        action.setText("Rename Page...");
//        action.setToolTipText("Rename The Selected Page");
//        action.setImageDescriptor(plugin
//                .loadImageDescriptor(TimTamPlugin.IMG_RENAME_PAGE));
//        // rename is disabled due to (another ) bug in conf CONF-974
//        actionList.add(action);
//        return actionList;
    
}
    

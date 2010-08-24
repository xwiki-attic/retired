
package com.xpn.xwiki.wiked.internal.ui;

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.action.RefreshAction;
import com.xpn.xwiki.wiked.internal.ui.editor.PageEditor;
import com.xpn.xwiki.wiked.internal.ui.editor.PageEditorInput;
import com.xpn.xwiki.wiked.repository.IPage;
import com.xpn.xwiki.wiked.repository.IRepository;
import com.xpn.xwiki.wiked.repository.IRepositoryListener;
import com.xpn.xwiki.wiked.repository.IRepositoryManager;
import com.xpn.xwiki.wiked.repository.IRepositoryObject;
import com.xpn.xwiki.wiked.repository.ISpace;
import com.xpn.xwiki.wiked.repository.RepositoryException;
import com.xpn.xwiki.wiked.repository.RepositoryObjectChangeEvent;

/** 
 * A view displaying tree of repositories, spaces and pages. 
 * @author psenicka_ja
 */
public class RepositoryView extends ViewPart {

    /** The main viewer component */
    private TreeViewer viewer;
    /** The model */
    private IRepositoryManager model;    
    /** Actions */
    private MainActionGroup mainActionGroup;
    
    /** View ID referred in plugin.xml */
    public final static String ID = "com.xpn.xwiki.wiked.RepositoryView";
    
    /** Creates the view, menu and toolbars */
    public void createPartControl(Composite parent) {
        this.model = WikedPlugin.getInstance().getRepositoryManager();
        this.viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		this.viewer.setContentProvider(new ContentProvider());
		this.viewer.setLabelProvider(new WorkbenchLabelProvider());
        this.viewer.setSorter(new ViewerSorter());
        this.viewer.setInput(model);
        this.mainActionGroup = new MainActionGroup(this);
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                mainActionGroup.fillContextMenu(manager, selection);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
        hookGlobalActions();
        hookDoubleClickAction();
        IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();
        this.mainActionGroup.fillActionBars(bars);
        WikedPlugin.getInstance().getRepositoryManager().addChangeListener(
        	new IRepositoryListener() {
        		public void repositoryChanged(RepositoryObjectChangeEvent event) {
        			refresh(event);
        		}
        	}
        );
        
        getSite().setSelectionProvider(viewer);
    }

    /** Forward the focus call to viewer */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /** Provide the viewer component */
    public TreeViewer getViewer() {
        return viewer;
    }

    /** Iterate over all selected nodes and perform refresh */
    public void refresh() {
        IRepositoryObject[] selectedNodes = getSelectedObjects();
        try {
            if (selectedNodes.length > 0) {
                for (int i = 0; i < selectedNodes.length; i++) {
                	try {
                		selectedNodes[i].refresh();
			        } catch (Exception ex) {
			        	WikedPlugin.logError("model refresh problem", ex);
			        }
                }
            } else {
            	try {
            		model.refresh();
		        } catch (Exception ex) {
		        	WikedPlugin.logError("model refresh problem", ex);
		        }
            } 
            this.viewer.refresh(true);
        } catch (Exception ex) {
        	WikedPlugin.logError("viewer refresh problem", ex);
        }
    }

    /**
     * Refreshes changed object only
     * @param event the event describes changed data
     */
    private void refresh(final RepositoryObjectChangeEvent event) {
		getSite().getShell().getDisplay().asyncExec (new Runnable () {
		    public void run () {
	    		IRepositoryObject objectToRefresh = null;
		    	try {
		    		int type = event.getEventType();
		    		if (RepositoryObjectChangeEvent.ERROR != type) {
			    		objectToRefresh = (IRepositoryObject) (
			    			RepositoryObjectChangeEvent.MODIFY == type ? 
			    			(IRepositoryObject)event.getSource() : event.getParentObject());
			    		if (objectToRefresh != null) {
			    			objectToRefresh.refresh();
			    		} else {
			    			model.refresh();
			    		}
		    		}
			    	viewer.refresh(objectToRefresh, true);
		    	} catch (Exception ex) {
		    		WikedPlugin.handleError("Cannot refresh "+
		    			objectToRefresh.getName(), ex);
		    	}
		    }
		});
	}
        
    private IRepositoryObject[] getSelectedObjects() {
        IStructuredSelection selection = 
            (IStructuredSelection) viewer.getSelection();
        IRepositoryObject nodes[] = new IRepositoryObject[selection.size()];
        Iterator iterator = selection.iterator();
        for (int i=0; iterator.hasNext(); i++) {
            nodes[i] = (IRepositoryObject)iterator.next();
        }
        return nodes;
    }

    private IRepositoryObject getFirstSelectedObject() {
        ISelection selection = viewer.getSelection();
        Object node = ((IStructuredSelection) selection).getFirstElement();
        return (IRepositoryObject)node;
    }
    
    private void hookGlobalActions() {
    	IActionBars bars = getViewSite().getActionBars();
        bars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), new RefreshAction("Refresh"));
    }
    
    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                final IRepositoryObject adapter = getFirstSelectedObject();
                if (adapter instanceof IRepository) {
    	            ProgressMonitorDialog dialog = new ProgressMonitorDialog(null);
    	            try {
    	            	dialog.run(true, false, new IRunnableWithProgress() {
                            public void run(IProgressMonitor monitor) {
                                try {
                                	((IRepository)adapter).getSpaces(monitor);
                                } catch (RepositoryException ex) {
                                	WikedPlugin.logError("cannot read spaces", ex);
                                }
                            }
    	            	});
    	            } catch (final Exception ex) {
    	            	WikedPlugin.handleError("Cannot load spaces", ex);
    	            }
                    viewer.setInput(model);
                    viewer.expandToLevel(2);
                } else if (adapter instanceof ISpace) {
                    ProgressMonitorDialog dialog = new ProgressMonitorDialog(null);
                    try {
                        dialog.run(true, false, new IRunnableWithProgress() {
                            public void run(IProgressMonitor monitor) {
                               	((ISpace)adapter).getPages(monitor);
                            }
                        });
                    } catch (final Exception ex) {
                        WikedPlugin.handleError("Cannot load pages", ex);
                    }
                    viewer.setInput(model);
                    viewer.expandToLevel(2);
                } else if (adapter instanceof IPage) {
                    IRepositoryObject[] selectedNodes = getSelectedObjects();
                    for (int i = 0; i < selectedNodes.length; i++) {
                        openEditor((IPage)selectedNodes[i], PageEditor.ID);
                    }
                }
            }
        });
    }

    private void openEditor(IPage object, String editorId) {
        try {
            IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();
            workbenchPage.setEditorAreaVisible(true);
            workbenchPage.openEditor(new PageEditorInput(object), editorId);
        } catch (WorkbenchException ex) {
            WikedPlugin.logError("failed to open an editor", ex);
        }
    }

    private static class ContentProvider extends BaseWorkbenchContentProvider {
        
		public boolean hasChildren(Object element) {
            if (element instanceof IRepository) {
            	return true;
            } else if (element instanceof ISpace) {
                return true;
            } else if (element instanceof IPage) {
                return false;
            } else {
            	return super.hasChildren(element);
            }
		}
        
        public Object[] getChildren(Object element) {
            if (element instanceof IRepositoryManager) {
                return ((IRepositoryManager)element).getRepositories();
            } 
    
            return super.getChildren(element); 
        }
    
    }    
}
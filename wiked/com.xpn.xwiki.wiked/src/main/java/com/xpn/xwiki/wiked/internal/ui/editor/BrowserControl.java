package com.xpn.xwiki.wiked.internal.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;

/**
 * HTML browser control
 * @author psenicka_ja
 */
public class BrowserControl extends Composite {

	private Browser browser;
	private Action back;
	private Action forward;
	private Action stop;
	private Action refresh;
	private int navigationCount;
	private boolean backPressed;

	public BrowserControl(Composite parent, int style, IActionBars actionBars) {
		super(parent, style);
		createControls(actionBars);
	}

    /** Set txt to be displayed */
    public void setText(String text) {
        browser.setText(text);
        updateNavigationStatus();
    }

	private void createControls(final IActionBars actionBars) {
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 3;
		formLayout.marginWidth = 3;
		setLayout(formLayout);
		ToolBarManager manager = new ToolBarManager(SWT.FLAT);
		createBrowserToolBar(manager);

		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = GridData.CENTER;
		gd.horizontalAlignment = GridData.FILL;
		ToolBar toolBar = manager.createControl(this);
		FormData data = new FormData();
		toolBar.setLayoutData(data);
		browser = new Browser(this, SWT.NONE);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(toolBar, 0, SWT.DEFAULT);
		data.bottom = new FormAttachment(100, 0);
		browser.setLayoutData(data);
		browser.addProgressListener(new BrowserListener(
            actionBars.getStatusLineManager().getProgressMonitor()));
		browser.addStatusTextListener(new StatusTextListener() {
			IStatusLineManager status = actionBars.getStatusLineManager();
			public void changed(StatusTextEvent event) {
				status.setMessage(event.text);
			}
		});
		browser.addLocationListener(new LocationAdapter() {
			public void changed(LocationEvent event) {
				updateNavigationStatus();
			}
		});
	}

	private void updateNavigationStatus() {
		back.setEnabled(navigationCount > 0);
		forward.setEnabled(browser.isForwardEnabled());
	}

	private void createBrowserToolBar(ContributionManager manager) {
        ImageRegistry imageRegistry = WikedPlugin.getInstance().getImageRegistry();
		this.back = new Action() {
			public void run() {
				backPressed = true;
				if (navigationCount > 1) {
					browser.back();
				}
				updateNavigationStatus();
			}
		};
		back.setToolTipText("Back");
		back.setImageDescriptor(imageRegistry.getDescriptor(
            WikedImageRegistry.BACK));
		back.setEnabled(false);
		manager.add(back);
        
		forward = new Action() {
			public void run() {
				browser.forward();
				updateNavigationStatus();
			}
		};
		forward.setToolTipText("Forward");
        forward.setImageDescriptor(imageRegistry.getDescriptor(
            WikedImageRegistry.FORWARD));
		forward.setEnabled(false);
		manager.add(forward);
        
		stop = new Action() {
			public void run() {
				browser.stop();
				backPressed = false;
			}
		};
		stop.setToolTipText("Stop");
		stop.setEnabled(false);
        stop.setImageDescriptor(imageRegistry.getDescriptor(
            WikedImageRegistry.STOP));
		manager.add(stop);
        
		refresh = new Action() {
			public void run() {
				if (navigationCount == 0) {
//					try {
//						page.refresh();
//					} catch (RepositoryException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				} else {
					browser.refresh();
				}
			}
		};
		refresh.setToolTipText("Refresh");
        refresh.setImageDescriptor(imageRegistry.getDescriptor(
            WikedImageRegistry.REFRESH));
		manager.add(refresh);
		refresh.setEnabled(true);
		manager.update(false);
	}

    private class BrowserListener extends ProgressAdapter {
        
        private IProgressMonitor monitor;
        private boolean working;
        private int workedSoFar;

        public BrowserListener(IProgressMonitor monitor) {
            this.monitor = monitor;
        }
        
        public void changed(ProgressEvent event) {
            if (event.total == 0) {
                return;
            }
            if (!working) {
                if (event.current == event.total) {
                    return;
                }
                monitor.beginTask("", event.total); //$NON-NLS-1$
                workedSoFar = 0;
                working = true;
                stop.setEnabled(true);
            }

            monitor.worked(event.current - workedSoFar);
            workedSoFar = event.current;
        }

        public void completed(ProgressEvent event) {
            monitor.done();
            working = false;
            stop.setEnabled(false);
            if (backPressed) {
                navigationCount--;
                backPressed = false;
            } else {
                String url = browser.getUrl();
                if (url.startsWith("http")) {// we mive somewhere
                    navigationCount++;
                }
            }
            updateNavigationStatus();
        }
    }
}
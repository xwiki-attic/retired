
package com.xpn.xwiki.wiked.internal;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.xpn.xwiki.wiked.internal.repository.RepositoryManager;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;
import com.xpn.xwiki.wiked.repository.IRepositoryManager;

/**
 * The main plugin class to be used in the desktop.
 * Use static method getInstance() to get access.
 */
public class WikedPlugin extends AbstractUIPlugin {
	
	/** The plugin ID */
    public static final String ID = "com.xpn.xwiki.wiked";

    private static final String CUSTOM_TEMPLATES_KEY = 
        "com.xpn.xwiki.wiked.customtemplates";
    
    /** The shared instance */
    private static WikedPlugin instance;
    
    /** The resource bundle */
    private ResourceBundle resourceBundle;
    /** Configuration/preferences manager */
    private RepositoryManager bookmarkManager;
    /** Template store */
    private TemplateStore templateStore;
    /** Type registry */
    private ContextTypeRegistry ctxTypeRegistry;
    
    /**
     * @return the shared instance.
     */
    public static WikedPlugin getInstance() {
        return instance;
    }

    /**
     * @return the workspace instance.
     */
    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /**
     * @return the active wrkbench shell or <code>null</code> if there is
     * no active workbench window.
     */
    public static Shell getActiveWorkbenchShell() {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        return window != null ? window.getShell() : null;
    }

    /**
     * @return the active workbench window or <code>null</code> if there
     * is no such window.
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        final IWorkbenchWindow[] windows = new IWorkbenchWindow[1];
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                windows[0] = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            }
        });
        return windows[0];
    }

    /**
     * Logs exception and logs to system.err if debugging.
     */
    public static void log(String message) {
        synchronized (System.out) {
            System.out.println(message);
        }
        if (getInstance().isDebugging()) {
            Status status = new Status(IStatus.ERROR, WikedPlugin.ID, 
                IStatus.ERROR, String.valueOf(message), null);
            getInstance().getLog().log(status);
        }
    }

    /**
     * Logs exception and logs to system.err if debugging.
     */
    public static void logError(String message, Throwable exception) {
//        if (getInstance().isDebugging() && exception != null) {
            synchronized (System.err) {
                System.err.println(message);
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
//        } else {
            Status status = new Status(IStatus.ERROR, WikedPlugin.ID, 
                IStatus.ERROR, String.valueOf(message), exception);
            getInstance().getLog().log(status);
//        }
    }

    /**
     * Logs exception and shows a modal status dialog.
     */
    public static void handleError(Shell parent, String message, Throwable exception) {
        handleError(parent, message, createStatus(exception, null));
    }

    /**
     * Logs exception and shows a modal status dialog using the active workbench
     * window's shell.
     */
    public static void handleError(String message, Throwable exception) {
        handleError(getActiveWorkbenchShell(), message, createStatus(exception, null));
    }

    public static void handleError(final Shell parent, final String message, 
        final IStatus status) {
        logStatus(message, status);
        if (parent == null) {
            return;
        }
        final String msg = WikedPlugin.getInstance().getResourceString("error");
        parent.getDisplay().asyncExec(new Runnable() {
            public void run() {
                Shell shell = parent;
                if (shell == null || shell.isDisposed()) {
                    shell = getActiveWorkbenchShell();
                }
                if (shell == null) {
                    logError(message, status.getException());
                } else {
                    ErrorDialog.openError(shell, msg, message, 
                        createStatus(null, status));
                }
            }
        });
    }
    
    private static void logStatus(String message, IStatus status) {
        if (status.getException() != null)
            logError(message, status.getException());
        if (status.isMultiStatus()) {
            IStatus[] children = status.getChildren();
            for (int i = 0; i < children.length; i++) {
                logStatus(children[i].getMessage(), children[i]);
            }
        }
    }

    private static IStatus createStatus(Throwable exception, IStatus status) {
        MultiStatus multiStatus = new MultiStatus(WikedPlugin.ID, IStatus.ERROR,
            createExceptionMessage(exception, status), exception);
        String info = String.valueOf(exception);
        if (exception == null && status != null && status.isMultiStatus()) {
            info = ""; //$NON-NLS-1$
            IStatus[] children = status.getChildren();
            if (children.length > 1) {
                multiStatus.addAll(status);
                return multiStatus;
            }
            for (int i = 0; i < children.length; i++) {
                info += children[i].getMessage();
            }
        }
        StringTokenizer st = new StringTokenizer(info, "\r\n"); //$NON-NLS-1$
        while (st.hasMoreTokens()) {
            multiStatus.add(
                new Status(IStatus.ERROR, WikedPlugin.ID, IStatus.OK, 
                    st.nextToken(), null));
        }
        return multiStatus;
    }

    private static String createExceptionMessage(Throwable exception, IStatus status) {
        if (exception == null) {
            return status == null ? "unknown" : status.getMessage(); //$NON-NLS-1$
        }
        String message = exception.getMessage();
        if (message == null) {
            return exception.getClass().getName();
        }
        if (message.length() > 120) {
            message = message.substring(0, 120);
            int lastSpace = message.lastIndexOf(' ');
            if (lastSpace > 40)
                message = message.substring(0, lastSpace);
            message += "..."; //$NON-NLS-1$
        }
        return message;
    }

    /**
     * The constructor loads the resources from "/plugin.properties" file.
     * Logs an error if not found or loaded.
     */
    public WikedPlugin() {
        instance = this;
        try {
            this.resourceBundle = ResourceBundle.getBundle("/Wiked.properties");
        } catch (MissingResourceException ex) {
            this.resourceBundle = null;
        }
    }

    public ResourceBundle getResourceBundle() {
        return this.resourceBundle;
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not
     * found.
     */
    public String getResourceString(String key) {
        ResourceBundle bundle = this.resourceBundle;
        try {
            return (bundle != null ? bundle.getString(key) : key);
        } catch (MissingResourceException ex) {
            logError("Cannot load resource key '"+key+"'", ex);
            return key;
        }
    }

    /**
     * Returns the configuration manager instance.
     */
    public IRepositoryManager getRepositoryManager() {
        return this.bookmarkManager;
    }

    public TemplateStore getTemplateStore() {
        if (templateStore == null) {
            templateStore = new ContributionTemplateStore(getPreferenceStore(),
                CUSTOM_TEMPLATES_KEY);
            try {
                templateStore.load();
            } catch (IOException ex) {
                logError("failed to load temaplates", ex);
            }
        }
        return templateStore;
    }
    
    public ContextTypeRegistry getContextTypeRegistry() {
        if (ctxTypeRegistry == null) {
            ctxTypeRegistry = new ContextTypeRegistry();
            ctxTypeRegistry.addContextType(new WikiContextType());
        }
        return ctxTypeRegistry;
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        WikedImageRegistry.configure(getImageRegistry(), getBundle().getEntry("/"));
        this.bookmarkManager = new RepositoryManager();
    }

    public void stop(BundleContext context) throws Exception {
        this.bookmarkManager.shutdown();
        super.stop(context);
    }


}
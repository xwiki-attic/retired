package org.xwiki.appserver;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class TomcatPlugin extends Plugin {

	// The shared instance.
	private static TomcatPlugin plugin;

	public final static String PLUGIN_ID = "org.xwiki.appserver";

	/**
	 * Returns the shared instance.
	 */
	public static TomcatPlugin getDefault() {
		return plugin;
	}

	private WebServer fWebServer;

	/**
	 * The constructor.
	 */
	public TomcatPlugin() {
		plugin = this;
	}

	public void logError(String message, CoreException e) {
		if (message == null)
			message = ""; //$NON-NLS-1$
		Status errorStatus = new Status(IStatus.ERROR, "", IStatus.OK, message,
				e);
		getLog().log(errorStatus);
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		fWebServer = new WebServer(PLUGIN_ID, "webapps");
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		fWebServer.stop();
		fWebServer = null;
		plugin = null;
	}

	public WebServer getWebServer() {
		return fWebServer;
	}

}

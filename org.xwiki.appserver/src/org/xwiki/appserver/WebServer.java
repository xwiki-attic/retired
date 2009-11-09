/**
 * 
 */
package org.xwiki.appserver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.help.internal.appserver.AppserverPlugin;
import org.eclipse.help.internal.appserver.IWebappServer;
import org.eclipse.help.internal.appserver.WebappManager;

/**
 * @author kotelnikov
 * 
 */
public class WebServer {

	public interface IWebAppDescriptor {

		/**
		 * @return the name of the web-application to install
		 */
		String getName();

		/**
		 * @return the local path to the webapplication within the plugin
		 */
		IPath getPath();

		/**
		 * @return the name of the plugin containing a web application to run
		 */
		String getPluginName();
	}

	private final static IWebAppDescriptor[] EMPTY_LIST = {};

	private String fExtensionPointName;

	private String fPluginId;

	private boolean fRunning;

	private IWebAppDescriptor[] fInstalledApps;

	public WebServer(String pluginId, String extensionPointName) {
		fPluginId = pluginId;
		fExtensionPointName = extensionPointName;
	}

	protected IWebAppDescriptor[] getWebAppDescriptors() throws CoreException {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(fPluginId,
				fExtensionPointName); //$NON-NLS-1$
		if (extensionPoint == null)
			return EMPTY_LIST;
		IExtension[] extensions = extensionPoint.getExtensions();
		int len = (extensions != null) ? extensions.length : 0;
		if (len == 0)
			return EMPTY_LIST;
		List list = new ArrayList();
		for (int i = 0; i < len; i++) {
			IConfigurationElement[] webapps = extensions[i]
					.getConfigurationElements();
			if (webapps.length < 1)
				continue;
			for (int j = 0; j < webapps.length; j++) {
				String str = webapps[j].getAttribute("plugin");
				if (str == null)
					str = extensions[i].getNamespace();
				final String pluginName = str;
				final String name = webapps[j].getAttribute("name");
				final String path = webapps[j].getAttribute("path");
				list.add(new IWebAppDescriptor() {

					public String getName() {
						return name;
					}

					public IPath getPath() {
						return new Path(path);
					}

					public String getPluginName() {
						return pluginName;
					}

				});
			}
		}
		IWebAppDescriptor[] array = new IWebAppDescriptor[list.size()];
		list.toArray(array);
		return array;
	}

	private void installApps(IWebAppDescriptor[] descriptors)
			throws CoreException {
		for (int i = 0; i < descriptors.length; i++) {
			String webappName = descriptors[i].getName();
			IPath webappPath = descriptors[i].getPath();
			String pluginName = descriptors[i].getPluginName();
			WebappManager.start(webappName, pluginName, webappPath);
		}
	}

	public void start() throws CoreException {
		IWebAppDescriptor[] descriptors;
		synchronized (this) {
			descriptors = getWebAppDescriptors();
			fInstalledApps = descriptors;
			installApps(descriptors);
			fRunning = true;
		}
		IWebappServer server = AppserverPlugin.getDefault().getAppServer();
		System.out.println("Server is running on " + server.getHost() + ":"
				+ server.getPort());
		// run a headless loop;
		while (fRunning) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ie) {
				break;
			}
		}
		synchronized (this) {
			uninstallApps(descriptors);
		}
		server.stop();
	}

	public void stop() {
		synchronized (this) {
			fRunning = false;
		}
	}

	private void uninstallApps(IWebAppDescriptor[] descriptors)
			throws CoreException {
		for (int i = 0; i < descriptors.length; i++) {
			String webappName = descriptors[i].getName();
			WebappManager.stop(webappName);
		}
	}

	public IWebAppDescriptor[] getInstalledApps() {
		return fInstalledApps;
	}

}

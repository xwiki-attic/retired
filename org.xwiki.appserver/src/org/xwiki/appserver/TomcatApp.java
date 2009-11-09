/**
 * 
 */
package org.xwiki.appserver;

import org.eclipse.core.runtime.IPlatformRunnable;

/**
 * @author kotelnikov
 * 
 */
public class TomcatApp implements IPlatformRunnable {

	public static boolean RUNNING = true;

	/**
	 * 
	 */
	public Object run(Object args) throws Exception {
		WebServer server = TomcatPlugin.getDefault().getWebServer();
		server.start();
		return EXIT_OK;
	}

}

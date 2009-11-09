package force;
/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import controller.ControllerRecieveThread;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IPlatformRunnable {

	public Object run(Object args) throws Exception {
		ControllerRecieveThread crt = new ControllerRecieveThread();
		new Thread(crt).start();
		Display display = PlatformUI.createDisplay();
		
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IPlatformRunnable.EXIT_RESTART;
			}
			return IPlatformRunnable.EXIT_OK;
		} finally {
			display.dispose();
		}
	}
}

package xunit.framework;

/*
 * 	XUnitTestInterface
 */

public interface XUnitTestInterface {

	/**
	 * Runs a test and collects its result in a XUnitTestResult instance.
	 */
	public abstract void run(XUnitTestResult result);
}

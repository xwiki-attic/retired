package xunit.framework;

/**
 *  XUnitTestRunExceptionThrow runs a Method throws a Throwable.
 *  This Throwable is Caught in XUnitTestResult to identify Failures and Errors.
 * 
 */

public interface XUnitTestRunExceptionThrow {
    
	/**
	 *  XUnitTestRunBare Methods throws the Exceptions
	 *  The Exceptions are either Charecterized as Failures (Assertion Failures)
	 *  or Errors (General Caught Exceptions)
	 */
	public abstract void XUnitTestRunBare() throws Throwable;
}

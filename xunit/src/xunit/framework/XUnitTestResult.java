package xunit.framework;

import java.util.Vector;

import xunit.exceptionsOLD.XUnitAssertionFailedException;

/**
 *  XUnitTestResult collects the results of executing
 *  a test case.  
 *  
 */
public class XUnitTestResult {
   
	/**
	 *  Vector of TestCaseFailures (Assertions Failed)
	 */
	protected Vector XUnitFailures;

	/**
	 *  Vector of TestCaseErrors (General Exception Throws)
	 */
	protected Vector XUnitErrors;
	
	/**
	 *  Number of XUnitTestCases Run
	 */
	protected int XUnitTestRuns;
	
	/**
	 *  Default Constructor
	 */
    public XUnitTestResult() {
        XUnitFailures 	= new Vector();
        XUnitErrors 	= new Vector();
        XUnitTestRuns 	= 0; 
    }
    
	/**
	 *  Executes a TestCase
	 */
	protected void run(final XUnitTestCase test) {
	    XUnitTestRuns 	+= 1;
	    
		/**
		 *  Creates an InnerClass with Overriding with RunBare Method of TestCase
		 */	    
		XUnitTestRunExceptionThrow XUnitTestRun = new XUnitTestRunExceptionThrow() {
			public void XUnitTestRunBare() throws Throwable {
				test.runBare();
			}
		};

		/**
		 *  Execute the TestCase by Invoking the RunBare Method Internally
		 */	    
		runXUnitTest(XUnitTestRun, test);
	}

	
	/**
	 *  Execute the TestCase by Invoking the RunBare Method Internally
	 *  Catch the Exceptions and Classify the Errors and Failure on the basis of 
	 *  Types of Exceptions Thrown
	 */	    
    private void runXUnitTest(XUnitTestRunExceptionThrow unitTestRun, XUnitTestCase test) {
        try {
            unitTestRun.XUnitTestRunBare();
		} 
		catch (XUnitAssertionFailedException e) {
			addFailure(test, e);
		}
		catch (Throwable e) {
			addError(test, e);
		}
    }

	/**
	 *  Append Error in Error Vector
	 */	    
    private void addError(XUnitTestCase test, Throwable e) {
        this.XUnitErrors.addElement(new XUnitFailedTest(test, e));        
    }

	/**
	 *  Append Failures (XUnit Assertions Failed) in Failure Vector
	 */	    
    private void addFailure(XUnitTestCase test, XUnitAssertionFailedException e) {
        this.XUnitFailures.addElement(new XUnitFailedTest(test, e));
    }
}

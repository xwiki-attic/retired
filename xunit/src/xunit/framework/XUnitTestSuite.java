/*
 * Created on 2005-aug-03
 * 
 */
package xunit.framework;

import java.util.Enumeration;
import java.util.Vector;

/**
 *  XUnitTestSuite is a Composite of XUnitTestCase.
 *  It Implements XUnitTestInterface, So that Interface can be used for Polymorphisms
 *  to be invoked in Runner's Run Method
 */
  
public class XUnitTestSuite implements XUnitTestInterface {

    /**
     *  Vector of XUnitTestCases.
     */
	private Vector Tests= new Vector(10);
	
    /**
     *  XUnitTestSuite Name.
     */	
	private String TestSuiteName = "";

    /**
     *  Default Constructor.
     */
    public XUnitTestSuite() {
        super();
    }

    /**
     *  Default Constructor.
     */
    public XUnitTestSuite(String testSuiteName) {
        super();
        setTestSuiteName(testSuiteName);
    }	
    
	/**
	 *  Adds a XUnitTest to the XUnitTestSuite Vector
	 */
	public void addTest(XUnitTestInterface Test) {
		Tests.addElement(Test);
	}    
	
	/**
	 *  Returns the XUnitTests as an enumeration
	 */
	public Enumeration getTests() {
		return Tests.elements();
	}
	
	/**
	 *  Returns the number of XUnitTests in this suite
	 */
	public int testCount() {
		return Tests.size();
	}
	
	/**
	 *  Returns the XUnitTest at the given index
	 */
	public XUnitTestInterface testAt(int index) {
		return (XUnitTestInterface)Tests.elementAt(index);
	}
	
	/**
	 *  Iterates through all TestInterfaces and Call Corresponding Run
	 */
    public void run(XUnitTestResult result) {
		for (Enumeration e= getTests(); e.hasMoreElements(); ) 
		{
			XUnitTestInterface test= (XUnitTestInterface)e.nextElement();
			runTest(test, result);
		}
    }
    
	/**
	 *  Invoke Corresponding Run for the Interface
	 */
	public void runTest(XUnitTestInterface test, XUnitTestResult result) {
		test.run(result);
	}
	
    public String getTestSuiteName() {
        return TestSuiteName;
    }
    
    public void setTestSuiteName(String testSuiteName) {
        TestSuiteName = testSuiteName;
    }
}

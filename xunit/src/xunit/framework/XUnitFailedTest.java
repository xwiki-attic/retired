package xunit.framework;

/**
 *  This Class will have Failed Tests along with Excetions Thrown
 *  Its Instances will be created in XResult when Test Case is Run and 
 *  Failed Tests are Inserted in a Vector of Failed Tests
 */     

public class XUnitFailedTest {
    
	private XUnitTestInterface 	XUnitFailedTest;
	private Throwable 			FailedException;

	/**
	 *  Constructor: XUnitFailedTestConstructor
	 */     
    public XUnitFailedTest(XUnitTestInterface unitFailedTest,
            Throwable failedException) {
        super();
        XUnitFailedTest = unitFailedTest;
        FailedException = failedException;
    }
    
	/**
	 *  Returns short description of the failure.
	 */
    public String toString(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.XUnitFailedTest+": "+this.FailedException.getMessage());        
        return stringBuffer.toString();
    }

	/**
	 *  Getter: getFailedException
	 */     
    public Throwable getFailedException() {
        return FailedException;
    }

	/**
	 *  Getter: getFailedTest
	 */     
    public XUnitTestInterface getXUnitFailedTest() {
        return XUnitFailedTest;
    }    
    
}





package xunit.framework;


public class XUnitTestCase extends XUnitAsserts implements XUnitTestInterface, XUnitTestCaseFixture {
	
	/**
	 *  Every XUnitTestCase will have a name
	 */
	private String XUnitTestCaseName = null;

	/**
	 * Constructs a XUnitTestCase with the null name.
	 */	
	public XUnitTestCase() {
		XUnitTestCaseName= null;
	}
	
	/**
	 * Constructs a XUnitTestCase with the given name.
	 */
	public XUnitTestCase(String name) {
		XUnitTestCaseName= name;
	}
	
	/**
	 * Creates a default XUnitTestResult object
	 */
	protected XUnitTestResult createResult() {
	    return new XUnitTestResult();
	}
	
	public void setUp() {
		// TODO Auto-generated method stub
		
	}

	public void tearDown() {
		// TODO Auto-generated method stub
		
	}
	
	public String getName() {
		return XUnitTestCaseName;
	}

	public void setName(String name) {
		XUnitTestCaseName= name;
	}
	
	public String toString() {
	    return getName() + "(" + getClass().getName() + ")";
	}
	
	public XUnitTestResult run() {
		XUnitTestResult result= createResult();
		run(result);
		return result;
	}

	public void run(XUnitTestResult result) {
		result.run(this);
	}
	
	public void runBare() throws Throwable {
		setUp();
		try {
			runTest();
		}
		finally {
			tearDown();
		}
	}
	
	protected void runTest() throws Throwable {
		fail("Throw Exception");
	}

}

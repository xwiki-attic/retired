package xunit.framework;

/*
 * 	XUnitTestCaseFixture
 */

public interface XUnitTestCaseFixture {
    
	/**
	 *  Setup the TestCase, Open Files, DB etc.
	 */
	public void setUp();
	
	/**
	 *  TearDown the TestCase, Close Files, DB etc.
	 */
	public void tearDown();
}

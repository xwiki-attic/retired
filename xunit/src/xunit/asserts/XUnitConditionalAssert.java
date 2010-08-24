/**
 * 
 */
package xunit.asserts;

/**
 *  Author:   Abdul Haseeb
 *  Email:    abdul.haseeb@gmail.com
 *  Creation: 2005-aug-09
 */
public class XUnitConditionalAssert extends XUnitAssert {

    protected XUnitConditionalAssert() {
        
    }
    
	public static String formatConditionalError(String errorMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *  Asserts that a condition is true. 
	 */
	public static void assertTrue(String message, boolean condition) {
		if (!condition)		{
			message = formatConditionalError(message);
			XUnitAssert.fail(message);
		}
	}
	
	/**
	 *  Asserts that a condition is true. 
	 */
	public static void assertTrue(boolean condition) {
		assertTrue(null, condition);
	}
	
	/**
	 *  Asserts that a condition is false. 
	 */
	public static void assertFalse(String message, boolean condition) {
		assertTrue(message, !condition);
	}
	
	/**
	 *  Asserts that a condition is false. 
	 */	
	public static void assertFalse(boolean condition) {
		assertFalse(null, condition);
	}	
}

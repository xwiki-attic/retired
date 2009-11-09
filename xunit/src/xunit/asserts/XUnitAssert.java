/**
 * 
 */
package xunit.asserts;

import xunit.exceptions.XUnitAssertionException;

/**
 *  Author:   Abdul Haseeb
 *  Email:    abdul.haseeb@gmail.com
 *  Creation: 2005-aug-09
 */
public class XUnitAssert{

    protected XUnitAssert(){
        
    }
    
	/* (non-Javadoc)
	 * @see xunit.asserts.AssertInterface#formatError(java.lang.String)
	 */
	public static String formatError(String errorMessage){		
		return errorMessage;
	}

	/* (non-Javadoc)
	 * @see xunit.asserts.AssertInterface#fail(java.lang.String)
	 */
	public static void fail(String errorMessage) {
	    errorMessage = formatError(errorMessage);
		throw new XUnitAssertionException(errorMessage);
	}

	public static void fail() {
		throw new XUnitAssertionException(null);
	}

}

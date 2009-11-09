/**
 * 
 */
package xunit.exceptions;


/**
 *  Author:   Abdul Haseeb
 *  Email:    abdul.haseeb@gmail.com
 *  Creation: 2005-aug-09
 */

public class XUnitAssertionException extends Error {

	private static final long serialVersionUID = 1L;
	
	public XUnitAssertionException (){
		super("Undefined Exception");
	}
	
	public XUnitAssertionException (String errorMessage){
		super(errorMessage);
	}

}

/*
 * Created on 2005-aug-01
 * 
 */
package xunit.exceptionsOLD;

/**
 *  XUnitAssertionFailedException is Thrown when an XUnit Assertion fails.
 * 
 */
public class XUnitAssertionFailedException extends Error{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     *  Default Constructor  
     */
	public XUnitAssertionFailedException () {}
	
    /**
     *  Error Message  
     */
	public XUnitAssertionFailedException (String message) {		super (message);	}
	
}

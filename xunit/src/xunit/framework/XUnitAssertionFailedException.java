/*
 * Created on 2005-aug-01
 * 
 */
package xunit.framework;

/**
 *  XUnitAssertionFailedException is Thrown when an XUnit Assertion fails.
 * 
 */
public class XUnitAssertionFailedException extends Error{

    /**
     *  Default Constructor  
     */
	public XUnitAssertionFailedException () {}
	
    /**
     *  Error Message  
     */
	public XUnitAssertionFailedException (String message) {		super (message);	}
	
}

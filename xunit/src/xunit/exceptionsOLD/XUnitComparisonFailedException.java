package xunit.exceptionsOLD;

public class XUnitComparisonFailedException extends
		XUnitAssertionFailedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     *  Default Constructor  
     */
	public XUnitComparisonFailedException () {}
	
    /**
     *  Error Message  
     */
	public XUnitComparisonFailedException (String message) {		super (message);	}
}

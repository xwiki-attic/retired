/*
 * Created on 2005-aug-01
 * 
 */
package xunit.framework;

import xunit.exceptionsOLD.XUnitAssertionFailedException;

public class XUnitAsserts {
	
	protected XUnitAsserts(){
		
	}
	
	/**
	 *  TestFailed, Throw XUnitAssertionFailed Exception with error message
	 */
	static public void fail(String message) {
		throw new XUnitAssertionFailedException(message);
	}

	/**
	 *  TestFailed, Throw XUnitAssertionFailed Exception with null message
	 */
	static public void fail() {
		fail(null);
	}
	
	/**
	 *  Asserts that a condition is true. 
	 */
	static public void assertTrue(String message, boolean condition) {
		if (!condition)		fail(message);
	}
	
	/**
	 *  Asserts that a condition is true. 
	 */
	static public void assertTrue(boolean condition) {
		assertTrue(null, condition);
	}
	
	/**
	 *  Asserts that a condition is false. 
	 */
	static public void assertFalse(String message, boolean condition) {
		assertTrue(message, !condition);
	}
	
	/**
	 *  Asserts that a condition is false. 
	 */
	static public void assertFalse(boolean condition) {
		assertFalse(null, condition);
	}
	
}

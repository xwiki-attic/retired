/*
 * Created on 2005-sep-14
 * 
 */
package xunit.exceptions;

/**
 * Will be Thrown when the Database can't be
 *  
 *        1. Initialized
 *  
 * @author eabduha 
 */
public class DatabaseException extends Exception {
    
	private static final long serialVersionUID = 1L;

	public DatabaseException(String arg0) {
        super(arg0);        
    }

}

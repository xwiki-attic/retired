/*
 * Created on 2005-aug-11
 * 
 */
package xunit.database;

/**
 * This Interface is used in Column Class. Each Column can be either NULLABLE, NONNULLABLE or UNKNOWN
 * 
 * @author eabduha
 */
public interface Nullable {
    /**
     * Column can contain NULL Values <code>NULLABLE</code>
     */
    public static final int NULLABLE 		=	1;
    
    /**
     * Column can't contain NULL Values <code>NONNULLABLE</code>
     */
    public static final int NONNULLABLE 	=	0;    
    
    /**
     * <code>UNKNOWN</code>
     */
    public static final int UNKNOWN		 	=	2;
}

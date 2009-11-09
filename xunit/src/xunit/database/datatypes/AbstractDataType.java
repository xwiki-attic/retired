package xunit.database.datatypes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AbstractDataType Class is used to Provide an Interface to Underlying DataTypes Namely
 * BinaryDataType, BooleanDataType, DateDataType, DoubleDataType, IntegerDataType, NumberDataType, StringDataType, TimeDataType
 * 
 * Each SubType will have its Own Operations, AbstractDataType Class is used in xunit.database.Column to define the DataType for the Attribute Column
 * 
 * @author eabduha
 */

public abstract class AbstractDataType {
    
    /**
     *  String Name
     */
    private final String name;

    /**
     *  SqlType Name of DataType
     */
    private final int sqlType;    
    
    /**
     * Constructs the AbstractDataType with specified String DataType Name and numeric SqlType
     * 
     * @param name String Name for the DataType 
     * @param sqlType SqlType Name for the DataType
     */
    public AbstractDataType(final String name, final int sqlType) {
        super();
        this.name = name;
        this.sqlType = sqlType;
        
        System.out.println("Following Type Created: "   + this.name + " SQLType is: " + this.sqlType);
        
    }    
    
    /** 
     * @return String Name for the AbstractDataType
     */
    public String getName() {
        return name;
    }    
    
    /**
     * @return SqlName for the AbstractDataType
     */ 
    public int getSqlType() {
        return sqlType;
    }
    
        
    /**
     * This Method TypeCasts the given Object to respective SubDataType
     * 
     * @param value Object to be TypeCasted by Respective SubDataType Class
     * @return TypeCasted Object
     */
    public abstract Object typeCast(Object value) throws xunit.exceptions.TypeCastException;
    
    /**
     * This Method retrieves the Object from the Specified Column of the ResultSet Given.
     * 
     * @param column Column Number whose Value is to be retrieved from the ResultSet
     * @param resultSet ResultSet from which the Value of particular Column Specified by 'column' param is Defined
     * @return Retrieved Object
     */
    public abstract Object getSqlValue(int column, ResultSet resultSet) throws SQLException;
    
    /**
     * This Method sets the Object in the Specified Column
     * 
     * @param value Value which is to be set in the Specified Column
     * @param column Column Number in which the Value is to be Inserted
     * @param statement SQL Statement used to Insert Value in column
     */
    public abstract void setSqlValue(Object value, int column, PreparedStatement statement) throws xunit.exceptions.TypeCastException, SQLException;
    
    /**
     * This Method Generates Random Data for the particular Column's DataType
     */
    public abstract void setRandomSqlValue(int column, PreparedStatement statement) throws xunit.exceptions.TypeCastException, SQLException;
    
}

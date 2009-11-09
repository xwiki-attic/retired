/*
 * Created on 2005-aug-11
 * 
 */
package xunit.database.datatypes;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import xunit.database.TableInterface;
import xunit.exceptions.TypeCastException;

/**
 * @author eabduha
 *
 */
public class DateDataType extends AbstractDataType {

    public DateDataType(String name, int sqlType) {
        super(name, sqlType);
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#typeCast(java.lang.Object)
     */
    public Object typeCast(Object value) throws TypeCastException{
        if (value == null) 									return null;
        if (value == TableInterface.NO_VALUE)				return null;
        if (value instanceof java.sql.Date)		            return value;

        try{
            return java.sql.Date.valueOf((String)value);
        }catch (IllegalArgumentException e){
            throw new TypeCastException(this.getClass() + ": Can't TypeCast to Object");
        }        
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#getSqlValue(int, java.sql.ResultSet)
     */
    public Object getSqlValue(int column, ResultSet resultSet) throws SQLException{
        java.sql.Date value = resultSet.getDate(column);
        if (value == null || resultSet.wasNull())	            return null;
        return value;
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#setSqlValue(java.lang.Object, int, java.sql.Statement)
     */
    public void setSqlValue(Object value, int column, PreparedStatement statement) throws TypeCastException, SQLException{
        statement.setDate(column, (java.sql.Date)typeCast(value));        
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#setRandomSqlValue(int, java.sql.PreparedStatement)
     */
    public void setRandomSqlValue(int column, PreparedStatement statement) throws TypeCastException, SQLException {
        Random randGenerator = new Random();
        setSqlValue(new Date(randGenerator.nextLong()),column,statement);
    }
}

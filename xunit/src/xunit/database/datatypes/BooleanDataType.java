/*
 * Created on 2005-aug-11
 * 
 */
package xunit.database.datatypes;

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
public class BooleanDataType extends AbstractDataType {

    /**
     * @param name
     * @param sqlType
     */
    public BooleanDataType(String name, int sqlType) {
        super(name, sqlType);
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#typeCast(java.lang.Object)
     */
    public Object typeCast(Object value) throws TypeCastException{
        if (value == null)								return null;
        if (value == TableInterface.NO_VALUE)           return null;
        if (value instanceof Boolean)		            return value;
        
        if (value instanceof Number){
            Number number = (Number)value;
            if (number.intValue() == 0)				    return Boolean.FALSE;
            else						                return Boolean.TRUE;
        }

        throw new TypeCastException(this.getClass() + ": Can't TypeCast to Object");
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#getSqlValue(int, java.sql.ResultSet)
     */
    public Object getSqlValue(int column, ResultSet resultSet) throws SQLException{
        if (resultSet.wasNull())            return null;
        boolean value = resultSet.getBoolean(column);
        return value ? Boolean.TRUE : Boolean.FALSE;
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#setSqlValue(java.lang.Object, int, java.sql.Statement)
     */
    public void setSqlValue(Object value, int column, PreparedStatement statement) throws TypeCastException, SQLException{
        statement.setBoolean(column, ((Boolean)typeCast(value)).booleanValue());        
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#setRandomSqlValue(int, java.sql.PreparedStatement)
     */
    public void setRandomSqlValue(int column, PreparedStatement statement) throws TypeCastException, SQLException {
        Random randGenerator = new Random();
        statement.setBoolean(column, randGenerator.nextBoolean());        
    }



}

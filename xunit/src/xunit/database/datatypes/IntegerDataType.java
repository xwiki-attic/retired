/*
 * Created on 2005-aug-11
 * 
 */
package xunit.database.datatypes;

import java.math.BigDecimal;
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
public class IntegerDataType extends AbstractDataType {

    /**
     * @param name
     * @param sqlType
     */
    public IntegerDataType(String name, int sqlType) {
        super(name, sqlType);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#typeCast(java.lang.Object)
     */
    public Object typeCast(Object value) throws TypeCastException{
        if (value == null)								return null;
        if (value == TableInterface.NO_VALUE)			return null;
        if (value instanceof Number)		            return new Integer(((Number)value).intValue());
        try{
            return typeCast(new BigDecimal(value.toString()));
        }catch (java.lang.NumberFormatException e){
            throw new TypeCastException(this.getClass() + ": Can't TypeCast to Object");
        }
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#getSqlValue(int, java.sql.ResultSet)
     */
    public Object getSqlValue(int column, ResultSet resultSet) throws SQLException{
        if (resultSet.wasNull())				            return null;
        int value = resultSet.getInt(column);        
        return new Integer(value);
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#setSqlValue(java.lang.Object, int, java.sql.Statement)
     */
    public void setSqlValue(Object value, int column, PreparedStatement statement) throws TypeCastException, SQLException{
        statement.setInt(column, ((Integer)typeCast(value)).intValue());        
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#setRandomSqlValue(int, java.sql.PreparedStatement)
     */
    public void setRandomSqlValue(int column, PreparedStatement statement) throws TypeCastException, SQLException {
        Random randGenerator = new Random();
        Integer integer = new Integer(randGenerator.nextInt());
        setSqlValue(integer, column, statement);
    }

}

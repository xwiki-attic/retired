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
public class DoubleDataType extends AbstractDataType {

    /**
     * @param name
     * @param sqlType
     */
    public DoubleDataType(String name, int sqlType) {
        super(name, sqlType);
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#typeCast(java.lang.Object)
     */
    public Object typeCast(Object value) throws TypeCastException{
        if (value == null) 									return null;
        if (value == TableInterface.NO_VALUE)				return null;
        if (value instanceof Number)			            return new Double(((Number)value).doubleValue());

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
        double value = resultSet.getDouble(column);
        if (resultSet.wasNull())		            return null;
        return new Double(value);
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#setSqlValue(java.lang.Object, int, java.sql.Statement)
     */
    public void setSqlValue(Object value, int column, PreparedStatement statement) throws TypeCastException, SQLException{
        statement.setDouble(column, ((Double)typeCast(value)).doubleValue());        
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#setRandomSqlValue(int, java.sql.PreparedStatement)
     */
    public void setRandomSqlValue(int column, PreparedStatement statement) throws TypeCastException, SQLException {
        Random randGenerator = new Random();
        Double dble = new Double(randGenerator.nextDouble());
        setSqlValue(dble, column, statement);
    }

}

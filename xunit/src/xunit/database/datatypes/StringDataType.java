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
public class StringDataType extends AbstractDataType {

    /**
     * @param name
     * @param sqlType
     */
    public StringDataType(String name, int sqlType) {
        super(name, sqlType);
    }

    
    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#typeCast(java.lang.Object)
     */
    /**
     * Not Complete, have to Provide much richer TypeCasting Coverage
     * 
     * @param value Value to TypeCast
     * @return TypeCasted Object
     */
    public Object typeCast(Object value) throws xunit.exceptions.TypeCastException{
        if (value == null)					            return null;
        if (value == TableInterface.NO_VALUE)	        return null;
        if (value instanceof String)			        return value;
        if (value instanceof java.sql.Date ||
            value instanceof java.sql.Time ||
            value instanceof java.sql.Timestamp || 
            value instanceof Boolean ||
            value instanceof Number)        			return value.toString();

        throw new TypeCastException( this.getClass() + ": Can't TypeCast to Object");        
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#getSqlValue(int, java.sql.ResultSet)
     */
    public Object getSqlValue(int column, ResultSet resultSet) throws SQLException{
        String value = resultSet.getString(column);
        if (value == null || resultSet.wasNull())
        {
            return null;
        }
        return value;
    }

    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#setSqlValue(java.lang.Object, int, java.sql.Statement)
     */
    public void setSqlValue(Object value, int column, PreparedStatement statement) throws TypeCastException, SQLException {
        statement.setString(column, (String)(typeCast(value)));
    }


    /* (non-Javadoc)
     * @see xunit.database.datatypes.AbstractDataType#setRandomSqlValue(int, java.sql.PreparedStatement)
     */
    public void setRandomSqlValue(int column, PreparedStatement statement) throws TypeCastException, SQLException {
        setSqlValue(getRandomString(0,100),column,statement);
    }

    public static int rand(int min, int max){        
        Random randGenerator = new Random();
        int n = max - min + 1;
        int i = randGenerator.nextInt()%n;
        if (i < 0) i = 0;
        return (min + i);
    }
    
    public static String getRandomString(int minCharacters, int maxCharacters){        
        int n = rand(minCharacters, maxCharacters);
        byte b[] = new byte[n];
        for (int i = 0; i < n; i++)  b[i] = (byte)rand('a', 'z');
        return new String(b, 0);
    }
    


}

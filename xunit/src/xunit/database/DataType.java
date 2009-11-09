/*
 * Created on 2005-aug-11
 * 
 */
package xunit.database;

import java.sql.Types;
import xunit.database.datatypes.*;

public class DataType {

    /**
     * <code>CHAR, VARCHAR, LONGVARCHAR</code> String Data Type
     */
    public static final int CHAR 		= 1;
    public static final int VARCHAR 	= 12;
    public static final int LONGVARCHAR = -1;

    /**
     * <code>NUMERIC, DECIMAL</code> Number Data Type
     */
    public static final int NUMERIC 	= 2;
    public static final int DECIMAL 	= 3;
    
    /**
     * <code>BOOLEAN</code> Boolean Data Type
     */
    public static final int BOOLEAN 	= 16;
    
    /**
     * <code>INTEGER</code> Integer Data Type
     */
    public static final int INTEGER 	= 4;
    
    /**
     * <code>TIME</code> Time Data Type
     */
    public static final int TIME 		= 92;
    
    /**
     * <code>DATE</code> Date Data Type
     */
    public static final int DATE 		= 91;
    
    /**
     * <code>FLOAT, DOUBLE</code> Double Data Type
     */
    public static final int FLOAT 		= 6;    
    public static final int DOUBLE 		= 8;
    
    /*
     * TODO Problemetic, Inconsistent SQL Types my My Types
     */
    public static String[] getDataTypesList (){
        String[] DataTypesList = new String[11];
        
        DataTypesList[0] = new String("CHAR");
        DataTypesList[1] = new String("VARCHAR");
        DataTypesList[2] = new String("LONGVARCHAR");
        
        DataTypesList[3] = new String("NUMERIC");
        DataTypesList[4] = new String("DECIMAL");        
        
        DataTypesList[5] = new String("BOOLEAN");
        
        DataTypesList[6] = new String("INTEGER");
        
        DataTypesList[7] = new String("TIME");
        
        DataTypesList[8] = new String("DATE");
        
        DataTypesList[9] = new String("FLOAT");
        DataTypesList[10] = new String("DOUBLE");
        
        return DataTypesList;
    }
    
    
    /**
     * @param type DataType, use DataType.CHAR, DataType.NUMERIC etc. 
     * @return AbstractDataType BaseClass
     */
    public static AbstractDataType getDataType (int type){
        switch (type){
        
        
        case DataType.CHAR:
            return new StringDataType("CHAR", Types.CHAR);
        
        case DataType.VARCHAR:
            return new StringDataType("VARCHAR", Types.VARCHAR);
        
        case DataType.LONGVARCHAR:
            return new StringDataType("LONGVARCHAR", Types.LONGVARCHAR);
        
        
        
            
        case DataType.NUMERIC:
            return new NumberDataType("NUMERIC", Types.NUMERIC);
            
        case DataType.DECIMAL:
            return new NumberDataType("DECIMAL", Types.DECIMAL);
        
        
        
        
            
        case DataType.BOOLEAN:
            return new BooleanDataType("BOOLEAN",Types.BOOLEAN);
        
        
        
        
            
        case DataType.INTEGER:
            return new IntegerDataType("INTEGER", Types.INTEGER);
        
        
        
        
        
        case DataType.TIME:
            return new TimeDataType("TIME", Types.TIME);
            
        
        
        
        case DataType.DATE:
            return new DateDataType("DATE", Types.DATE);
            
        
        
        
        case DataType.FLOAT:
            return new DoubleDataType("FLOAT", Types.FLOAT);
            
        case DataType.DOUBLE:
            return new DoubleDataType("DOUBLE", Types.DOUBLE);
            
        
        
        
        default:
            return null;            
        }
    }
    
}


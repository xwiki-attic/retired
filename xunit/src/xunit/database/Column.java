/*
 * Created on 2005-aug-11
 * 
 */
package xunit.database;

import xunit.database.datatypes.AbstractDataType;

/**
 * This Class Represents the Column. Each Table comprises of a Collection of Columns. 
 * 
 * @author eabduha
 */
public class Column implements Nullable{

    /**
     * Whether its NULLABLE, NONNULLABLE or UNKNOWN <code>nullable</code>
     */
    private final int 					nullable;
    
    /**
     * Name of Column <code>columnName</code>
     */
    private final String 				columnName;
    
    /**
     * DataType of Column <code>dataType</code>
     */
    private final AbstractDataType 		dataType;

    
    public static String[] getNullableAttributes(){
        
        String[] NullableAttributes = new String[3];        
        NullableAttributes[0] = "NULLABLE";
        NullableAttributes[1] = "NONNULLABLE";
        NullableAttributes[2] = "UNKNOWN";        
        return NullableAttributes;
        
    }
    
    public String NullableValue (){
        switch (nullable){
        case 0:
            return "NOT NULL";
        case 1:
            return "NULL";
        case 2:
            return "";
        default:
            return "";    
        }
    }
    
    public String toString(){
        return " Column Name " + this.columnName + " SQLDataType " + this.dataType.getName() + " ColumnNullableAttr " + this.nullable;
    }
    
    /**
     * @param columnName Name of Column
     * @param datatype @see DataType
     */
    public Column(String columnName, int datatype){
        this.columnName 	= columnName;
        this.dataType 		= DataType.getDataType(datatype);
        this.nullable 		= UNKNOWN;
    }
    
    /**
     * @param columnName Name of Column
     * @param datatype @see DataType
     * @param nullable Nullable attribute of the Column 
     */
    public Column(String columnName, int datatype, int nullable){
        this.columnName 	= columnName;
        this.dataType 		= DataType.getDataType(datatype);
        this.nullable 		= nullable;
   }    
    
    /**
     * @return Column Name
     */
    public String getColumnName() {
        return columnName;
    }
    
    /**
     * @return DataType of the Column
     */
    public AbstractDataType getDataType() {
        return dataType;
    }
    
    /**
     * @return Nullable Attribute of Column
     */
    public int getNullable() {
        return nullable;
    }
    
}

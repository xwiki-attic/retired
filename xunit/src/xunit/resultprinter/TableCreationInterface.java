/*
 * Created on 2005-sep-09
 * 
 */
package xunit.resultprinter;

import xunit.database.Column;
import xunit.database.DataType;

/**
 * @author eabduha
 *
 */
public class TableCreationInterface {
    
    // Available DataTypes which are to be Presented to User
    private String[] dataTypes = null;    
    private String[] columnNullableTypes = null;

    /**
     *  Interface to Enable user to Create a Table as per defined DataTypes
     *  
     *  .. Table Needs a TableMeataData for its Creation
     *  .. MetaData Include .. Table Name and List of Columns
     * 
     *  .. Each Column has a DataType which is Defined in Package ... xunit.database.datatypes
     *  
     *  This information has to be given in an Interface so that User can Write .. 
     * 	1. Table Name
     *  2. Create Columns .. (Column Name) and Select Column DataType from some Drop List 
     *  (Drop list for Data Type will be populated with the already defined DataTypes) 
     * 
     */
    
    public TableCreationInterface() {
        // List of DataTypes which are Supported by Database Functions
        dataTypes = DataType.getDataTypesList();
        
        // List of Possible Column Nullable Attributes
        columnNullableTypes = Column.getNullableAttributes();
    }
    

}

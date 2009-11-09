/*
 * Created on 2005-aug-11
 * 
 */
package xunit.database;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import xunit.exceptions.DatabaseException;
import xunit.exceptions.TypeCastException;

/**
 * Every Table will Comprise of of TableMetaData. Which has Table Name and an Array of Columns (Column Name, Datatype, NULLABLE Attribute)
 * 
 * @author eabduha 
 */

public class TableMetadata {
    /**
     * Table Name <code>tableName</code>
     */
    private String 	tableName;
    
    /**
     * List of Columns for Table <code>columns</code>
     */
    private Column[] columns;
    
    public TableMetadata(){
        
    }
    
    /**
     * @param tableName Name of Table
     * @param columns List of Columns
     */
    public TableMetadata(String tableName, Column[] columns){
        this.tableName 	= tableName;
        this.columns	= columns; 
	}

    public int getColumnCount(){
        return columns.length;
    }
    
    public String getColumnString(){
        String columnString = new String();
	        for (int i = 0; i < columns.length; i++) {
	            columnString = columnString + columns[i].toString();
	            if (i< (columns.length-1) ){
	                columnString = columnString + ", ";
	            }
	        }
        return columnString;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        String columnString = new String();
        for (int i = 0; i < columns.length; i++) {
            columnString = columnString + columns[i].toString();
        }
        return "tableName= " 	+ tableName + 
        		" , columns=" 	+ columnString;
    }
    
    /**
     * @return Column List of the TableMetaData
     */
    public Column[] getColumns() {
        return columns;
    }
    
    /**
     * @return Table Name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param metaData
     */
    public void CreateMetaData(ResultSetMetaData metaData) throws DatabaseException {
        try{
            int columnCount = metaData.getColumnCount();
            this.columns = new Column[columnCount];
            
            if (columnCount>=1)
                this.tableName = metaData.getTableName(1);            
            
            for (int i=1;i<=columnCount;i++){
                this.columns[i-1] = new Column(metaData.getColumnName(i),metaData.getColumnType(i),metaData.isNullable(i));
            }            
        }catch (Exception e) {
            throw new DatabaseException("Exception in generating Metadata " + e.toString());    
        }
    }
    
    public void RandomlyPopulateColumns(PreparedStatement statement) throws DatabaseException{
        for (int i = 0; i < columns.length; i++){
            try {
                columns[i].getDataType().setRandomSqlValue(i+1, statement);
            } catch (TypeCastException e) {
                throw new DatabaseException("TypeCast Exception Occured while RandomlyPopulating the Table " + e.toString());
            } catch (SQLException e) {
                throw new DatabaseException("SQL Exception Occured while RandomlyPopulating the Table " + e.toString());
            }
        }
    }
}

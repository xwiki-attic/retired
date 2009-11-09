/*
 * Created on 2005-sep-15
 * 
 */
package xunit.database;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import xunit.asserts.DatabaseConnection;
import xunit.exceptions.DatabaseException;
import xunit.resultprinter.XUnitResult;

/**
 * @author eabduha
 *
 */
public class ResultsetTable extends AbstractTable {    
    
    DatabaseConnection dbConnection = null;
    java.sql.ResultSet resultset = null;
    ResultSetMetaData resultsetMetadata = null;
    String tableName = "";
    java.sql.Statement statetment = null;
    String Query = "";
    TableMetadata tableMetadata = null;

    /*
     * TODO DataSet .. not exactly Sure
     * DataSet will be Created from the TableMetaData
     * Must be Called at the end of Each Distructor, if TableMetaData for a Table is Properly Created
     */
    TableDataSet dataSet = null;
    
    public ResultsetTable(DatabaseConnection dbConnection, String tableName) throws DatabaseException {
        this.dbConnection = dbConnection;
        this.tableName = tableName;
        
        // Might Throw Database Exception
        this.statetment = this.dbConnection.CreateStatement();
        this.CreateQuery();
        this.SelectTable();
        this.getResultsetMetaData();
        
        // If table Selected Properly then
        tableMetadata = new TableMetadata();
        tableMetadata.CreateMetaData(resultsetMetadata);
        
        // Create the TableDataSet associated with particular TableMetaData
        dataSet = new TableDataSet(tableMetadata);
    }
    
    public ResultsetTable(DatabaseConnection dbConnection, TableMetadata tableMetaData) throws DatabaseException {
        this.dbConnection = dbConnection;
        this.tableName = tableMetaData.getTableName();
        
        // Might Throw Database Exception
        this.statetment = this.dbConnection.CreateStatement();
        if ( !isTableAlreadyExist() && !this.dbConnection.IsConnectionReadOnly()){
            TableCreateQuerry (tableMetaData);
            CreateTable();
            this.tableMetadata  = tableMetaData;
        }else	throw new DatabaseException("Table Already Exists in Database");
            
        // Create the TableDataSet associated with particular TableMetaData
        dataSet = new TableDataSet(tableMetadata);
    }

    public void CreateRandomDataSet(int NumRandomRows) throws DatabaseException{
        //TODO
        //  dataSet.CreateRandomDataSet(NumRandomRows);
        
    }
    
    public void TableCreateQuerry (TableMetadata tableMetaData) throws DatabaseException{
        if (tableMetaData.getTableName().equals("")){
            throw new DatabaseException("TableName not Given for the Creation of ResultSet Table");
        }
        Column[] columns = tableMetaData.getColumns();
        
        Query = "";
        Query = "create table " + tableMetaData.getTableName() + " ( ";
        for (int i = 0; i < columns.length; i++) {
            columns[i].getDataType().getName();            
            Query = Query + " " + columns[i].getColumnName() +
            				" " + columns[i].getDataType().getName() +
            				" " + columns[i].NullableValue();
            				
            if (i <  (columns.length-1))    Query = Query + ","	;
        }        
        Query = Query + " ) ";
        
        System.out.println("Created Query: " + Query);
    }
    
    public void CreateTable()throws DatabaseException{
        try {
            statetment.execute(this.Query);
        } catch (SQLException e) {
            throw new DatabaseException("Problem in Executing the Create Query to Create ResultSet Table\n" + e.toString());
        }
    }
    
    private boolean isTableAlreadyExist() throws DatabaseException {
        this.CreateQuery();
        try{
            this.SelectTable();
        }catch (DatabaseException e){
            return false;
        }
        
        return true;       
    }

    public XUnitResult DisplayTableMetaData() throws DatabaseException{
        if (this.tableMetadata==null)
            throw new DatabaseException("Table MetaData not Yet Initialized");
        
        XUnitResult displayMetadataResult = new XUnitResult();
        displayMetadataResult.ConcatResult("TableMetaData: " + this.tableMetadata.toString());
        return displayMetadataResult;
    }
    
    public void getResultsetMetaData() throws DatabaseException{
        if ( !isResultSetNull() ){
            try {
                this.resultsetMetadata = this.resultset.getMetaData();
            } catch (SQLException e) {
                throw new DatabaseException("Can't Get ResultSet MetaData");    
            }
        }
    }
    
    public boolean isResultSetNull() throws DatabaseException{
        if (resultset == null)
            throw new DatabaseException("ResultSet Null");
        
        return false;
    }
    
    public void CreateQuery() throws DatabaseException{
        if (this.tableName.equals("")){
            throw new DatabaseException("TableName not Given for the Creation of ResultSet Table");
        }
        
        Query = "Select * " +
        		"from " +
        		this.tableName;
        
        System.out.println("Created Query: " + Query);
    }
    
    public void SelectTable()throws DatabaseException{
        try {
            resultset = statetment.executeQuery(this.Query);
        } catch (SQLException e) {
            throw new DatabaseException("Problem in Executing the Select Query to Create ResultSet Table\n" + e.toString());
        }
    }
    
    public TableMetadata getTableMetaData() throws DatabaseException{
        if (this.tableMetadata==null)
            throw new DatabaseException("Table MetaData not Yet Initialized");
    	
        return this.tableMetadata;
    }

    public int getRowCount() {
        return 0;
    }

    public Object getValue(int row, String column) throws Exception {
        return null;
    }

}

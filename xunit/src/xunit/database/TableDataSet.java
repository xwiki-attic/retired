/*
 * Created on 2005-sep-16
 * 
 */
package xunit.database;

import xunit.asserts.DatabaseConnection;
import xunit.database.datatypes.StringDataType;
import xunit.exceptions.DatabaseException;
import java.sql.*;

public class TableDataSet {

    // Copy of MetaData and Connection
    private TableMetadata tableMetaData 		= null;
    private DatabaseConnection dbConnection 	= null;
    private PreparedStatement preparedStatement = null;
    private String sql 							= "";
    private Savepoint savePoint					= null;
    
    // We know the TableMetaData, So all Operations of 
    // Data Insersion i.e. Insertion of Row, Test Data etc should be Done here  
    public TableDataSet(TableMetadata tableMetaData_) throws DatabaseException{
        this.tableMetaData = tableMetaData_;
        CreateSQLString();
    }
    
    private void CreateSQLString () throws DatabaseException{
        this.sql = "";    
        if (tableMetaData != null){
            sql = sql + "INSERT INTO " + 
            			this.tableMetaData.getTableName() + " ( " + 
            			this.tableMetaData.getColumnString() + 
            			" ) VALUES ( " + getPlaceHolderString() + " )";
        }else{
            throw new DatabaseException("TableMetaData is Null, Can't Proceed Further with DataSet ");
        }
        
    }
    
    private String getPlaceHolderString(){
        String placeHolder = " ? ";
        for (int i=0;i<(this.tableMetaData.getColumnCount()-1);i++)
            placeHolder = placeHolder + " , " + " ? ";
        
        return placeHolder;
    }
    
    // TODO with Current Implementation it is needed that it is Called Only Once, 
    // Have to put a decent way to Restore with Multiple Insertions or Random Fillups
    public void RandomlyPopulateDatabase(DatabaseConnection dbConnection_, int lowerLimit, int higherLimit) throws DatabaseException {        
        this.dbConnection = dbConnection_;
        Connection connection = this.dbConnection.getConnection();        
        
        if (tableMetaData == null || sql == ""){
            throw new DatabaseException("SQL Can't be Generated");
        }
        
        try{
            // Create the SavePoint as the Dataset when deleted has to RollBack to this Point
            savePoint = connection.setSavepoint();            
            preparedStatement = connection.prepareStatement(sql);
            FillUpTable(StringDataType.rand(lowerLimit, higherLimit));
        } catch (SQLException e){
            throw new DatabaseException("SQL Exception Thrown " + e.toString());
        }
    }

    private void FillUpTable(int rowLimit) throws DatabaseException{
        for (int rows=0;rows<rowLimit;rows++){
            try {
                tableMetaData.RandomlyPopulateColumns(preparedStatement);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DatabaseException("SQLException while filling up Table " + e.toString());
            }
        }
    }

}

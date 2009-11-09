package xunit.database;

import xunit.exceptions.DatabaseException;

public class TableArray {
	
    private AbstractTable TableObjects[] = null;
    private int TableObjectsInitialized = 0;
    private int TotalTables = 0;
    
    public TableArray(int totalTables) {
    	TableObjects = new AbstractTable[totalTables];
    	TotalTables = totalTables;   
    	
    	InitializeTableObjects();
	}
    
    private void InitializeTableObjects(){
    	for (int i = 0; i < TableObjects.length; i++) {
			TableObjects[i] = null;
			TableObjectsInitialized = 0;
		}
    }
    
    public void AddTable(AbstractTable table) throws DatabaseException{
    	if (TableObjectsInitialized >= TotalTables){
    		throw new DatabaseException("Maximum Tables Already Loaded");
    	}
    	
    	TableObjects[TableObjectsInitialized++] = table;
    }
    
    public void RemoveTable(String tableName) throws DatabaseException{
    	for (int i = 0; i < this.TableObjectsInitialized; i++) {
    		if (TableObjects[i]!=null){
    			try{
	    			if (TableObjects[i].getTableMetaData().getTableName().equals(tableName)){
	    				TableObjects[i] = null;
	    				for(;i<this.TableObjectsInitialized;i++) TableObjects[i] = TableObjects[i+1];
	    			}
    			}catch (DatabaseException e){
    				throw e;
    			}
    		}
    	}
    	throw new DatabaseException("No Table with " + tableName + " Loaded in Table Array");
    }
    
    public void RemoveTable(TableMetadata tableMetaData) throws DatabaseException{
    	RemoveTable(tableMetaData.getTableName());
    }
    
    public TableMetadata getTableMetaData(String tableName) throws DatabaseException{
    	for (int i = 0; i < this.TableObjectsInitialized; i++) {
    		if (TableObjects[i]!=null){
    			try{
	    			if (TableObjects[i].getTableMetaData().getTableName().equals(tableName))
	    				return TableObjects[i].getTableMetaData();
    			}catch (DatabaseException e){
    				throw e;
    			}
    		}    		
    	}    		
    	throw new DatabaseException("No Table with " + tableName + " Loaded in Table Array");
    }
    
}

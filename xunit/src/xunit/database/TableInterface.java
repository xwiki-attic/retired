/*
 * Created on 2005-sep-06
 * 
 */
package xunit.database;

import xunit.exceptions.DatabaseException;

/**
 * @author eabduha
 */
public interface TableInterface {
    
    public static final Object NO_VALUE = new Object();
    
    
    public TableMetadata getTableMetaData() throws DatabaseException;
    public int getRowCount();
    public Object getValue(int row, String column) throws Exception;
}


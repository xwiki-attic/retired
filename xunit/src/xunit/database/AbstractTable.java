/*
 * Created on 2005-sep-09
 * 
 */
package xunit.database;

import xunit.exceptions.DatabaseException;

/**
 * @author eabduha
 *
 */
public abstract class AbstractTable implements TableInterface {

    /* (non-Javadoc)
     * @see xunit.database.TableInterface#getTableMetaData()
     */
    public abstract TableMetadata getTableMetaData() throws DatabaseException;

    /* (non-Javadoc)
     * @see xunit.database.TableInterface#getRowCount()
     */
    public abstract int getRowCount();

    /* (non-Javadoc)
     * @see xunit.database.TableInterface#getValue(int, java.lang.String)
     */
    public abstract Object getValue(int row, String column) throws Exception ;

}

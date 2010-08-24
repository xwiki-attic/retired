/*
 * Created on 2005-sep-09
 * 
 */
package xunit.plugin;

import xunit.asserts.DatabaseConnection;
import xunit.database.ResultsetTable;
import xunit.database.TableArray;
import xunit.database.TableMetadata;
import xunit.exceptions.DatabaseException;
import xunit.resultprinter.XUnitResult;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

public class XUnitPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface {
    
	TableArray tableArray = null;    
    DatabaseConnection dbConnection = null;
    
   
    /**
     * @param name Name of plugin
     * @param classname Class Name
     * @param context XWikiContext
     */
    public XUnitPlugin(String name, String classname, XWikiContext context) {
        super(name, classname, context);
        init(context);
        
        tableArray = new TableArray(10);
    }

    public XUnitResult createDBConnection (String connection_url, String username, String password, String database){
        try {
            dbConnection = DatabaseConnection.getDatabaseConnection(connection_url, username, password, database);
        } catch (DatabaseException e) {
            return new XUnitResult(e.toString());
        }
        return new XUnitResult("Success in Creation of Database Connection");
    }
    
    /* (non-Javadoc)
     * @see com.xpn.xwiki.plugin.XWikiPluginInterface#getName()
     */
    public String getName() {
        return "xunit";
    }    
    
    /* (non-Javadoc)
     * @see com.xpn.xwiki.plugin.XWikiPluginInterface#init(com.xpn.xwiki.XWikiContext)
     */
    public void init(XWikiContext context) {
        super.init(context);
    }
    
    /* (non-Javadoc)
     * @see com.xpn.xwiki.plugin.XWikiPluginInterface#getPluginApi(com.xpn.xwiki.plugin.XWikiPluginInterface, com.xpn.xwiki.XWikiContext)
     */
    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
        return new XUnitPluginApi((XUnitPlugin)plugin, context);
    }

   // -------------------- API Methods, which Expose the XUnit API Funcationality ------------------- //
    
    
    
    // -------------------- Utility Methods ------------------- //
    
        /**
         * @return
         */
        public XUnitResult SelectTable(String tableName) {
            XUnitResult result = new XUnitResult();
            try {
                if (!dbConnection.IsConnectionNull() && dbConnection.IsConnectionEstablised()){
                    ResultsetTable rsTable = new ResultsetTable(dbConnection, tableName);
                    result = rsTable.DisplayTableMetaData();
                }
            } catch (DatabaseException e) {
                return new XUnitResult(e.toString());
            }
            result.ConcatResult("Table Selection Done Properly");
            return result;
        }

        /**
         * @param tableMetaData
         * @return
         */
        public XUnitResult CreateTable(TableMetadata tableMetaData) {
            XUnitResult result = new XUnitResult();
            try {
                if (!dbConnection.IsConnectionNull() && dbConnection.IsConnectionEstablised()){
                    ResultsetTable rsTable = new ResultsetTable(dbConnection, tableMetaData);
                    result = rsTable.DisplayTableMetaData();
                }
            } catch (DatabaseException e) {
                return new XUnitResult(e.toString());
            }
            result.ConcatResult("Table Created Properly");

            return result;
        }
    
    
}

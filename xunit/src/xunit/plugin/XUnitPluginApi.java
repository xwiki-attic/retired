/*
 * Created on 2005-sep-09
 * 
 */
package xunit.plugin;

import xunit.database.Column;
import xunit.database.TableMetadata;
import xunit.resultprinter.TableCreationInterface;
import xunit.resultprinter.TableSelectionInterface;
import xunit.resultprinter.XUnitResult;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;

public class XUnitPluginApi extends Api{

    private XUnitPlugin plugin;
    
    /**
     * @param plugin First Create the XUnit plugin and then pass this Constructor 
     * @param context XWikiContext
     */
    public XUnitPluginApi(XUnitPlugin plugin, XWikiContext context){
        super(context);
        setPlugin(plugin);
    }
    
    /**
     * @param plugin XUnitPlugin
     */
    private void setPlugin(XUnitPlugin plugin) {
        this.plugin = plugin; 
    }

    /**
     * @param arg0
     */
    public XUnitPluginApi(XWikiContext arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @return XUnitPlugin
     */
    public XUnitPlugin getPlugin() {
        return plugin;
    }
    
    
		    // ====== TABLE RELATED APIs EXPOSED ===== //
    
    		/**
    		 * TODO: The Interface to Show to User upon this is not Decided
    		 * 
    		 * Interface to Show User Tables for Selection
    		 * 
    		 * @return This Method will return the Interface which can be used to enable User to Select Table 
    		 */
    		public TableSelectionInterface RetrieveInterface_TableSelection(){
    		    return new TableSelectionInterface();
    		}

    		/**
    		 * TODO: The Interface to Show to User upon this is not Decided
    		 * 
    		 * Interface to enable user to Create Table
    		 * 
    		 * @return This Method will return the Interface which can be used to enable User to enter Table MetaData to Create the Table 
    		 */
    		public TableCreationInterface RetrieveInterface_TableCreate(){
    		    return new TableCreationInterface();
    		}
    		
    		/**
    		 * If a user wants to Create a Table then, this Interface should be used to create a Table
    		 * 
    		 * @param tableMetaData MetaData for Table Creation. (Table Name, List of Columns)
    		 * @return
    		 */
    		public XUnitResult CreateTable(TableMetadata tableMetaData){
    		    // TODO
    		    return plugin.CreateTable(tableMetaData);
    		}
    		
    		/**
    		 * If a user wants to Use the existing Table then, this Interface should be used to select the Table
    		 * 
    		 * @param TableName TableName which is required to be Selected
    		 * @return
    		 */
    		public XUnitResult SelectTable(String TableName){
    		    // TODO
    		    return plugin.SelectTable(TableName);    		    
    		}
    		
    		public Column createColumn(String ColumnName, int dataType){
    		    return new Column(ColumnName,dataType);
    		}
    		
    		public TableMetadata createTableMetaData (String tableName, Column[] columns){
    		    return new TableMetadata(tableName,columns);
    		}
    		
    		public XUnitResult createDBConnection (String connection_url, String username, String password, String database){
    		    return plugin.createDBConnection(connection_url, username, password, database);
    		}
    		
    		   		
    		
    		
}

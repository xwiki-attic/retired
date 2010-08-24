/*
 * Created on 2005-sep-09
 * 
 */
package xunit.test;

import xunit.database.Column;
import xunit.database.TableMetadata;
import xunit.plugin.XUnitPlugin;
import xunit.resultprinter.XUnitResult;

import com.xpn.xwiki.XWikiContext;

/**
 * @author eabduha
 *
 */
public class TestClassPluginDB {

    public TestClassPluginDB() {
        super();
    }

    public static void main(String[] args) {
        // dummy Context, In Actual it has to be retrieved from Application
        XWikiContext context = new XWikiContext();        
        XUnitPlugin xunitplugin = new XUnitPlugin("xunit", "xunit", context);
        XUnitResult result = xunitplugin.createDBConnection("jdbc:mysql://localhost/","root","","test");
        
        Column[] columns = new Column[2];
        columns[0] = new Column("Column1",1);
        columns[1] = new Column("Column2",3);
        
        TableMetadata mdata = new TableMetadata("Test2",columns);
        result = xunitplugin.CreateTable(mdata);
        
        
        System.out.println(result.getResult());
        
        
        
    }
}

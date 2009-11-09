/*
 * Created on 2005-aug-03
 * 
 */
package xunit.framework;

/**
 * @author eabduha
 *
 */
public class XUnitDatabaseAsserts extends XUnitObjectTypeAsserts {
	
	private String entity = "";
	private String entityDBPath = "";
	
	public XUnitDatabaseAsserts(){
		entity = "";
		entityDBPath = "";
		/*
		 * Create Connection with XWikiDatabase
		 */
	}
	
	private String ParseEntityPath (){
		/*
		 *  Parse the Entity Path to Database Path For later Extensibility
		 */
		return this.entityDBPath;
	}
	
	public void setEntity(String entity){
		this.entity = entity;
		this.entityDBPath = ParseEntityPath();
	}
	
	public String getEntity(){
		return entity;
	}
	
	/**
	 *  Asserts that Class/Macro/Database Entity Exists in Database 
	 */	
	public void assertExist(String entityPath) {
		/*
		 *  Querry the Database to check whether the Entity Exists or not
		 */
		
	}
	

	

	
}

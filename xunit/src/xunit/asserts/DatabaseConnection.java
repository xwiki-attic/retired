/**
 * 
 */
package xunit.asserts;

import java.sql.DriverManager;
import java.sql.SQLException;

import xunit.exceptions.DatabaseException;

import com.mysql.jdbc.Connection;


/**
 * DatabaseConnection is a Singelton Class, which provides reference to DatabaseConnection 
 * 
 * @author Abdul Haseeb
 */
public class DatabaseConnection {
	
	/*
	 *  For Singelton Reference
	 */
	private static DatabaseConnection reference 	=	null;	
	
	/*
	 * DatabaseConnection
	 */
	private static Connection connection 			= 	null;
	
	/*
	 *  Connection Related Settings, ConnectionURL, username, password, database
	 */
	private static String connection_url 			=	null;
	private static String username 					=	null;
	private static String password 					=	null;
	private static String database					= 	null;
	
	private static boolean connection_established 	= 	false;
	
	public static DatabaseConnection getDatabaseConnection(String connection_url, String username, String password, String database) throws DatabaseException{
		if (reference==null)
			try {
				reference = new DatabaseConnection(connection_url, username, password, database);
				
				Connect();
			} catch (Exception e) {
			    throw new DatabaseException(formatDatabaseConnectionError("Creating Database Connection",e));
			} 

		return reference;
	}
	
	private static String formatDatabaseConnectionError (String msg , Exception e){
		return ("Class: DatabaseConnection .  <" + e.toString() + ">  " + msg);
	}
	
	private static String formatDatabaseConnectionError (String msg){
		return ("Class: DatabaseConnection .  <" + ">  " + msg);
	}

	
	/**
	 * Load JDBC Driver
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private void loadJDBCDriver() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	}

	/**
	 * Private Constructor, Singelton Class. Use getDatabaseConnection()
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private DatabaseConnection(String connection_url, String username, String password, String database) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		this.setConnection_url(connection_url);
		this.setUsername(username);
		this.setPassword(password);
		this.setDatabase(database);
		
		loadJDBCDriver();
	}
	
	/**
	 *  Disconnect with the DataBase
	 */
	public void Disconnect(){
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException e) {
				XUnitAssert.fail(formatDatabaseConnectionError("Closing Database Connection",e));				
			}
		
		connection_established = false;
	}

	public boolean IsDatabaseConnectionInitiated() throws DatabaseException{
	    if (reference == null)
	        throw new DatabaseException(formatDatabaseConnectionError("DatabaseConnection not already Initiated"));
	    
	    return true;
	}
	
	public java.sql.Statement CreateStatement () throws DatabaseException{
	    try{
		    if (IsConnectionEstablised() && !IsConnectionNull()){
		        return connection.createStatement();    
		    }
	    }catch (SQLException e){
	        throw new DatabaseException(formatDatabaseConnectionError("Can't Create SQL Statement"));
	    }
	 
	    throw new DatabaseException(formatDatabaseConnectionError("No Exception but Still Can't Create SQL Statement"));   
	}
	
	public boolean IsConnectionNull() throws DatabaseException{
	    if (connection == null)
	        throw new DatabaseException(formatDatabaseConnectionError("Connection not Opened"));
	    
	    return false;
	}
	
	public boolean IsConnectionEstablised() throws DatabaseException{
	    if (connection.isClosed())
	        throw new DatabaseException(formatDatabaseConnectionError("Connection is Closed"));
	    
	    return true;
	}
	
	public boolean IsConnectionReadOnly() throws DatabaseException{
	    try{
		    if (connection.isReadOnly())
		        throw new DatabaseException(formatDatabaseConnectionError("Connection is ReadOnly"));
	    }catch(SQLException e){
	        throw new DatabaseException(formatDatabaseConnectionError("Connection is ReadOnly", e));
	    }
	    
	    return false;
	}

	
	/**
	 *  Connect with the DataBase
	 */
	public static void Connect(){
		try {
			connection = (Connection)(DriverManager.getConnection(prepareConnectionURL()));
		} catch (SQLException e) {
			XUnitAssert.fail(formatDatabaseConnectionError("Connecting to Database",e));
		}
		
		connection_established = true;
	}
	
	public static String prepareConnectionURL (){
		return (connection_url + database + "?user=" + username + "&password=" + password ) ;
	}
	
	public void setUsername (String usrname){
		username = usrname;
	}

	public void setPassword (String pwd){
		password = pwd;
	}
	
	public void setConnectionURL(String connect_url){
		connection_url = connect_url;
	}

	public boolean isConnectionEstablished (){
		return connection_established;
	}

	/**
	 * @return Returns the connection.
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection The connection to set.
	 */
	public void setConnection(Connection connection_) {
		connection = connection_;
	}

	/**
	 * @return Returns the connection_url.
	 */
	public String getConnection_url() {
		return connection_url;
	}

	/**
	 * @param connection_url The connection_url to set.
	 */
	public void setConnection_url(String connection_url_) {
		connection_url = connection_url_;
	}

	/**
	 * @return Returns the database.
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * @param database The database to set.
	 */
	public void setDatabase(String database_) {
		database = database_;
	}

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}
	
}

package xunit.test;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

public class TestClassJDBC {

	/*
	 *  JDBC Related Constructs
	 */
	private Connection connection 	= 	null;
	private Statement sql_statement = 	null;
	private ResultSet resultset 	= 	null;

	/*
	 *  URL Connection Constructs
	 */
	private String mysql_url 			=	null;
	private String connection_url 		=	null;
	private String username 			=	null;
	private String password 			=	null;
	private String database				= 	null;
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 */
	public TestClassJDBC() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		super();
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		this.connection_url = "";
		this.password = "";
		this.username = "";
	}

	public TestClassJDBC(String connection_url, String password, String username, String database) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		super();
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		this.connection_url = connection_url;
		this.password = password;
		this.username = username;
		this.database = database;
		getConnection();
	}
	
	public void getConnection() throws SQLException{
		this.mysql_url = (this.connection_url + "?user=" + this.username + "&password=" + this.password ) ;
		System.out.println(mysql_url);
		this.connection = (Connection)(DriverManager.getConnection(this.mysql_url.toString()));
		return;
	}
	
	public static void main(String[] args) {
		try {
			TestClassJDBC test = new TestClassJDBC("jdbc:mysql://localhost/", "fadsfads","root","xwiki");
			System.out.println("JDBC Driver Properly Loaded");
			System.out.println("Connection Properly Made");
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}

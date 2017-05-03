package Analysis.Data;

import java.sql.*;

public class MySQLConnection {
	
	private String url;
	private String userName;
	private String passWord;

	public MySQLConnection(String url, String userName, String passWord)
	{
		this.url = url;
		this.userName = userName;
		this.passWord = passWord;
	}
	
	public Connection DBConnection() throws ClassNotFoundException, SQLException
	{
		String driver = "com.mysql.jdbc.Driver";
		
		
		Class.forName(driver);
		
		Connection conn = DriverManager.getConnection(url, userName, passWord);
		
		if(!conn.isClosed())
		{
			System.out.println("Succeeded connecting to the Database!");
		}
		return conn;
	}
	
	public static void main(String[] args) {
		MySQLConnection mysqlconn = new MySQLConnection("jdbc:mysql://10.1.32.147:3306/perf_log", "admin","admin");
		try {
			mysqlconn.DBConnection();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

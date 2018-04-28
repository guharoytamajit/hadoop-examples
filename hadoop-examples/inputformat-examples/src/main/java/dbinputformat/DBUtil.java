package dbinputformat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/test";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	public static void populateRecord() {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.execute("drop table DBUSER if exists");
			stmt.close();
			stmt = conn.createStatement();
			stmt.execute("drop table employee  if exists");
			stmt.close();

			stmt = conn.createStatement();
			String createTableSQL = "CREATE TABLE DBUSER(" + "USER_ID NUMBER(5) NOT NULL, "
					+ "USERNAME VARCHAR(20) NOT NULL, " + "CREATED_BY VARCHAR(20) NOT NULL, " + "PRIMARY KEY (USER_ID) "
					+ ")";
			stmt.executeUpdate(createTableSQL);
			stmt.close();
			
			stmt = conn.createStatement();
			String createTableSQL2 = "CREATE TABLE employee(" + "id NUMBER(5) NOT NULL, "
					+ "name VARCHAR(20) NOT NULL, " +  "PRIMARY KEY (id) "
					+ ")";
			stmt.executeUpdate(createTableSQL2);
			stmt.close();
			
			stmt = conn.createStatement();
			stmt.executeUpdate(
					"INSERT INTO DBUSER" + "(USER_ID, USERNAME, CREATED_BY) " + "VALUES" + "(1,'tamajit','system')");
			stmt.close();
			stmt = conn.createStatement();
			stmt.executeUpdate(
					"INSERT INTO DBUSER" + "(USER_ID, USERNAME, CREATED_BY) " + "VALUES" + "(2,'john','tamajit')");
			stmt.close();
			stmt = conn.createStatement();
			stmt.executeUpdate(
					"INSERT INTO DBUSER" + "(USER_ID, USERNAME, CREATED_BY) " + "VALUES" + "(3,'mark','tamajit')");
			stmt.close();
			conn.close();

			Connection dbConnection = getConnection();
			String selectTableSQL = "SELECT USER_ID, USERNAME from DBUSER";
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery(selectTableSQL);
			while (rs.next()) {
				String userid = rs.getString("USER_ID");
				String username = rs.getString("USERNAME");
				System.out.println(userid + "  " + username);
			}
			dbConnection.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Goodbye!");
	}

	public static void checkRecord() {
		Connection dbConnection = null;
		try {
			dbConnection = getConnection();
			String selectTableSQL = "SELECT name, id from employee";
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery(selectTableSQL);
			while (rs.next()) {
				String username = rs.getString("name");
				int id = rs.getInt("id");
				System.out.println(id + "  " + username);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally

		{
			try {
				dbConnection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Connection conn;
		// STEP 1: Register JDBC driver
		Class.forName(JDBC_DRIVER);

		// STEP 2: Open a connection
		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		return conn;
	}
}

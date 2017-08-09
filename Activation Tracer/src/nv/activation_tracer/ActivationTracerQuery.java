package nv.activation_tracer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ActivationTracerQuery {
	
	//-----------------------------------------------------------------//
	
	/** Declare and initialize final variables **/
	
	private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	
	//-----------------------------------------------------------------//
	
	/** Declare global variables **/
	
	private String dbUrl;
	private String dbUsername;
	private String dbPassword;
	private String query;
	private ArrayList<ArrayList<String>> result;
	private Exception exceptionThrown;
	
	//-----------------------------------------------------------------//
	
	/** Constructors **/
	
	protected ActivationTracerQuery(String url, String uN, String pW, String q) {
		dbUrl = url;
		dbUsername = uN;
		dbPassword = pW;
		query = q;
		result = new ArrayList<ArrayList<String>>();
		exceptionThrown = null;
		
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				ArrayList<String> row = new ArrayList<String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					row.add(rs.getString(i));
				}
				result.add(row);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			exceptionThrown = e;
		} catch (Exception e) {
			exceptionThrown = e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				exceptionThrown = e;
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				exceptionThrown = e;
			}
		}
	}
	
	//-----------------------------------------------------------------//
	
	/** Private Methods **/
	
	//-----------------------------------------------------------------//
	
	/** Mutator Methods **/
	
	//-----------------------------------------------------------------//
	
	/** Accessor Methods **/
	
	protected ArrayList<ArrayList<String>> getResult() {
		return result;
	}
	
	protected Exception getException() {
		return exceptionThrown;
	}
	
	//-----------------------------------------------------------------//
	
}

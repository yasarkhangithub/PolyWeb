package org.insight.sels.config;

import java.sql.Connection;
import java.sql.DriverManager;

import org.insight.sels.datasources.DataSource;

/**
 * 
 * @author Yasar Khan
 *
 */
public class DBConnection {

	private static Connection dbConnection;
	
	public static Connection getConnection(DataSource datasource) {
        if (dbConnection != null) 
        	return dbConnection;
        
        return getConnection(datasource.getDataSourceURL(), datasource.getUserName(), datasource.getPassword());
    }
	
	
	private static Connection getConnection(String datasourceURL, String username, String password) {
		
		try {  
			
			Class.forName("com.mysql.jdbc.Driver");  
			
			dbConnection = DriverManager.getConnection(datasourceURL, username, password);   
			
		} catch(Exception e) { 
			System.out.println(e); 
		}
		
		return dbConnection;
	}
	
}

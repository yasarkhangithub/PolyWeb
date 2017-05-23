package org.insight.sels.test;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.insight.sels.config.Database;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.datasources.RDBDataSource;
import org.insight.sels.queryservice.DBQueryService;

/**
 * 
 * @author Yasar Khan
 *
 */
public class DbConnectionTest {
	
	String dbDriver = "com.mysql.jdbc.Driver";
	static String datasourceURL = "jdbc:mysql://localhost:3306/tcga_ov_cnv";
	
	
	public static void main(String args[]) {
		
		String queryString = "SELECT * FROM cnv_ov LIMIT 1;";
		
		DataSource ds = new RDBDataSource();
		ds.setDataSourceURL("jdbc:mysql://datasrv01.deri.ie:8085/tcga_ov_cnv");
		ds.setUserName("yaskha");
		ds.setPassword("mysql");
		
		try {
			Connection c = Database.getInstance(ds).getConnection();
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(queryString);
			while(rs.next()) {
				System.out.println("ID: " + rs.getInt("id"));
			}
		} catch (SQLException | IOException | PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		DBQueryService dbQueryService = new DBQueryService();
//		dbQueryService.executeQuery(queryString, datasourceURL);
//		dbQueryService.getSchema(datasourceURL);
		
	}

}

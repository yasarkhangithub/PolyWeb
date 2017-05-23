package org.insight.sels.test;

import org.insight.sels.queryservice.CSVQueryService;



/**
 * 
 * @author Yasar Khan
 *
 */
public class ApacheDrillConnectionTest {
	
	static String datasourceURL = "http://localhost:8047/query.json";

	public static void main(String[] args) {
		
//		testConnection();
		testGetSchema();
		
	}
	
	
	/**
	 * 
	 */
	public static void testConnection() {
		
		String query = "SELECT * FROM dfs.`D:/chromosome-1.tsv` LIMIT 5";
		
		CSVQueryService csvService = new CSVQueryService();
//		csvService.executeQuery(query, datasourceURL);
		
	}
	
	
	/**
	 * 
	 */
	public static void testGetSchema() {
		
		CSVQueryService csvService = new CSVQueryService();
//		csvService.getSchema(datasourceURL);
		
	}

}

package org.insight.sels.test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.insight.sels.queryservice.CSVQueryService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * 
 * @author Yasar Khan
 *
 */
public class ApacheDrillConnectionTest {
	
	static String datasourceURL = "http://localhost:8047/query.json";

	public static void main(String[] args) {
		
//		testConnection();
//		testGetSchema();
		getResults();
		
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
	
	public static void getResults() {
		
		String query = "SELECT Chromosome, Start_Position, End_Position "
				+ "FROM dfs.`/home/yaskha/Genomics/Polystore/Data/CNV/CNVD/Raw-Data/Homo_Sapiens/` "
				+ "WHERE Chromosome = '22' LIMIT 2";
		
		System.out.println(query);
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://datasrv01.deri.ie:8047/query.json");
		
		httpPost.addHeader("Content-Type", "application/json");

		try {
			String data =  "{\"queryType\":\"SQL\",\"query\":\""+query+"\"}"; 
			httpPost.setEntity(new StringEntity(data));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		HttpResponse response;
		Iterator<JsonNode> rowIter = null;
		try {
			response = httpClient.execute(httpPost);

			InputStreamReader inReader = new InputStreamReader(response.getEntity().getContent());

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(inReader);
			System.out.println(rootNode.toString());
			JsonNode columnsNode = rootNode.path("columns");
			JsonNode rowsNode = rootNode.path("rows"); 
			
			rowIter = rowsNode.elements();
			
			if(rowIter.hasNext()) {
				JsonNode row = rowIter.next();
			}

		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

}

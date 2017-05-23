package org.insight.sels.datasources;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.insight.sels.rml.Mapper;
import org.sels.insight.schema.Schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Yasar Khan
 *
 */
public class CSVDataSource extends DataSource {	
	
	public CSVDataSource() {
		super();
	}
	
	
	
	@Override
	public void generateSchema() {
		
//		Schema csvSchema = this.getSchema();
//		String query = "SELECT * FROM dfs.`" + this.getDirPath() + "` LIMIT 1";
//		
//		HttpClient httpClient = new DefaultHttpClient();
//		HttpPost httpPost = new HttpPost(this.getDataSourceURL());
//		
//		httpPost.addHeader("Content-Type", "application/json");
//
//		try {
//			String data =  "{\"queryType\":\"SQL\",\"query\":\""+query+"\"}"; 
//			httpPost.setEntity(new StringEntity(data));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		
//		HttpResponse response;
//		try {
//			response = httpClient.execute(httpPost);
//
//			InputStreamReader inReader = new InputStreamReader(response.getEntity().getContent());
//
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode rootNode = objectMapper.readTree(inReader);
//			
//			JsonNode columnsNode = rootNode.path("columns"); 
//			
//			List<Object> columnList = new ArrayList<Object>();
//			for (JsonNode valueNode : columnsNode) {
//				
//				if(valueNode.textValue().equals(new String("start_position"))) {
//					columnList.add("start");
//				} else if(valueNode.textValue().equals(new String("end_position"))) {
//					columnList.add("end");
//				} else {
//					columnList.add(valueNode.textValue());
//				}
//			}
//			csvSchema.setPropList(columnList);
//			
//
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		}
		
	}



	@Override
	public Boolean containPredicate(Node predicate) {
		Boolean contains = false;
		
		Mapper mapper = this.getMapper();
		Map<String, String> predMap = mapper.getPredicateToColumnMap();
		
		if(predMap.keySet().contains(predicate.toString()))
			contains = Boolean.TRUE;
		
//		String queryString = "PREFIX rr: <http://www.w3.org/ns/r2rml#> "
//				
//				+ "SELECT * "
//				
//				+ "WHERE { "
//				
//				+ "?tmap a rr:TriplesMap . "
//				+ "?tmap rr:predicateObjectMap ?poMap . "
//				+ "?poMap rr:predicate <" + predicate.toString() + "> . "
//				
//				+ " } ";
//		
//		Query query = QueryFactory.create(queryString);
//		
//		try (QueryExecution qexec = QueryExecutionFactory.create(query, getMapperModel())) {
//		    org.apache.jena.query.ResultSet results = qexec.execSelect() ;
//		    
//		    if(results.hasNext()) {
//		    	contains = Boolean.TRUE;
//		    }
//		  }
		
		return contains;
	}

}

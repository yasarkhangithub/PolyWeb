package org.insight.sels.queryservice;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.insight.sels.config.Config;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.QueryVar;
import org.insight.sels.query.SubQuery;
import org.insight.sels.query.TPGroup;
import org.insight.sels.querywriter.SPARQLToCSVQWriter;
import org.insight.sels.querywriter.URITemplate;
import org.insight.sels.result.CSVRSWrapper;
import org.insight.sels.result.PolyQuerySolution;
import org.insight.sels.result.PolyResultSet;
import org.insight.sels.result.RDFRSWrapper;
import org.insight.sels.result.ResultSetWrapper;
import org.insight.sels.util.StringUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * 
 * @author Yasar Khan
 *
 */
public class CSVQueryService_New implements QueryService_New {
	
	private DataSource csvDataSource;
	private TPGroup subQuery;
	private List<String> projectionList = new ArrayList<String>();
	private List<String> mainQueryProjList = new ArrayList<String>();

	@Override
	public ResultSetWrapper executeQuery(TPGroup subQuery, DataSource datasource) {
		
//		System.out.println(subQuery.getTpList().toString());
		
		csvDataSource = datasource;
		this.subQuery = subQuery;
		
		Config config = Config.getInstance();
		this.mainQueryProjList = config.getSparqlQuery().getProjectionList();
		
		String queryString = rewriteQuery();
		
		
//		System.out.println("CSV Query:" );
//		System.out.println("=========================================" );
//		System.out.println(queryString);
//		System.out.println("=========================================" );
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(csvDataSource.getDataSourceURL());
		
		httpPost.addHeader("Content-Type", "application/json");

		try {
			String data =  "{\"queryType\":\"SQL\",\"query\":\""+queryString+"\"}"; 
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
			JsonNode columnsNode = rootNode.path("columns");
			JsonNode rowsNode = rootNode.path("rows"); 
			
			rowIter = rowsNode.elements();

		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		ResultSetWrapper resultSetWrap = new CSVRSWrapper();
		resultSetWrap.setResultSet(rowIter);
		resultSetWrap.setSubQuery(subQuery);
		
		return resultSetWrap;
	}

	
	@Override
	public String rewriteQuery() {
		SPARQLToCSVQWriter sparqlToCSVWriter = new SPARQLToCSVQWriter();
		String queryString = sparqlToCSVWriter.rewriteQuery(subQuery, csvDataSource);
		
		return queryString;
	}
	
	
	
}

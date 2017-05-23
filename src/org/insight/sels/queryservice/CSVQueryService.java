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
import org.insight.sels.result.PolyQuerySolution;
import org.insight.sels.result.PolyResultSet;
import org.insight.sels.util.StringUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * 
 * @author Yasar Khan
 *
 */
public class CSVQueryService implements QueryService {
	
	private DataSource csvDataSource;
	private TPGroup subQuery;
	private List<String> projectionList = new ArrayList<String>();
	private List<String> mainQueryProjList = new ArrayList<String>();

	@Override
	public PolyResultSet executeQuery(TPGroup subQuery, DataSource datasource) {
		
//		System.out.println(subQuery.toString());
		
		csvDataSource = datasource;
		this.subQuery = subQuery;
		
		Config config = Config.getInstance();
		this.mainQueryProjList = config.getSparqlQuery().getProjectionList();
		
		String queryString = rewriteQuery();
		
		List<PolyQuerySolution> qSolList = new ArrayList<PolyQuerySolution>();
		PolyResultSet polyRset = new PolyResultSet();
		
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
		try {
			response = httpClient.execute(httpPost);

			InputStreamReader inReader = new InputStreamReader(response.getEntity().getContent());

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(inReader);
			JsonNode columnsNode = rootNode.path("columns");
			JsonNode rowsNode = rootNode.path("rows"); 
			
			Iterator<JsonNode> rowIter = rowsNode.elements();
			
			while(rowIter.hasNext()) {
				
				PolyQuerySolution qs = new PolyQuerySolution();
				Map<String, String> varToValueMap = new HashMap<String, String>();
				List<String> varList = new ArrayList<String>();
				
				JsonNode row = rowIter.next();
				
				Map<String, QueryVar> varMap = subQuery.getVarMap();
				Set<String> varKeySet = varMap.keySet();
				
				for (String varName : varKeySet) {
					
					QueryVar queryVar = varMap.get(varName);
//					String varAltName = queryVar.getAlternateName();
					List<String> varAltList = queryVar.getAlternateNameList();
					
					String varValue = null;
					
					if(queryVar.getIsTemplateVar()) {
						URITemplate uriTemplate = queryVar.getUriTemplate();
						Map<String, String> varValueMap = new HashMap<String, String>();
						for (String varAltName : varAltList) {
							JsonNode n = row.findValue(varAltName);
							if(n != null)
								varValueMap.put(varAltName, n.asText());
						}
						varValue = uriTemplate.putValuesInTemplate(varValueMap);
					} else {
						JsonNode n = row.findValue(varAltList.get(0));
						if(n != null)
							varValue = n.asText();
					}
					
//					String varValue = row.findValue(varAltName).asText();
//					
//					if(queryVar.getIsTemplateVar()) {
//						varValue = StringUtil.getTemplatedValue(queryVar.getTemplate(), varValue, varAltName);
//					}
					
					
					varToValueMap.put(varName, varValue);
					varList.addAll(varAltList);
//					System.out.println(varName + " = " + varValue);
				}
				
//				for (JsonNode colValueNode : columnsNode) {
//					
//					String columnName = colValueNode.textValue();
//					String rowValue = row.findValue(columnName).asText();
//					
//					varToValueMap.put(columnName, rowValue);
//					varList.add(columnName);
////					System.out.println(columnName + " = " + rowValue);
//				}
				
				qs.setVarToValueMap(varToValueMap);
				qs.setVarList(mainQueryProjList);
				qSolList.add(qs);
				
			}
			
			polyRset.setQuerySolList(qSolList);

		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return polyRset;
	}

	
	@Override
	public String rewriteQuery() {
		SPARQLToCSVQWriter sparqlToCSVWriter = new SPARQLToCSVQWriter();
		String queryString = sparqlToCSVWriter.rewriteQuery(subQuery, csvDataSource);
		
		return queryString;
	}


	@Override
	public PolyResultSet executeQuery(SubQuery subQuery) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	@Override
//	public String rewriteQuery() {
//		
//		List<Triple> tripleList = subQuery.getTpList();
//		
//		String query = "SELECT ";
//		
//		for (Triple triple : tripleList) {
//			
//			Node predicate = triple.getPredicate();
//			
//			if(predicate.isURI()) {
//				String predicateLocalName = predicate.getLocalName();
//				
//				
//				if(predicateLocalName.equals(new String("start"))) {
//					predicateLocalName = "start_position";
//				} else if(predicateLocalName.equals(new String("end"))) {
//					predicateLocalName = "end_position";
//				}
//				projectionList.add(predicateLocalName);
//			}
//			
//		}
//		
//		int count = 1;
//		for (String projVar : projectionList) {
//			
//			query += projVar;
//			
//			if(count < projectionList.size()) {
//				query += ", ";
//			}
//			
//			count++;
//		}
//		
//		query += " FROM dfs.`" + csvDataSource.getDirPath() + "` LIMIT 5";
//		
//		return query;
//		
//	}
	
	
	
}

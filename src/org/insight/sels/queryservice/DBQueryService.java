package org.insight.sels.queryservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.insight.sels.config.Config;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.QueryVar;
import org.insight.sels.query.SQLQuery;
import org.insight.sels.query.SubQuery;
import org.insight.sels.query.EExclusiveGroup;
import org.insight.sels.querywriter.SPARQLToSQLWriter;
import org.insight.sels.querywriter.URITemplate;
import org.insight.sels.result.PolyQuerySolution;
import org.insight.sels.result.PolyResultSet;
import org.insight.sels.rml.R2RMLMapper;
import org.insight.sels.util.StringUtil;

/**
 * 
 * @author Yasar Khan
 *
 */
public class DBQueryService implements QueryService {

	private DataSource rdbDataSource;
	private Connection dbConn;
	private EExclusiveGroup subQuery;
	private List<String> mainQueryProjList = new ArrayList<String>();
	private List<String> dbProjectionList = new ArrayList<String>();
	
	
	@Override
	public PolyResultSet executeQuery(EExclusiveGroup subQuery, DataSource datasource) {
		
		this.rdbDataSource = datasource;
		this.subQuery = subQuery;
		Config config = Config.getInstance();
		this.mainQueryProjList = config.getSparqlQuery().getProjectionList();
		
		String queryString = rewriteQuery();
		
		List<PolyQuerySolution> qSolList = new ArrayList<PolyQuerySolution>();
		PolyResultSet polyRset = new PolyResultSet();
		
//		System.out.println("RDB SQL Query: " );
//		System.out.println("=========================================" );
//		System.out.println(queryString);
//		System.out.println("=========================================" );
		
		try {
			connectDB();
			Statement stmt = dbConn.createStatement();
			ResultSet rs = stmt.executeQuery(queryString);
			
			Map<String, QueryVar> varMap = subQuery.getVarMap();
			Set<String> varKeySet = varMap.keySet();
			
			while(rs.next()) {
				PolyQuerySolution qs = new PolyQuerySolution();
				Map<String, String> varToValueMap = new HashMap<String, String>();
				
				for (String varName : varKeySet) {
					
					QueryVar queryVar = varMap.get(varName);
					List<String> varAltList = queryVar.getAlternateNameList();
					
					String varValue = null;
					
					if(queryVar.getIsTemplateVar()) {
						URITemplate uriTemplate = queryVar.getUriTemplate();
						Map<String, String> varValueMap = new HashMap<String, String>();
						for (String varAltName : varAltList) {
							varValueMap.put(varAltName, rs.getString(varAltName));
						}
						varValue = uriTemplate.putValuesInTemplate(varValueMap);
					} else {
						varValue = rs.getString(varAltList.get(0));
					}
					
//					String varValue = rs.getString(varAltName);
//					
//					if(queryVar.getIsTemplateVar()) {
//						varValue = StringUtil.getTemplatedValue(queryVar.getTemplate(), varValue, varAltName.split("\\.")[1]);
//					}
					
					
					varToValueMap.put(varName, varValue);
//					System.out.println(varName + " = " + varValue);
				}
				
				qs.setVarToValueMap(varToValueMap);
				qs.setVarList(mainQueryProjList);
				qSolList.add(qs);
				
			}
			
			polyRset.setQuerySolList(qSolList);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				dbConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return polyRset;
	}
	

	@Override
	public String rewriteQuery() {
		SPARQLToSQLWriter sqlWriter = new SPARQLToSQLWriter();
		SQLQuery sqlQuery = sqlWriter.rewriteQuery(subQuery, rdbDataSource);
		String queryString = sqlQuery.getQueryString();
		
		return queryString;
	}
	
//	@Override
//	public String rewriteQuery() {
//		
//		Config config = Config.getInstance();
//		mainQueryProjList = config.getSparqlQuery().getProjectionList();
//		
//		List<Triple> tripleList = subQuery.getTpList();
//		Map<String, QueryVar> varMap = subQuery.getVarMap();
//		
//		R2RMLMapper queryRML = new R2RMLMapper();
//		
//		String projection = "";
//		String from = " FROM ";
//		
//		List<String> tableList = new ArrayList<String>();
//		
////		Schema rdbSchema = rdbDataSource.getSchema();
////		Map<Object, Object> columnToTableMap = rdbSchema.getPropToConceptMap();
//		
//		
//		
//		for (Triple triple : tripleList) {
//			
//			Node subject = triple.getSubject();
//			Node predicate = triple.getPredicate();
//			Node object = triple.getObject();
//			
//			if(predicate.isURI()) {
//				
//				Map<String, String> columnToTableMap = queryRML.getColumnName(predicate.toString());
//				
////				Table table = (Table) columnToTableMap.get(predicateLocalName);
////				String tableName = table.getName();
//				String columnName = columnToTableMap.keySet().iterator().next();
//				String tableName = columnToTableMap.get(columnName);
//				
//				String objectAltName = tableName + "." + columnName;
//				
//				dbProjectionList.add(objectAltName);
//				
//				if(!tableList.contains(tableName)) {
//					tableList.add(tableName);
//				}
//				
//				if(subject.isVariable()) {
//					String varName = subject.getName();
//					
//					QueryVar queryVar = varMap.get(varName);
//					if(queryVar == null) {
//						queryVar = new QueryVar();
//						queryVar.setVarName(varName);
//						queryVar.getIsDomainOf().add(predicate);
//						queryVar.setIsDomain(Boolean.TRUE);
//						
//						if(mainQueryProjList.contains(varName)) {
//							queryVar.setIsResultVar(Boolean.TRUE);
//						}
//						
//						varMap.put(varName, queryVar);
//					} else {
//						queryVar.getIsDomainOf().add(predicate);
//						queryVar.setIsDomain(Boolean.TRUE);
//					}
//				}
//				
//				if(object.isVariable()) {
//					String varName = object.getName();
//					
//					QueryVar queryVar = varMap.get(varName);
//					if(queryVar == null) {
//						queryVar = new QueryVar();
//						queryVar.setVarName(varName);
//						queryVar.setAlternateName(objectAltName);
//						queryVar.setIsRangeOf(predicate);
//						queryVar.setIsRange(Boolean.TRUE);
//						
//						if(mainQueryProjList.contains(varName)) {
//							queryVar.setIsResultVar(Boolean.TRUE);
//						}
//						
//						varMap.put(varName, queryVar);
//					}
//					
//				}
//				
//				
//			}
//			
//		}
//		
//		Set<String> varKeySet = varMap.keySet();
//		for (String varName : varKeySet) {
//			QueryVar var = varMap.get(varName);
//			String altName = var.getAlternateName();
//			if(altName == null) {
////				System.out.println("Var Name: " + varName);
//				queryRML.getSubjectDetails(var.getIsDomainOf(), var);
//				dbProjectionList.add(var.getAlternateName());
//			}
//			
//		}
//		
//		int count = 1;
//		for (String proj : dbProjectionList) {
//			projection += proj;
//			
//			if(count < dbProjectionList.size())
//				projection += ",";
//			
//			count++;
//		}
//		
//		
//		for (String tableStr : tableList) {
//			from += tableStr;
//		}
//		
//		String query = "SELECT " + projection + from + " LIMIT 5";
//		
//		return query;
//		
//	}
	
	
	/**
	 * Connection to Database
	 */
	public void connectDB() {

		try {  
			
			Class.forName("com.mysql.jdbc.Driver");  
			
			dbConn = DriverManager.getConnection(rdbDataSource.getDataSourceURL(), rdbDataSource.getUserName(), rdbDataSource.getPassword());   
			
		} catch(Exception e) { 
			System.out.println(e); 
			}
	}


	@Override
	public PolyResultSet executeQuery(SubQuery subQuery) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

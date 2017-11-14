package org.insight.sels.datasources;

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
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.insight.sels.config.DBConnection;
import org.insight.sels.rml.Mapper;
import org.insight.sels.rml.R2RMLMapper;
import org.insight.sels.rml.TripleMap;
import org.insight.sels.structures.Table;
import org.sels.insight.schema.Schema;

/**
 * 
 * @author Yasar Khan
 *
 */
public class RDBDataSource extends DataSource {
	
	private Connection dbConn;
	
	public RDBDataSource() {
		super();
	}

	public Connection getDbConn() {
		return dbConn;
	}

	public void setDbConn(Connection dbConn) {
		this.dbConn = dbConn;
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



	@Override
	public void generateSchema() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void predicateNullCheck() {
		
		String nullCheckQuery = "";
		Connection dbConn = DBConnection.getConnection(this);
		
		Mapper mapper = this.getMapper();
		Set<TripleMap> tripleMapSet = mapper.getTripleMapSet();
		
		for (TripleMap tripleMap : tripleMapSet) {
			String type = tripleMap.getSubjectType();
			Set<String> predSet = tripleMap.getPredicateSet();
			String source = tripleMap.getSource();
			
			for (String predicate : predSet) {
				String column = mapper.getColumn(predicate);
				nullCheckQuery = "SELECT " + column + " FROM " + source + " WHERE " + column + " IS NULL LIMIT 1";
				
				ResultSet rs = null;
				Statement stmt = null;
				
				try {
					stmt = dbConn.createStatement();
					rs = stmt.executeQuery(nullCheckQuery);
					if(rs.next()) {
//						System.err.println("Predicate " + predicate + " is Null. ");
						this.getNullPredicates().add(predicate);
					} else {
//						System.out.println("Predicate " + predicate + " is Not Null. ");
					}
					
				} catch (SQLException e) {
					try { 
						if(stmt != null) 
							stmt.close(); 
						if(rs != null) 
							rs.close(); 
						if(dbConn != null) 
							dbConn.close(); 
						} catch (SQLException e1) { e1.printStackTrace(); }
					e.printStackTrace();
				} finally {
					try { 
						if(stmt != null) 
							stmt.close(); 
						if(rs != null) 
							rs.close();  
						} catch (SQLException e1) { e1.printStackTrace(); }
				}
			}
		}
		
//		if(dbConn != null)
//			try {
//				dbConn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		
		
	}

}

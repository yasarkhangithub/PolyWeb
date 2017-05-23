package org.insight.sels.datasources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.insight.sels.config.DBConnection;
import org.insight.sels.rml.Mapper;
import org.insight.sels.rml.R2RMLMapper;
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

}

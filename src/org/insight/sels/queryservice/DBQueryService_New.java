package org.insight.sels.queryservice;

import java.beans.PropertyVetoException;
import java.io.IOException;
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
import org.insight.sels.config.DBConnection;
import org.insight.sels.config.Database;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.QueryVar;
import org.insight.sels.query.SQLQuery;
import org.insight.sels.query.SubQuery;
import org.insight.sels.query.EExclusiveGroup;
import org.insight.sels.querywriter.SPARQLToSQLWriter;
import org.insight.sels.querywriter.URITemplate;
import org.insight.sels.result.PolyQuerySolution;
import org.insight.sels.result.PolyResultSet;
import org.insight.sels.result.RDBRSWrapper;
import org.insight.sels.result.ResultSetWrapper;
import org.insight.sels.rml.R2RMLMapper;
import org.insight.sels.util.StringUtil;

/**
 * 
 * @author Yasar Khan
 *
 */
public class DBQueryService_New implements QueryService_New {

	private DataSource rdbDataSource;
	private Connection dbConn;
	private EExclusiveGroup subQuery;
	private List<String> mainQueryProjList = new ArrayList<String>();
	private List<String> dbProjectionList = new ArrayList<String>();
	
	
	@Override
	public ResultSetWrapper executeQuery(EExclusiveGroup subQuery, DataSource datasource) {
		
		this.rdbDataSource = datasource;
		this.subQuery = subQuery;
		Config config = Config.getInstance();
		this.mainQueryProjList = config.getSparqlQuery().getProjectionList();
		
		String queryString = rewriteQuery();
		
//		System.out.println("RDB SQL Query: " );
//		System.out.println("=========================================" );
//		System.out.println(queryString);
//		System.out.println("=========================================" );
		
		ResultSet rs = null;
		Statement stmt = null;
		try {
//			dbConn = Database.getInstance(datasource).getConnection();
			dbConn = DBConnection.getConnection(datasource);
			stmt = dbConn.createStatement();
			rs = stmt.executeQuery(queryString);
			
			
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
		} 
		
		ResultSetWrapper resultSetWrap = new RDBRSWrapper();
		resultSetWrap.setResultSet(rs);
		resultSetWrap.setSubQuery(subQuery);
		
		return resultSetWrap;
	}
	

	@Override
	public String rewriteQuery() {
		SPARQLToSQLWriter sqlWriter = new SPARQLToSQLWriter();
		SQLQuery sqlQuery = sqlWriter.rewriteQuery(subQuery, rdbDataSource);
		String queryString = sqlQuery.getQueryString();
		
		return queryString;
	}
	
	
}

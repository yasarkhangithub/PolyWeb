package org.insight.sels.config;

import java.util.ArrayList;
import java.util.List;

import org.insight.sels.datasources.DataSource;
import org.insight.sels.stats.QueryExecutionStats;
import org.insight.sels.util.DataSourceUtil;
import org.insight.sels.util.FileUtility;
import org.sels.insight.schema.SPARQLQuery;

/**
 * 
 * @author Yasar Khan
 *
 */
public class Config {
	
	private static Config config = null;
	private SPARQLQuery sparqlQuery;
	
	private List<DataSource> dataSourceList;
//	
//	String datasourcesFilePath = "config/datasources.json";
//	String datasourcesFilePath = "config/datasources-rdf.json";
	String datasourcesFilePath = "config/datasources-mysql-local.json";
	private String queryPath = "config/queries/" + "QE-9";
	
	private QueryExecutionStats queryExecStats;
	
	/**
	 * Constructor
	 */
	private Config() {
		dataSourceList = new ArrayList<DataSource>();
		
		String queryString = FileUtility.readQueryString(queryPath);
		sparqlQuery = new SPARQLQuery(queryString);
		
		queryExecStats = new QueryExecutionStats();
	}
	
	
	/**
	 * This method returns an object of Config.
	 * @return
	 */
	public static Config getInstance() {
		
		if(config == null) {
			config = new Config();
		}
		
		return config;
	}
	
	
	/**
	 * This method initializes all the data sources and loads schemas of all the data sources.
	 */
	public void initialize() {
		
		dataSourceList = DataSourceUtil.getDataSourceList(datasourcesFilePath);
		
		System.out.println("Number of Data Sources: " + dataSourceList.size());
		
		for (DataSource dataSource : dataSourceList) {
			dataSource.generateSchema();
		}
		
		sparqlQuery.parseSPARQLQuery();
		
	}

	
	
	public SPARQLQuery getSparqlQuery() {
		return sparqlQuery;
	}


	public void setSparqlQuery(SPARQLQuery sparqlQuery) {
		this.sparqlQuery = sparqlQuery;
	}


	public List<DataSource> getDataSourceList() {
		return dataSourceList;
	}


	public void setDataSourceList(List<DataSource> dataSourceList) {
		this.dataSourceList = dataSourceList;
	}


	public QueryExecutionStats getQueryExecStats() {
		return queryExecStats;
	}


	public void setQueryExecStats(QueryExecutionStats queryExecStats) {
		this.queryExecStats = queryExecStats;
	}

	
	

}

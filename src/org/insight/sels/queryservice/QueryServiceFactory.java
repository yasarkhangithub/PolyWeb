package org.insight.sels.queryservice;

import org.insight.sels.datasources.CSVDataSource;
import org.insight.sels.datasources.RDBDataSource;
import org.insight.sels.datasources.RDFDataSource;

/**
 * 
 * @author Yasar Khan
 *
 */
public class QueryServiceFactory {
	
	public QueryService getQueryService(String dataSourceType) {
		
		if(dataSourceType == null) {
			return null;
		}
		
		if(dataSourceType.equalsIgnoreCase("RDF")) {
			return new RDFQueryService();
		} else if(dataSourceType.equalsIgnoreCase("RDB")) {
			return new DBQueryService();
		} else if(dataSourceType.equalsIgnoreCase("CSV")) {
			return new CSVQueryService();
		}
		
		return null;
		
	}

}

package org.insight.sels.queryservice;

import org.insight.sels.datasources.CSVDataSource;
import org.insight.sels.datasources.RDBDataSource;
import org.insight.sels.datasources.RDFDataSource;

/**
 * 
 * @author Yasar Khan
 *
 */
public class QueryServiceFactory_New {
	
	public QueryService_New getQueryService(String dataSourceType) {
		
		if(dataSourceType == null) {
			return null;
		}
		
		if(dataSourceType.equalsIgnoreCase("RDF")) {
			return new RDFQueryService_New();
		} else if(dataSourceType.equalsIgnoreCase("RDB")) {
			return new DBQueryService_New();
		} else if(dataSourceType.equalsIgnoreCase("CSV")) {
			return new CSVQueryService_New();
		}
		
		return null;
		
	}

}

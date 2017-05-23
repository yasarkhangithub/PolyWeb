package org.insight.sels.result.transform;

import org.insight.sels.queryservice.CSVQueryService;
import org.insight.sels.queryservice.DBQueryService;
import org.insight.sels.queryservice.QueryService;
import org.insight.sels.queryservice.RDFQueryService;

/**
 * 
 * @author Yasar Khan
 *
 */
public class ResultTransformFactory {

	public ResultTransform getResultTransform(String dataSourceType) {
		
		if(dataSourceType == null) {
			return null;
		}
		
		if(dataSourceType.equalsIgnoreCase("RDF")) {
			return new RDFResultTransform();
		} else if(dataSourceType.equalsIgnoreCase("RDB")) {
			return new RDBResultTransform();
		} else if(dataSourceType.equalsIgnoreCase("CSV")) {
			return new CSVResultTransform();
		}
		
		return null;
		
	}
	
}

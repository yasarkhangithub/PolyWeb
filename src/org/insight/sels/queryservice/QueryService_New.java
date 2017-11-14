package org.insight.sels.queryservice;

import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.EExclusiveGroup;
import org.insight.sels.result.ResultSetWrapper;

/**
 * 
 * @author Yasar Khan
 *
 */
public interface QueryService_New {
	
	public ResultSetWrapper executeQuery(EExclusiveGroup subQuery, DataSource datasource);
	
	public String rewriteQuery();

}

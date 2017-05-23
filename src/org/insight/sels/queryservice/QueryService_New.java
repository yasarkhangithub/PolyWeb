package org.insight.sels.queryservice;

import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.TPGroup;
import org.insight.sels.result.ResultSetWrapper;

/**
 * 
 * @author Yasar Khan
 *
 */
public interface QueryService_New {
	
	public ResultSetWrapper executeQuery(TPGroup subQuery, DataSource datasource);
	
	public String rewriteQuery();

}

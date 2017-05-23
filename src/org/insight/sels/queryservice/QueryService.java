package org.insight.sels.queryservice;

import java.util.List;

import org.apache.jena.graph.Triple;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.SubQuery;
import org.insight.sels.query.TPGroup;
import org.insight.sels.result.PolyQuerySolution;
import org.insight.sels.result.PolyResultSet;

/**
 * 
 * @author Yasar Khan
 *
 */
public interface QueryService {
	
	public PolyResultSet executeQuery(TPGroup subQuery, DataSource datasource);
	
	public PolyResultSet executeQuery(SubQuery subQuery);
	
	public String rewriteQuery();

}

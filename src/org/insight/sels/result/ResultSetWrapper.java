package org.insight.sels.result;

import org.insight.sels.query.TPGroup;

/**
 * 
 * @author Yasar Khan
 *
 */
public interface ResultSetWrapper {

	public PolyQuerySolution getNextResult(); 
	
	public void setResultSet(Object resultSet);
	
	public void setSubQuery(TPGroup subQuery);
	
}

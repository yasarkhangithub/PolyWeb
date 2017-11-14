package org.insight.sels.result;

import org.insight.sels.query.EExclusiveGroup;

/**
 * 
 * @author Yasar Khan
 *
 */
public interface ResultSetWrapper {

	public PolyQuerySolution getNextResult(); 
	
	public void setResultSet(Object resultSet);
	
	public void setSubQuery(EExclusiveGroup subQuery);
	
}

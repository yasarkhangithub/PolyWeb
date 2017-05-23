package org.insight.sels.result.transform;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.insight.sels.query.QueryVar;
import org.insight.sels.query.TPGroup;
import org.insight.sels.result.PolyQuerySolution;

/**
 * 
 * @author Yasar Khan
 *
 */
public abstract class ResultTransform {
	
	private TPGroup subQuery;
	protected Map<String, QueryVar> varMap;
	protected Set<String> varKeySet;
	
	public abstract PolyQuerySolution tranform(Object querySolution) throws SQLException;

	public TPGroup getSubQuery() {
		return subQuery;
	}

	public void setSubQuery(TPGroup subQuery) {
		this.subQuery = subQuery;
		this.setVarMap(subQuery.getVarMap());
		this.setVarKeySet(getVarMap().keySet());
	}

	public Map<String, QueryVar> getVarMap() {
		return varMap;
	}

	public void setVarMap(Map<String, QueryVar> varMap) {
		this.varMap = varMap;
	}

	public Set<String> getVarKeySet() {
		return varKeySet;
	}

	public void setVarKeySet(Set<String> varKeySet) {
		this.varKeySet = varKeySet;
	}

}

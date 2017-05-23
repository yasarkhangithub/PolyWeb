package org.insight.sels.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * 
 * @author Yasar Khan
 *
 */
public class PolyResultSet implements Iterator<PolyQuerySolution> {

	private List<PolyQuerySolution> querySolList;
	private List<String> varNames = new ArrayList<String>();
	private int index;
	
	
	public PolyResultSet() {
		querySolList = new ArrayList<PolyQuerySolution>();
		this.index = 0;
	}
	
	public PolyResultSet(List<PolyQuerySolution> querySolList) {
		this.querySolList = querySolList;
		this.index = 0;
	}
	
	
	
	@Override
	public boolean hasNext() {
		
		if(querySolList.size() == index) {
			return false;
		} else {
			return true;
		}
		
	}

	
	
	@Override
	public PolyQuerySolution next() {
		
		if(hasNext()) {
			return querySolList.get(index++);
		} else {
			return null;
		}
		
	}

	public List<String> getVarNames() {
		return varNames;
	}

	public void setVarNames(List<String> varNames) {
		this.varNames = varNames;
	}

	public List<PolyQuerySolution> getQuerySolList() {
		return querySolList;
	}

	public void setQuerySolList(List<PolyQuerySolution> querySolList) {
		this.querySolList = querySolList;
	}


}

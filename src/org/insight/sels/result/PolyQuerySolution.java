package org.insight.sels.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Yasar Khan
 *
 */
public class PolyQuerySolution {
	
	private Map<String, String> varToValueMap = new HashMap<String, String>();
	private List<String> varList = new ArrayList<String>();

	
	/**
	 * This method returns the value of the provided variable name in var in the PolyQuerySolution
	 * 
	 * @param var
	 * @return
	 */
	public String getValue(String var) {
		
		String value = varToValueMap.get(var);
		
		return value;
	}
	
	
	
	public Map<String, String> getVarToValueMap() {
		return varToValueMap;
	}

	
	
	public void setVarToValueMap(Map<String, String> varToValueMap) {
		this.varToValueMap = varToValueMap;
	}



	public List<String> getVarList() {
		return varList;
	}



	public void setVarList(List<String> varList) {
		this.varList = varList;
	}
	
}

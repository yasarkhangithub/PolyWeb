package org.insight.sels.rml;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Yasar Khan
 *
 */
public abstract class Mapper {
	
	protected Map<String, String> predicateToColumnMap = new HashMap<String, String>();
	protected Map<String, String> columnToSourceMap = new HashMap<String, String>();
	protected Map<String, String> predicateToTemplateMap = new HashMap<String, String>();
	Set<TripleMap> tripleMapSet = new HashSet<TripleMap>();
	
	public abstract void populatePredMap();
	
	public abstract void populateColumnMap();
	
	public abstract void populateTemplateMap();
	
	public abstract void populateTripleMapSet();

	
	public String getColumn(String predicate) {
		return predicateToColumnMap.get(predicate);
	}
	
	
	public String getSource(String column) {
		return columnToSourceMap.get(column);
	}
	
	
	public String getTemplate(String predicate) {
		return predicateToTemplateMap.get(predicate);
	}
	
	
	public String getSubjectTemplate(Set<String> predicateSet) {
//		System.out.println("Set 1:================== " + predicateSet.toString());
		String subTemplate = null;
		int maxSize = 0;
		for (TripleMap tMap : tripleMapSet) {
			Set<String> pSet = new HashSet<String>(tMap.getPredicateSet());
//			System.out.println("Set 2:================= " + pSet.toString());
			
			boolean flag = pSet.retainAll(predicateSet);
			
			if(flag) {
				int setSize = pSet.size();
				if(setSize > maxSize) {
					maxSize = setSize;
					subTemplate = tMap.getSubjectTemplate();
				}
			}
			
		}
		
		return subTemplate;
	}
	
	
	public Map<String, String> getPredicateToColumnMap() {
		return predicateToColumnMap;
	}
	
	

	public void setPredicateToColumnMap(Map<String, String> predicateToColumnMap) {
		this.predicateToColumnMap = predicateToColumnMap;
	}
	
	

	public Map<String, String> getColumnToSourceMap() {
		return columnToSourceMap;
	}

	
	
	public void setColumnToSourceMap(Map<String, String> columnToSourceMap) {
		this.columnToSourceMap = columnToSourceMap;
	}

	
	
	public Map<String, String> getPredicateToTemplateMap() {
		return predicateToTemplateMap;
	}

	
	
	public void setPredicateToTemplateMap(Map<String, String> predicateToTemplateMap) {
		this.predicateToTemplateMap = predicateToTemplateMap;
	}

}

package org.sels.insight.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;

/**
 * 
 * @author Yasar Khan
 *
 */
public class Schema {
	
	private List<Object> conceptList;
	private List<Object> propList;
	private Map<Object, Object> propToConceptMap;
	
	
	public Schema() {
		conceptList = new ArrayList<Object>();
		propList = new ArrayList<Object>();
		propToConceptMap = new HashMap<Object, Object>();
	}
	
	
	public Boolean contains(Node prop) {
		
		Boolean flag = Boolean.FALSE;
		
		if(propList.contains(prop.getLocalName()) || propList.contains(prop.getURI())) {
			flag = Boolean.TRUE;
		}
		
		return flag;
	}
	
	
	public List<Object> getConceptList() {
		return conceptList;
	}
	
	
	public void setConceptList(List<Object> conceptList) {
		this.conceptList = conceptList;
	}
	
	
	public List<Object> getPropList() {
		return propList;
	}
	
	
	public void setPropList(List<Object> propList) {
		this.propList = propList;
	}
	
	
	public Map<Object, Object> getPropToConceptMap() {
		return propToConceptMap;
	}
	
	
	public void setPropToConceptMap(Map<Object, Object> propToConceptMap) {
		this.propToConceptMap = propToConceptMap;
	}
	
	

}

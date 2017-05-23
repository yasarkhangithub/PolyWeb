package org.insight.sels.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Yasar Khan
 *
 */
public class Subject {
	
	private String uri;
	private String localName;
	private List<Predicate> predicateList = new ArrayList<Predicate>();
	
	@Override
	public String toString() {
		String subjectString = "";
		
		subjectString += "[ " + this.getLocalName() + " ] ---> ";
		subjectString += " { ";
		
		for (Predicate predicate : predicateList) {
			subjectString += " ( " + predicate.getLocalName() + " ) ";
		}
		
		subjectString += " } ";
		
		return subjectString;
	}
	
	
	public String getUri() {
		return uri;
	}
	
	
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	
	
	public String getLocalName() {
		return localName;
	}
	
	
	
	public void setLocalName(String localName) {
		this.localName = localName;
	}
	
	
	
	public List<Predicate> getPredicateList() {
		return predicateList;
	}
	
	
	
	public void setPredicateList(List<Predicate> predicateList) {
		this.predicateList = predicateList;
	}

}

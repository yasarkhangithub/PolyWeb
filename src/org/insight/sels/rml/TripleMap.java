package org.insight.sels.rml;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Yasar Khan
 *
 */
public class TripleMap {
	
	private String source;
	private String subjectTemplate;
	private String subjectType;
	private Set<String> predicateSet = new HashSet<String>();
	
	
	public String getSource() {
		return source;
	}
	
	
	public void setSource(String source) {
		this.source = source;
	}
	
	
	
	public String getSubjectTemplate() {
		return subjectTemplate;
	}
	
	
	
	public void setSubjectTemplate(String subjectTemplate) {
		this.subjectTemplate = subjectTemplate;
	}
	
	
	
	public String getSubjectType() {
		return subjectType;
	}
	
	
	
	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}
	
	
	
	public Set<String> getPredicateSet() {
		return predicateSet;
	}
	
	
	
	public void setPredicateSet(Set<String> predicateSet) {
		this.predicateSet = predicateSet;
	}
	
	

}

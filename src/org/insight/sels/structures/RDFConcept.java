package org.insight.sels.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Yasar Khan
 *
 */
public class RDFConcept {

	private String uri;
	private String localName;
	private List<Predicate> predList;
	
	
	public RDFConcept() {
		predList = new ArrayList<Predicate>();
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
	
	
	public List<Predicate> getPredList() {
		return predList;
	}
	
	
	public void setPredList(List<Predicate> predList) {
		this.predList = predList;
	}
	
	
}

package org.sels.insight.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insight.sels.structures.Predicate;
import org.insight.sels.structures.RDFConcept;

/**
 * 
 * @author Yasar Khan
 *
 */
public class RDFSchema {
	
	private List<RDFConcept> conceptList;
	private Map<Predicate, RDFConcept> predToConceptMap;
	
	public RDFSchema() {
		conceptList = new ArrayList<RDFConcept>();
		predToConceptMap = new HashMap<Predicate, RDFConcept>();
	}
	
	
	@Override
	public String toString() {
		
		String rdfSchemaString = "-------------------------------------------------------------" + System.lineSeparator();
		rdfSchemaString += "No.\t" + "Concept\t\t" + "Predicates" + System.lineSeparator();
		rdfSchemaString += "-------------------------------------------------------------" + System.lineSeparator();
		
		int count = 0;
		for (RDFConcept rdfConcept : conceptList) {
			
			count++;
			
			rdfSchemaString += count + "\t" + rdfConcept.getLocalName() + "\t\t";
			
			List<Predicate> predList = rdfConcept.getPredList();
			
			for (Predicate predicate : predList) {
				rdfSchemaString += predicate.getLocalName() + ", ";
			}
			
			rdfSchemaString += System.lineSeparator();
			
		}
		
		
		return rdfSchemaString;
	}
	
	
	public List<RDFConcept> getConceptList() {
		return conceptList;
	}
	
	
	public void setConceptList(List<RDFConcept> conceptList) {
		this.conceptList = conceptList;
	}
	
	
	public Map<Predicate, RDFConcept> getPredToConceptMap() {
		return predToConceptMap;
	}
	
	
	public void setPredToConceptMap(Map<Predicate, RDFConcept> predToConceptMap) {
		this.predToConceptMap = predToConceptMap;
	}
	
	

}

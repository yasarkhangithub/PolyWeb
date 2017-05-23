package org.insight.sels.datasources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.insight.sels.structures.Predicate;
import org.insight.sels.structures.RDFConcept;
import org.sels.insight.schema.Schema;

/**
 * 
 * @author Yasar Khan
 *
 */
public class RDFDataSource extends DataSource {
	
	@Override
	public void generateSchema() {
		
		Schema rdfSchema = this.getSchema();
		
		String queryString = "SELECT DISTINCT ?predicate WHERE { "
				+ "?subject ?predicate ?object. "
				+ "}";
		
		Query query = QueryFactory.create(queryString);
		List<String> graphs = this.getDefaultGraphList();
		for (String graph : graphs) {
			query.addGraphURI(graph);
		}
		QueryExecution qExe = QueryExecutionFactory.sparqlService(this.getDataSourceURL(), query);	
		ResultSet resultSet = qExe.execSelect();
		
		List<Object> propList = new ArrayList<Object>();
		while(resultSet.hasNext()) {
			QuerySolution qSol = resultSet.next();
			Resource predicateRes = qSol.get("predicate").asResource();
			String predicateURI = predicateRes.getURI();
			propList.add(predicateURI);
		}
		
		rdfSchema.setPropList(propList);
		
	}

//	@Override
//	public void generateSchema() {
//		
//		Schema rdfSchema = this.getSchema();
//		Map<Object, Object> propToConceptMap = rdfSchema.getPropToConceptMap();
//		
//		String queryString = "SELECT DISTINCT ?concept ?predicate ?range WHERE { "
//				+ "?ins a ?concept. "
//				+ "?ins ?predicate ?obj. "
//				+ "OPTIONAL { ?obj a ?range. } "
//				+ "}";
//		
//		Query query = QueryFactory.create(queryString);
//		QueryExecution qExe = QueryExecutionFactory.sparqlService(this.getDataSourceURL(), query);	
//		ResultSet resultSet = qExe.execSelect();
//		
//		List<String> concepts = new ArrayList<String>();
//		List<Object> conceptList = new ArrayList<Object>();
//		List<Object> propList = new ArrayList<Object>();
//		Map<String, RDFConcept> conceptMap = new HashMap<String, RDFConcept>();
//		while(resultSet.hasNext()) {
//			
//			QuerySolution qSol = resultSet.next();
//			
//			Resource conceptRes = qSol.get("concept").asResource();
//			String conceptURI = conceptRes.getURI();
//			String conceptLocalName = conceptRes.getLocalName();
//			
//			Resource predicateRes = qSol.get("predicate").asResource();
//			String predicateURI = predicateRes.getURI();
//			String predicateLocalName = predicateRes.getLocalName();
//			Predicate predicate = new Predicate();
//			predicate.setUri(predicateURI);
//			predicate.setLocalName(predicateLocalName);
//			propList.add(predicate.getUri());
//			
//			RDFNode rangeNode = qSol.get("range");
//			
//			
//			if(concepts.contains(conceptURI)) {
//				
//				RDFConcept rdfConcept = conceptMap.get(conceptURI);
//				rdfConcept.getPredList().add(predicate);
//				propToConceptMap.put(predicate, rdfConcept);
//				
//			} else {
//				concepts.add(conceptURI);
//				RDFConcept rdfConcept = new RDFConcept();
//				rdfConcept.setUri(conceptURI);
//				rdfConcept.setLocalName(conceptLocalName);
//				rdfConcept.getPredList().add(predicate);
//				
//				conceptList.add(rdfConcept);
//				conceptMap.put(conceptURI, rdfConcept);
//				propToConceptMap.put(predicate, rdfConcept);
//			}
//			
//		}
//		
//		rdfSchema.setConceptList(conceptList);
//		rdfSchema.setPropList(propList);
//		
//	}

	@Override
	public Boolean containPredicate(Node predicateNode) {
		Boolean contains = false;
		
		contains = this.getSchema().contains(predicateNode);
		
		return contains;
	}

}

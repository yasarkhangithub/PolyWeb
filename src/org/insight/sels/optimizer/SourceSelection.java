package org.insight.sels.optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.insight.sels.config.Config;
import org.insight.sels.datasources.DataSource;
import org.sels.insight.schema.SPARQLQuery;

/**
 * 
 * @author Yasar Khan
 *
 */
public class SourceSelection {
	
//	private Map<DataSource, List<Triple>> dataSourceTotripleMap = new HashMap<DataSource, List<Triple>>();
	
	public Map<Triple, List<DataSource>> doSourceSelection(SPARQLQuery sparqlQuery) {
		
		Map<Triple, List<DataSource>> tripleToDataSourceMap = new HashMap<Triple, List<DataSource>>();
		
		Config config = Config.getInstance();
		List<DataSource> allDSList = config.getDataSourceList();
		
		List<Triple> tpList = sparqlQuery.getTpList();
		String rdfType = new String("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		for (Triple triple : tpList) {
			
			List<DataSource> relevantDSList = new ArrayList<DataSource>();
			tripleToDataSourceMap.put(triple, relevantDSList);
			
			Node subjectNode = triple.getSubject();
			Node predicateNode = triple.getPredicate();
			Node ObjectNode = triple.getObject();
			
			String predURI = predicateNode.getURI();
			String predLocalName = predicateNode.getLocalName();
						
			if(predURI.equals(rdfType)) {
				relevantDSList.addAll(allDSList);
			} else {
				
				for (DataSource dataSource : allDSList) {
					
					if(dataSource.containPredicate(predicateNode)) {
						relevantDSList.add(dataSource);
					}
				}
				
			}
		}
		
		
//		Set<Triple> tpSet = tripleToDataSourceMap.keySet();
//		for (Triple triple : tpSet) {
//			List<DataSource> dsList = tripleToDataSourceMap.get(triple);
//			
//			System.out.println(triple.toString() + " == { ");
//			for (DataSource dataSource : dsList) {
//				System.out.println(dataSource.getType());
//			}
//			System.out.println(" }");
//		}
		
		return tripleToDataSourceMap;
		
	}

}

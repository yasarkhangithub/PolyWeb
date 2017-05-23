package org.insight.sels.test;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;


/**
 * 
 * @author Yasar Khan
 *
 */
public class QueryOnModelTest {

	public static void main(String[] args) {
		
		Model model = ModelFactory.createDefaultModel() ;
		model.read("mappers/CNV_R2RML_Mapping.ttl") ;
		
		String queryString = "PREFIX rr: <http://www.w3.org/ns/r2rml#> "
				+ "PREFIX genome: <http://sels.insight.org/cancer-genomics/schema/> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				
				+ "SELECT ?column ?tableName "
				
				+ "WHERE { "
				
				+ "?tmap a rr:TriplesMap . "
				+ "?tmap rr:logicalTable ?table . "
				+ "?table rr:tableName ?tableName . "
				+ "?tmap rr:predicateObjectMap ?poMap . "
				+ "?poMap rr:predicate genome:chr . "
				+ "?poMap rr:objectMap ?oMap . "
				+ "?oMap rr:column ?column . "
				
				+ " } ";
		
//		String q = "PREFIX rr: <http://www.w3.org/ns/r2rml#> SELECT DISTINCT ?tableName ?subTemplate ?type WHERE { ?tmap a rr:TriplesMap . ?tmap rr:logicalTable ?table . ?tmap rr:subjectMap ?subjectMap . ?subjectMap rr:template ?subTemplate . ?subjectMap rr:class ?type . ?table rr:tableName ?tableName . ?tmap rr:predicateObjectMap ?poMap . ?poMap rr:predicate ?predicate . VALUES ?predicate { <http://sels.insight.org/cancer-genomics/schema/start> <http://sels.insight.org/cancer-genomics/schema/chr> <http://sels.insight.org/cancer-genomics/schema/end>  } } ";
		
		Query query = QueryFactory.create(queryString);
		
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
		    ResultSet results = qexec.execSelect() ;

		    ResultSetFormatter.out(System.out, results);
		  }

	}

}

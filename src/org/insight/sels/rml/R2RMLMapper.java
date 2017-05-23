package org.insight.sels.rml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.base.Sys;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.shared.Lock;
import org.insight.sels.query.QueryVar;
import org.insight.sels.querywriter.URITemplate;
import org.insight.sels.util.StringUtil;

/**
 * 
 * @author Yasar Khan
 *
 */
public class R2RMLMapper extends Mapper {
	
	private Model model;
	
	public R2RMLMapper(Model model) {
		this.model = model;
	}
	
	
	public void getSubjectDetails(List<Node> predList, QueryVar var) {
		
		String preds = "";
		
		for (Node pred : predList) {
			preds += "<" + pred.getURI() + "> ";
		}
		
		String queryString = "PREFIX rr: <http://www.w3.org/ns/r2rml#> "
				
				+ "SELECT DISTINCT ?tableName ?subTemplate ?type "
				
				+ "WHERE { "
				
				+ "?tmap a rr:TriplesMap . "
				+ "?tmap rr:logicalTable ?table . "
				+ "?tmap rr:subjectMap ?subjectMap . "
				+ "?subjectMap rr:template ?subTemplate . "
				+ "?subjectMap rr:class ?type . "
				+ "?table rr:tableName ?tableName . "
				+ "?tmap rr:predicateObjectMap ?poMap . "
				+ "?poMap rr:predicate ?predicate . "
				+ "VALUES ?predicate { " + preds + " }"
				+ " } ";
		
		Query query = QueryFactory.create(queryString);
		
		model.enterCriticalSection(Lock.READ);
		try {
			try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			    ResultSet results = qexec.execSelect() ;
			    
			    while(results.hasNext()) {
			    	QuerySolution qSol = results.next();
			    	
			    	String tableName = qSol.get("tableName").toString();
			    	String subTemplate = qSol.get("subTemplate").toString();
			    	String type = qSol.get("type").toString();
			    	
			    	URITemplate uriTemplate = new URITemplate(subTemplate);
			    	var.setUriTemplate(uriTemplate);
			    	var.setIsTemplateVar(Boolean.TRUE);
			    	var.setTableName(tableName);
			    	
			    	List<String> templateVarList = uriTemplate.getTemplateVarList();
			    	List<String> altNameList = new ArrayList<String>();
			    	for (String templateVar : templateVarList) {
//						String altName = tableName + "." + templateVar;
						String altName = templateVar;
						altNameList.add(altName);
					}
			    	
			    	var.setAlternateNameList(altNameList);
			    	
			    }
			  } catch (Exception e) {
				  e.printStackTrace(System.out);
				System.out.println(queryString);
			}
		} finally { model.leaveCriticalSection() ; }
		
		
		
	}
	
	
	public void getSubjectDetails(Node typeNode, QueryVar var) {
		
		String queryString = "PREFIX rr: <http://www.w3.org/ns/r2rml#> "
				
				+ "SELECT DISTINCT ?tableName ?subTemplate "
				
				+ "WHERE { "
				
				+ "?tmap a rr:TriplesMap . "
				+ "?tmap rr:logicalTable ?table . "
				+ "?tmap rr:subjectMap ?subjectMap . "
				+ "?subjectMap rr:template ?subTemplate . "
				+ "?subjectMap rr:class <" + typeNode.getURI() + "> . "
				+ "?table rr:tableName ?tableName . "
				+ "?tmap rr:predicateObjectMap ?poMap . "
				+ "?poMap rr:predicate ?predicate . "
				+ " } ";
		
		Query query = QueryFactory.create(queryString);
		
		model.enterCriticalSection(Lock.READ);
		try {
			try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			    ResultSet results = qexec.execSelect() ;
			    
			    while(results.hasNext()) {
			    	QuerySolution qSol = results.next();
			    	
			    	String tableName = qSol.get("tableName").toString();
			    	String subTemplate = qSol.get("subTemplate").toString();
			    	
			    	URITemplate uriTemplate = new URITemplate(subTemplate);
			    	var.setUriTemplate(uriTemplate);
			    	var.setIsTemplateVar(Boolean.TRUE);
			    	var.setTableName(tableName);
			    	
			    	List<String> varNameList = uriTemplate.getTemplateVarList();
			    	List<String> altNameList = new ArrayList<String>();
			    	for (String varName : varNameList) {
//			    		String altName = tableName + "." + varName;
			    		String altName = varName;
			    		altNameList.add(altName);
					}
			    	
			    	var.setAlternateNameList(altNameList);
			    	
			    	
			    }
			  } catch (Exception e) {
				  e.printStackTrace(System.out);
				System.out.println(queryString);
			}
		} finally { model.leaveCriticalSection() ; }
		
		
		
	}
	
	

	@Override
	public void populatePredMap() {
		
		Map<String, String> columnToTableMap = new HashMap<String, String>();
		
		String queryString = "PREFIX rr: <http://www.w3.org/ns/r2rml#> "
				
				+ "SELECT DISTINCT ?column ?predicate "
				
				+ "WHERE { "
				
				+ "?tmap a rr:TriplesMap . "
				+ "?tmap rr:logicalTable ?table . "
				+ "?table rr:tableName ?tableName . "
				+ "?tmap rr:predicateObjectMap ?poMap . "
				+ "?poMap rr:predicate ?predicate . "
				+ "?poMap rr:objectMap ?oMap . "
				+ "?oMap rr:column ?column . "
				
				+ " } ";
		
		Query query = QueryFactory.create(queryString);
		
		model.enterCriticalSection(Lock.READ);
		try {
			try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			    ResultSet results = qexec.execSelect() ;
			    
			    while(results.hasNext()) {
			    	QuerySolution qSol = results.next();
			    	
			    	String columnName = qSol.get("column").toString();
			    	String predicate = qSol.get("predicate").toString();
			    				    	
			    	predicateToColumnMap.put(predicate, columnName);
			    }
			  } catch (Exception e) {
				  e.printStackTrace(System.out);
				System.out.println(queryString);
			}
		} finally { model.leaveCriticalSection() ; }
		

		
	}

	@Override
	public void populateColumnMap() {
		
		String queryString = "PREFIX rr: <http://www.w3.org/ns/r2rml#> "
				
				+ "SELECT DISTINCT ?column ?tableName "
				
				+ "WHERE { "
				
				+ "?tmap a rr:TriplesMap . "
				+ "?tmap rr:logicalTable ?table . "
				+ "?table rr:tableName ?tableName . "
				+ "?tmap rr:predicateObjectMap ?poMap . "
				+ "?poMap rr:predicate ?predicate . "
				+ "?poMap rr:objectMap ?oMap . "
				+ "?oMap rr:column ?column . "
				
				+ " } ";
		
		Query query = QueryFactory.create(queryString);
		
		model.enterCriticalSection(Lock.READ);
		try {
			try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			    ResultSet results = qexec.execSelect() ;
			    
			    while(results.hasNext()) {
			    	QuerySolution qSol = results.next();
			    	
			    	String columnName = qSol.get("column").toString();
			    	String tableName = qSol.get("tableName").toString();
			    	
//			    	System.out.println(predicate + " === Column: " + columnName);
			    	
			    	columnToSourceMap.put(columnName, tableName);
			    }
			  } catch (Exception e) {
				  e.printStackTrace(System.out);
				System.out.println(queryString);
			}
		} finally { model.leaveCriticalSection() ; }
		
	}


	@Override
	public void populateTemplateMap() {
		
		String queryString = "PREFIX rr: <http://www.w3.org/ns/r2rml#> "
				
				+ "SELECT DISTINCT ?predicate ?template "
				
				+ "WHERE { "
				
				+ "?tmap a rr:TriplesMap . "
				+ "?tmap rr:logicalTable ?table . "
				+ "?table rr:tableName ?tableName . "
				+ "?tmap rr:predicateObjectMap ?poMap . "
				+ "?poMap rr:predicate ?predicate . "
				+ "?poMap rr:objectMap ?oMap . "
				+ "?oMap rr:column ?column . "
				+ "?oMap rr:template ?template . "
				
				+ " } ";
		
		Query query = QueryFactory.create(queryString);
		
		model.enterCriticalSection(Lock.READ);
		try {
			try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			    ResultSet results = qexec.execSelect() ;
			    
			    while(results.hasNext()) {
			    	QuerySolution qSol = results.next();
			    	
			    	RDFNode templateNode = qSol.get("template");
			    	RDFNode predicateNode = qSol.get("predicate");
			    	
			    	if(templateNode != null) {
			    		predicateToTemplateMap.put(predicateNode.toString(), templateNode.toString());
			    	}
			    	
			    }
			  } catch (Exception e) {
				  e.printStackTrace(System.out);
				System.out.println(queryString);
			}
		} finally { model.leaveCriticalSection() ; }
		
	}


	@Override
	public void populateTripleMapSet() {
		
		Map<String, TripleMap> tempMap = new HashMap<String, TripleMap>();
		
		String queryString = "PREFIX rr: <http://www.w3.org/ns/r2rml#> "
				
				+ "SELECT DISTINCT ?tmap ?tableName ?subTemplate ?type ?predicate "
				
				+ "WHERE { "
				
				+ "?tmap a rr:TriplesMap . "
				+ "?tmap rr:logicalTable ?table . "
				+ "?tmap rr:subjectMap ?subjectMap . "
				+ "?subjectMap rr:template ?subTemplate . "
				+ "?subjectMap rr:class ?type . "
				+ "?table rr:tableName ?tableName . "
				+ "?tmap rr:predicateObjectMap ?poMap . "
				+ "?poMap rr:predicate ?predicate . "
				+ " } ";
		
		Query query = QueryFactory.create(queryString);
		
		model.enterCriticalSection(Lock.READ);
		try {
			try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			    ResultSet results = qexec.execSelect() ;
			    
			    while(results.hasNext()) {
			    	QuerySolution qSol = results.next();
			    	
			    	String tm = qSol.get("tmap").toString();
			    	String tableName = qSol.get("tableName").toString();
			    	String subTemplate = qSol.get("subTemplate").toString();
			    	String type = qSol.get("type").toString();
			    	String predicate = qSol.get("predicate").toString();
			    				    	
			    	TripleMap tMap = tempMap.get(tm);
			    	if(tMap != null) {
			    		tMap.getPredicateSet().add(predicate);
			    	} else {
			    		tMap = new TripleMap();
				    	tMap.setSource(tableName);
				    	tMap.setSubjectTemplate(subTemplate);
				    	tMap.setSubjectType(type);
				    	tMap.getPredicateSet().add(predicate);
				    	tripleMapSet.add(tMap);
				    	tempMap.put(tm, tMap);
			    	}
			    	
			    	
			    }
			  } catch (Exception e) {
				  e.printStackTrace(System.out);
				System.out.println(queryString);
			}
		} finally { model.leaveCriticalSection() ; }
		
	}

	
	
	
	
}

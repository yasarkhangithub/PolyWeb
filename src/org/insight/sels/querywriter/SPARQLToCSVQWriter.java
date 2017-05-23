package org.insight.sels.querywriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.insight.sels.config.Config;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.QueryVar;
import org.insight.sels.query.SubQuery;
import org.insight.sels.query.TPGroup;
import org.insight.sels.rml.RMLMapper;
import org.insight.sels.util.StringUtil;

/**
 * 
 * @author Yasar Khan
 *
 */
public class SPARQLToCSVQWriter {
	
	
	public String rewriteQuery(TPGroup subQuery, DataSource datasource) {
		
		Config config = Config.getInstance();
		List<String> mainQueryProjList = config.getSparqlQuery().getProjectionList();

		List<Triple> tripleList = subQuery.getTpList();
		Map<String, QueryVar> varMap = new HashMap<String, QueryVar>();
		subQuery.setVarMap(varMap);

		RMLMapper mapperRML = (RMLMapper) datasource.getMapper();

		List<String> dbProjectionList = new ArrayList<String>();
		String whereClause = "";
		
		int uriSubVarCount = 0;
		String uriSubVarName = "v_";

		for (Triple triple : tripleList) {

			Node subject = triple.getSubject();
			Node predicate = triple.getPredicate();
			Node object = triple.getObject();

			if (predicate.isURI() && !(predicate.getURI().equals(new String("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")))) {

				String columnName = mapperRML.getColumn(predicate.toString());
				
				String objectAltName = columnName;

				dbProjectionList.add(objectAltName);


				if (subject.isVariable()) {
					String varName = subject.getName();

					QueryVar queryVar = varMap.get(varName);
					if (queryVar == null) {
						queryVar = new QueryVar();
						queryVar.setVarName(varName);
						queryVar.getIsDomainOf().add(predicate);
						queryVar.getPredicateSet().add(predicate.toString());
						queryVar.setIsDomain(Boolean.TRUE);

						if (mainQueryProjList.contains(varName)) {
							queryVar.setIsResultVar(Boolean.TRUE);
						}

						varMap.put(varName, queryVar);
					} else {
						queryVar.getIsDomainOf().add(predicate);
						queryVar.getPredicateSet().add(predicate.toString());
						queryVar.setIsDomain(Boolean.TRUE);
					}
				} else { // If subject is not a variable, i.e. a resource URI
					
					String varName = uriSubVarName+uriSubVarCount;
					QueryVar queryVar = varMap.get(varName);
					
					if (queryVar == null) {
						queryVar = new QueryVar();
						queryVar.setVarName(varName);
						queryVar.getIsDomainOf().add(predicate);
						queryVar.getPredicateSet().add(predicate.toString());
						queryVar.setIsDomain(Boolean.TRUE);
						queryVar.setValuedNode(subject);
						queryVar.setIsValued(Boolean.TRUE);

						varMap.put(varName, queryVar);
						
					} else {
						if(queryVar.getValuedNode().equals(subject)) {
							queryVar.getIsDomainOf().add(predicate);
							queryVar.getPredicateSet().add(predicate.toString());
							queryVar.setIsDomain(Boolean.TRUE);
						} else {
							uriSubVarCount++;
							
							varName = uriSubVarName+uriSubVarCount;
							queryVar = new QueryVar();
							queryVar.setVarName(varName);
							queryVar.getIsDomainOf().add(predicate);
							queryVar.getPredicateSet().add(predicate.toString());
							queryVar.setIsDomain(Boolean.TRUE);
							queryVar.setValuedNode(subject);
							queryVar.setIsValued(Boolean.TRUE);

							varMap.put(varName, queryVar);
						}
						
					}
					
//					if(!whereClause.isEmpty())
//						whereClause += " AND ";
//					whereClause += objectAltName + " = '" + StringUtil.getLocalName(subject) + "' ";
				}

				if (object.isVariable()) {
					String varName = object.getName();

					QueryVar queryVar = varMap.get(varName);
					if (queryVar == null) {
						queryVar = new QueryVar();
						queryVar.setVarName(varName);
						List<String> altNameList = new ArrayList<String>();
						altNameList.add(objectAltName);
						queryVar.setAlternateNameList(altNameList);
						queryVar.setIsRangeOf(predicate);
						queryVar.setIsRange(Boolean.TRUE);

						if (mainQueryProjList.contains(varName)) {
							queryVar.setIsResultVar(Boolean.TRUE);
						}
						
						String template = mapperRML.getTemplate(predicate.toString());
						if(template != null) {
							URITemplate uriTemplate = new URITemplate(template);
							queryVar.setUriTemplate(uriTemplate);
							queryVar.setIsTemplateVar(Boolean.TRUE);
						}

						varMap.put(varName, queryVar);
					}

				} else if(object.isLiteral()) {
					if(!whereClause.isEmpty())
						whereClause += " AND ";
					whereClause += objectAltName + " = '" + object.getLiteralValue().toString() + "' ";
				} else if(object.isURI()) {
					if(!whereClause.isEmpty())
						whereClause += " AND ";
					whereClause += objectAltName + " = '" + StringUtil.getLocalName(object) + "' ";
				}

			} else if (predicate.isURI() && predicate.getURI().equals(new String("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))) {
				
				if(subject.isVariable() && object.isURI()) {
					String varName = subject.getName();

					QueryVar queryVar = varMap.get(varName);
					if (queryVar == null) {
						queryVar = new QueryVar();
						queryVar.setVarName(varName);
						queryVar.setIsDomain(Boolean.TRUE);

						if (mainQueryProjList.contains(varName)) {
							queryVar.setIsResultVar(Boolean.TRUE);
						}

						varMap.put(varName, queryVar);
					} else {
						queryVar.setIsDomain(Boolean.TRUE);
					}
					
					mapperRML.getCSVSubjectDetails(object, queryVar);
					
					List<String> altNameList = queryVar.getAlternateNameList();
					for (String altName : altNameList) {
						dbProjectionList.add(altName);
	
					}
					
				} else { // If subject is a resource URI
					
				}
			}

		}

		Set<String> varKeySet = varMap.keySet();
		for (String varName : varKeySet) {
//			System.out.println("Var Name:::: " + varName);
			QueryVar var = varMap.get(varName);
			List<String> altNameList = var.getAlternateNameList();
//			System.out.println("Alt Name List Size::::: " + altNameList.size());
//			for (String string : altNameList) {
//				System.out.println("Altname ==== " + string);
//			}
			if (altNameList.isEmpty()) {
//				mapperRML.getCSVSubjectDetails(var.getIsDomainOf(), var);
				String subTemplate = mapperRML.getSubjectTemplate(var.getPredicateSet());
				URITemplate uriTemplate = new URITemplate(subTemplate);
		    	var.setUriTemplate(uriTemplate);
		    	var.setIsTemplateVar(Boolean.TRUE);
		    	
		    	List<String> varNameList = uriTemplate.getTemplateVarList();
		    	for (String varN : varNameList) {
		    		String altName = varN;
		    		altNameList.add(altName);
				}
			}
			
			for (String altName : altNameList) {
//				System.out.println("Alt Name:::: " + altName);
				if(!dbProjectionList.contains(altName))
					dbProjectionList.add(altName);
			}
			
			if(var.getIsValued()) {
				if(!whereClause.isEmpty())
					whereClause += " AND ";
				Map<String, String> varValueMap = var.getUriTemplate().getValuesInTemplate(var.getValuedNode().toString());
				Set<String> varSet = varValueMap.keySet();
				int count=1;
				int setSize = varSet.size();
				for (String v : varSet) {
					whereClause += v + " = '" + varValueMap.get(v) + "' ";
					if(count != setSize) 
						whereClause += " AND ";
					count++;
				}
			}

		}
		
		String query = "SELECT DISTINCT ";
		
		int count = 1;
		for (String projVar : dbProjectionList) {
			
			query += projVar;
			
			if(count < dbProjectionList.size()) {
				query += ", ";
			}
			
			count++;
		}
		
		ExprList filterExprs = subQuery.getFilterExprList();
		if(!filterExprs.isEmpty()) {
			
			for (Expr expr : filterExprs) {
				Set<Var> exprVars = expr.getVarsMentioned();
				String exprStr = expr.toString();
				for (Var exprVar : exprVars) {
					QueryVar qvar = varMap.get(exprVar.getName());
					if(qvar != null) {
						String altVar = qvar.getAlternateNameList().get(0);
						exprStr = exprStr.replace(exprVar.toString(), altVar);
//						exprStr = exprStr.replace("?start", "Start_Position");
					}
				}
				
				exprStr = exprStr.replace("||", "OR");
				exprStr = exprStr.replace("&&", "AND");
				exprStr = exprStr.replace("\"", "'");
				
				if(whereClause.isEmpty())
					whereClause += " " + exprStr;
				else
					whereClause += " AND " + exprStr;
			}
			
//			System.out.println("CSV Where ============= " + whereClause);
		}
		
		if(whereClause.isEmpty())
			query += " FROM dfs.`" + datasource.getDirPath() + "`";
		else
			query += " FROM dfs.`" + datasource.getDirPath() + "` WHERE " + whereClause + " ";
		
		
		
		return query;
		
	}
	

}

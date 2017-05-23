package org.insight.sels.querywriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.jena.sparql.algebra.op.OpAssign;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpConditional;
import org.apache.jena.sparql.algebra.op.OpDatasetNames;
import org.apache.jena.sparql.algebra.op.OpDiff;
import org.apache.jena.sparql.algebra.op.OpDisjunction;
import org.apache.jena.sparql.algebra.op.OpDistinct;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpGroup;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpLabel;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpList;
import org.apache.jena.sparql.algebra.op.OpMinus;
import org.apache.jena.sparql.algebra.op.OpNull;
import org.apache.jena.sparql.algebra.op.OpOrder;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProcedure;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpPropFunc;
import org.apache.jena.sparql.algebra.op.OpQuad;
import org.apache.jena.sparql.algebra.op.OpQuadBlock;
import org.apache.jena.sparql.algebra.op.OpQuadPattern;
import org.apache.jena.sparql.algebra.op.OpReduced;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.algebra.op.OpSlice;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.op.OpTopN;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprFunction;
import org.apache.jena.sparql.expr.ExprFunction0;
import org.apache.jena.sparql.expr.ExprFunction1;
import org.apache.jena.sparql.expr.ExprFunction2;
import org.apache.jena.sparql.expr.ExprFunction3;
import org.apache.jena.sparql.expr.ExprFunctionN;
import org.apache.jena.sparql.expr.ExprFunctionOp;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.ExprVisitor;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.Template;
import org.insight.sels.config.Config;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.QueryVar;
import org.insight.sels.query.SQLQuery;
import org.insight.sels.query.SubQuery;
import org.insight.sels.query.TPGroup;
import org.insight.sels.rml.R2RMLMapper;
import org.insight.sels.util.StringUtil;

/**
 * 
 * @author Yasar Khan
 *
 */
public class SPARQLToSQLWriter {
	
	TPGroup subQuery = new TPGroup();
	
	public SQLQuery rewriteQuery(TPGroup sq, DataSource datasource) {

//		System.out.println(sq.getTpList().toString());
		subQuery = sq;
		Config config = Config.getInstance();
		List<String> mainQueryProjList = config.getSparqlQuery().getProjectionList();

		List<Triple> tripleList = subQuery.getTpList();
		Map<String, QueryVar> varMap = subQuery.getVarMap();

		R2RMLMapper mapperR2RML = (R2RMLMapper) datasource.getMapper();

		List<String> dbProjectionList = new ArrayList<String>();
		List<String> tableList = new ArrayList<String>();
		
		SQLQuery sqlQuery = new SQLQuery();
		String whereClause = "";
		
		int uriSubVarCount = 0;
		String uriSubVarName = "v_";

		for (Triple triple : tripleList) {

			Node subject = triple.getSubject();
			Node predicate = triple.getPredicate();
			Node object = triple.getObject();
			
			if (predicate.isURI() && !(predicate.getURI().equals(new String("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")))) {
				
//				Map<String, String> columnToTableMap = queryRML.getColumnName(predicate.toString());
//
//				String columnName = columnToTableMap.keySet().iterator().next();
//				String tableName = columnToTableMap.get(columnName);
				
				String columnName = mapperR2RML.getColumn(predicate.toString());
				String tableName = mapperR2RML.getSource(columnName);

//				String objectAltName = tableName + "." + columnName;
				String objectAltName = columnName;

				dbProjectionList.add(objectAltName);

				if (!tableList.contains(tableName)) {
					tableList.add(tableName);
				}

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
						
//						if(!whereClause.isEmpty())
//							whereClause += " AND ";
//						whereClause += tableName+"."+varName + " = " + StringUtil.getLocalName(subject) + " ";
						
//						uriSubVarCount++;
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
					
					
				}

				if (object.isVariable()) {
					String varName = object.getName();

					QueryVar queryVar = varMap.get(varName);
					if (queryVar == null) {
						queryVar = new QueryVar();
						queryVar.setVarName(varName);
						List<String> altNameList = new ArrayList<String>();
						altNameList.add(objectAltName);
						queryVar.setTableName(tableName);
						queryVar.setAlternateNameList(altNameList);
						queryVar.setIsRangeOf(predicate);
						queryVar.setIsRange(Boolean.TRUE);

						if (mainQueryProjList.contains(varName)) {
							queryVar.setIsResultVar(Boolean.TRUE);
						}
						
						String template = mapperR2RML.getTemplate(predicate.toString());
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
					whereClause += objectAltName + " = " + object.getLiteralValue().toString() + " ";
				} else if(object.isURI()) {
					if(!whereClause.isEmpty())
						whereClause += " AND ";
					whereClause += objectAltName + " = " + StringUtil.getLocalName(object) + " ";
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
					mapperR2RML.getSubjectDetails(object, queryVar);
					
					List<String> altNameList = queryVar.getAlternateNameList();
					for (String altName : altNameList) {
						dbProjectionList.add(altName);
						
						String tableName = altName.split("\\.")[0];
						if (!tableList.contains(tableName)) {
							tableList.add(tableName);
						}
					}
					
					
				} else if(subject.isURI() && object.isURI()) {
					
				}
			}

		}

		Set<String> varKeySet = varMap.keySet();
		for (String varName : varKeySet) {
			QueryVar var = varMap.get(varName);
			List<String> altNameList = var.getAlternateNameList();
			if (altNameList.isEmpty()) {
				// System.out.println("Var Name: " + varName);
//				mapperR2RML.getSubjectDetails(var.getIsDomainOf(), var);
				
				String subTemplate = mapperR2RML.getSubjectTemplate(var.getPredicateSet());
//				System.out.println("*&*&*&*&*&*&*&*&*&*&  " + var.getPredicateSet().toString());
				URITemplate uriTemplate = new URITemplate(subTemplate);
		    	var.setUriTemplate(uriTemplate);
		    	var.setIsTemplateVar(Boolean.TRUE);
		    	
		    	List<String> templateVarList = uriTemplate.getTemplateVarList();
		    	for (String templateVar : templateVarList) {
//					String altName = tableName + "." + templateVar;
					String altName = templateVar;
					altNameList.add(altName);
				}
		
		    	

		    	
				for (String altName : altNameList) {
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
//						whereClause += var.getTableName() + "." + v + " = " + varValueMap.get(v) + " ";
						whereClause += v + " = " + varValueMap.get(v) + " ";
						if(count != setSize) 
							whereClause += " AND ";
						count++;
					}
				}
			}

		}
		
		ExprList filterExprs = subQuery.getFilterExprList();
		if(!filterExprs.isEmpty()) {
			
			for (Expr expr : filterExprs) {
				Set<Var> exprVars = expr.getVarsMentioned();
				String exprStr = expr.toString();
//				System.out.println(exprStr);
				for (Var exprVar : exprVars) {
					QueryVar qvar = varMap.get(exprVar.getName());
					if(qvar != null) {
						String altVar = qvar.getAlternateNameList().get(0);
						exprStr = exprStr.replace(exprVar.toString(), altVar);
					} else {
						exprStr = exprStr.replace("&& ( ?cnf > 30 )", "");
//						System.out.println("After: " + exprStr);
					}
						
					
				}
				
				if(whereClause.isEmpty())
					whereClause += " " + exprStr;
				else
					whereClause += " AND " + exprStr;
			}
			
//			System.out.println("Where ::::: " + whereClause);
		}

		
		
		
		sqlQuery.setProjectionList(dbProjectionList);
		sqlQuery.setTableList(tableList);
		sqlQuery.setWhereClause(whereClause);
		
		return sqlQuery;
	}
	
	
//	public void processExpr(ExprFunction exprFunction, Set<String> varKeySet, Expr mainExpr) {
//		
//		Expr arg1 = exprFunction.getArg(1);
//		Expr arg2 = exprFunction.getArg(2);
//		String operator = exprFunction.getOpName();
//		
//		if(arg1.isFunction()) {
//			if(!Collections.disjoint(varKeySet, arg1.getFunction().getVarNamesMentioned()))
//				processExpr(arg1.getFunction(), varKeySet, mainExpr);
//			if(!Collections.disjoint(varKeySet, arg2.getFunction().getVarNamesMentioned()))
//				processExpr(arg2.getFunction(), varKeySet, mainExpr);
//		} else {
//			if(!Collections.disjoint(varKeySet, exprFunction.getVarNamesMentioned())) {
//				if(mainExpr == null)
//					mainExpr = exprFunction.getExpr();
//			}
//		}
//	}

}

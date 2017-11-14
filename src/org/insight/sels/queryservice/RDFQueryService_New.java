package org.insight.sels.queryservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.insight.sels.config.Config;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.SubQuery;
import org.insight.sels.query.EExclusiveGroup;
import org.insight.sels.result.PolyQuerySolution;
import org.insight.sels.result.PolyResultSet;
import org.insight.sels.result.RDBRSWrapper;
import org.insight.sels.result.RDFRSWrapper;
import org.insight.sels.result.ResultSetWrapper;

/**
 * 
 * @author Yasar Khan
 *
 */
public class RDFQueryService_New implements QueryService_New {

	private DataSource rdbDataSource;
	private EExclusiveGroup subQuery;
	private List<String> mainQueryProjList = new ArrayList<String>();
	
	@Override
	public ResultSetWrapper executeQuery(EExclusiveGroup subQuery, DataSource datasource) {
		
		this.rdbDataSource = datasource;
		this.subQuery = subQuery;
		
		Config config = Config.getInstance();
		this.mainQueryProjList = config.getSparqlQuery().getProjectionList();
		
		String queryString = rewriteQuery();
		
//		System.out.println("SPARQL Query:" );
//		System.out.println("=========================================" );
//		System.out.println(queryString);
//		System.out.println("=========================================" );
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qExe = QueryExecutionFactory.sparqlService(rdbDataSource.getDataSourceURL(), query);	
		ResultSet resultSet = qExe.execSelect();
		
		ResultSetWrapper resultSetWrap = new RDFRSWrapper();
		resultSetWrap.setResultSet(resultSet);
		resultSetWrap.setSubQuery(subQuery);
		
		return resultSetWrap;
	}

	@Override
	public String rewriteQuery() {
		
		List<Triple> tripleList = subQuery.getTpList();
		
		ElementTriplesBlock block = new ElementTriplesBlock();
		
		for (Triple triple : tripleList) {
			block.addTriple(triple); 
		}
		
		ElementGroup body = new ElementGroup();
		body.addElement(block);
		ExprList exprs = subQuery.getFilterExprList();
		for (Expr expr : exprs) {
			body.addElement(new ElementFilter(expr));
		}
		Query query = QueryFactory.make();
		query.setQueryPattern(body);
		query.setQuerySelectType();
		query.setDistinct(true);
//		query.setQueryResultStar(true);
//		query.setLimit(5);
		query.addProjectVars(mainQueryProjList);
//		query.addResultVar("s"); 
		
//		if(!exprs.isEmpty()) {
//			System.out.println("RDF Filter Query: " + query.toString());
//		}
		
		return query.toString();
	}
	
	
}

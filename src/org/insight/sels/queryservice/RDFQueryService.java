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
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.insight.sels.config.Config;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.SubQuery;
import org.insight.sels.query.TPGroup;
import org.insight.sels.result.PolyQuerySolution;
import org.insight.sels.result.PolyResultSet;

/**
 * 
 * @author Yasar Khan
 *
 */
public class RDFQueryService implements QueryService {

	private DataSource rdbDataSource;
	private TPGroup subQuery;
	private List<String> mainQueryProjList = new ArrayList<String>();
	
	@Override
	public PolyResultSet executeQuery(TPGroup subQuery, DataSource datasource) {
		
		this.rdbDataSource = datasource;
		this.subQuery = subQuery;
		
		Config config = Config.getInstance();
		this.mainQueryProjList = config.getSparqlQuery().getProjectionList();
		
		String queryString = rewriteQuery();
		List<PolyQuerySolution> qSolList = new ArrayList<PolyQuerySolution>();
		PolyResultSet polyRset = new PolyResultSet();
		
//		System.out.println("SPARQL Query:" );
//		System.out.println("=========================================" );
//		System.out.println(queryString);
//		System.out.println("=========================================" );
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qExe = QueryExecutionFactory.sparqlService(rdbDataSource.getDataSourceURL(), query);	
		ResultSet resultSet = qExe.execSelect();
		
		while(resultSet.hasNext()) {
			QuerySolution qSol = resultSet.next();
//			System.out.println(qSol.toString());
			
			PolyQuerySolution qs = new PolyQuerySolution();
			Iterator<String> varIter = qSol.varNames();
			List<String> varList = new ArrayList<String>();
			Map<String, String> varToValueMap = new HashMap<String, String>();
			while(varIter.hasNext()) {
				String varName = varIter.next();
				varList.add(varName);
				RDFNode varValueNode = qSol.get(varName);
				String varValue = "";
				if(varValueNode.isLiteral())
					varValue = varValueNode.asLiteral().getValue().toString();
				else 
					varValue = varValueNode.toString();
				varToValueMap.put(varName, varValue);
			}
			
			qs.setVarList(varList);
			qs.setVarToValueMap(varToValueMap);
			
			qSolList.add(qs);
		}
		
		polyRset.setQuerySolList(qSolList);
		
		return polyRset;
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
		Query query = QueryFactory.make();
		query.setQueryPattern(body);
		query.setQuerySelectType();
//		query.setQueryResultStar(true);
//		query.setLimit(5);
		query.addProjectVars(mainQueryProjList);
//		query.addResultVar("s"); 
		
		return query.toString();
	}

	@Override
	public PolyResultSet executeQuery(SubQuery subQuery) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

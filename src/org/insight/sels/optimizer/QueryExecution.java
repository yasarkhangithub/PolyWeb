package org.insight.sels.optimizer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.insight.sels.config.Config;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.SubQuery;
import org.insight.sels.query.TPGroup;
import org.insight.sels.queryservice.QueryService;
import org.insight.sels.queryservice.QueryServiceFactory;
import org.insight.sels.result.PolyQuerySolution;
import org.insight.sels.result.PolyResultSet;
import org.insight.sels.stats.QueryExecutionStats;
import org.sels.insight.schema.SPARQLQuery;

/**
 * 
 * @author Yasar Khan
 *
 */
public class QueryExecution {
	
	private PolyResultSet polyResultSet = new PolyResultSet();
	
	public PolyResultSet executeQuery(SPARQLQuery sparqlQuery) throws SQLException {
		
		QueryExecutionStats qeStats = Config.getInstance().getQueryExecStats();
		
		/**
		 * Relevant sources are identified by the source selection algorithm.
		 */
		SourceSelection sourceSelection = new SourceSelection();
		long ssStartTime = System.currentTimeMillis();
		Map<Triple, List<DataSource>> tripleToDataSourceMap = sourceSelection.doSourceSelection(sparqlQuery);
		long ssEndTime = System.currentTimeMillis();
//		long ssTime = ssEndTime - ssStartTime;
		qeStats.calculateSourceSelectionTime(ssStartTime, ssEndTime);
		
		
		
		long qpStartTime = System.currentTimeMillis();
		QueryPlanner queryPlanner = new QueryPlanner();
		queryPlanner.createPlan(tripleToDataSourceMap);
		long qpEndTime = System.currentTimeMillis();
//		long qpTime = qpEndTime - qpStartTime;
		qeStats.calculateQueryPlanningTime(qpStartTime, qpEndTime);
		
		
		long qeStartTime = System.currentTimeMillis();
		ParrallelQPlanExecution qpEval = new ParrallelQPlanExecution();
		polyResultSet = qpEval.evaluatePlan(queryPlanner);
		long qeEndTime = System.currentTimeMillis();
//		long qeTime = qeEndTime - qeStartTime;
		qeStats.calculateQueryExecutionTime(qeStartTime, qeEndTime);
		
		
//		QueryPlanExecution_New qpEval = new QueryPlanExecution_New();
//		polyResultSet = qpEval.evaluatePlan(queryPlanner);
		
		
		
		
//		System.out.println("Results Size: " + polyResultSet.getQuerySolList().size());
		
//		QueryPlanExecution qpEval = new QueryPlanExecution();
//		PolyResultSet polyResultSet = qpEval.evaluatePlan(queryPlanner);
		
//		Map<DataSource, List<Triple>> dataSourceToTripleMap = getDataSourceToTripleMap(tripleToDataSourceMap);
//		List<SubQuery> subQueryList = new ArrayList<SubQuery>();
		
//		QueryServiceFactory queryServiceFactory = new QueryServiceFactory();
		
//		Set<DataSource> dataSourceSet = dataSourceToTripleMap.keySet();
//		List<PolyQuerySolution> allQSolList = new ArrayList<PolyQuerySolution>();
//		Integer subQueryId = 1;
//		for (DataSource dataSource : dataSourceSet) {
//			List<Triple> tripleList = dataSourceToTripleMap.get(dataSource);
//			
//			SubQuery subQuery = new SubQuery(subQueryId);
//			subQuery.setTpList(tripleList);
//			subQuery.setDatasource(dataSource);
//			subQuery.setProjectionList(getSubQueryProjection(sparqlQuery.getProjectionList(), tripleList));
//			subQuery.calculateCost();
//			subQueryList.add(subQuery);
//			subQueryId++;
			
//			QueryService queryService = queryServiceFactory.getQueryService(dataSource.getType());
//			PolyResultSet polyRset = queryService.executeQuery(subQuery);
//			
//			allQSolList.addAll(polyRset.getQuerySolList());
//		}
		
//		PolyResultSet polyResultSet = new PolyResultSet(allQSolList);
//		polyResultSet.setVarNames(allQSolList.get(0).getVarList());
		
//		Collections.sort(subQueryList);
//		for (SubQuery sq : subQueryList) {
//			System.out.println("SQ Type: " + sq.getDatasource().getType() + " ---- Cost: " + sq.getCost());
//		}
		
		
		
//		QueryPlan queryPlan = new QueryPlan();
//		queryPlan.createPlan(subQueryList);
//		
//		QueryPlanEvaluation qpEval = new QueryPlanEvaluation();
//		PolyResultSet polyResultSet = qpEval.evaluatePlan(queryPlan);
		
		return polyResultSet;
	}
	
	
	
	/**
	 * 
	 * @param tripleToDataSourceMap
	 * @return
	 */
	public Map<DataSource, List<Triple>> getDataSourceToTripleMap(Map<Triple, List<DataSource>> tripleToDataSourceMap) {
		
		Map<DataSource, List<Triple>> dataSourceToTripleMap = new HashMap<DataSource, List<Triple>>();
		
		Set<Triple> tripleSet = tripleToDataSourceMap.keySet();
		for (Triple triple : tripleSet) {
			List<DataSource> dataSourceList = tripleToDataSourceMap.get(triple);
			for (DataSource dataSource : dataSourceList) {
				if(dataSourceToTripleMap.containsKey(dataSource)) {
					dataSourceToTripleMap.get(dataSource).add(triple);
				} else {
					List<Triple> tripleList = new ArrayList<Triple>();
					tripleList.add(triple);
					dataSourceToTripleMap.put(dataSource, tripleList);
				}
			}
		}
		
		return dataSourceToTripleMap;
	}
	
	
	
	/**
	 * Returns list of projection variables for a sub query
	 * 
	 * @param mainQueryProjList
	 * @param subQueryTripleList
	 * @return
	 */
	private List<String> getSubQueryProjection(List<String> mainQueryProjList, List<Triple> subQueryTripleList) {
		List<String> subQueryProjList = new ArrayList<String>();
		
		for (Triple triple : subQueryTripleList) {
			Node subject = triple.getSubject();
			Node object = triple.getObject();
			
			if(subject.isVariable()) {
				String subjectVar = subject.getName();
				if(mainQueryProjList.contains(subjectVar)) {
					subQueryProjList.add(subjectVar);
				}
			}
			
			if(object.isVariable()) {
				String objectVar = object.getName();
				if(mainQueryProjList.contains(objectVar)) {
					subQueryProjList.add(objectVar);
				}
			}
			
		}
		
		return subQueryProjList;
	}
	
}

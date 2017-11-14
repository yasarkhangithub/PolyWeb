package org.insight.sels.optimizer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.EExclusiveGroup;
import org.insight.sels.queryservice.QueryServiceFactory_New;
import org.insight.sels.queryservice.QueryService_New;
import org.insight.sels.result.PolyQuerySolution;
import org.insight.sels.result.PolyResultSet;
import org.insight.sels.result.ResultSetWrapper;

/**
 * 
 * @author Yasar Khan
 *
 * @param <T>
 */
public class QueryPlanExecution_New<T> {

	private QueryServiceFactory_New queryServiceFactory = new QueryServiceFactory_New();
	private Map<Integer, FederatedJoin<T>> fedJoinMap;
	private PolyResultSet mainQueryPolyRS = new PolyResultSet();
	private List<PolyQuerySolution> mainQueryPolyQsList = new ArrayList<PolyQuerySolution>();
	
	public PolyResultSet evaluatePlan(QueryPlanner<T> queryPlan) throws SQLException {
		
		mainQueryPolyRS.setQuerySolList(mainQueryPolyQsList);
		
		fedJoinMap = queryPlan.getFedJoinMap();
		Integer leafJoinId = queryPlan.getLeafJoinID();
		Boolean joinHasResults = Boolean.FALSE;
		
		/**
		 * Get the leaf join to start evaluating the query plan.
		 * Leaf join means where both the arguments are Triple Groups.
		 */
		FederatedJoin<T> join = fedJoinMap.get(leafJoinId);
		Integer joinId = join.getId();
		JoinArg<T> leftArg = join.getJoinArgLeft();
		JoinArg<T> rightArg = join.getJoinArgRight();
		List<String> joinVars = join.getJoinVarList();
		Integer nextJoinId = join.getNextJoinId();
		
		
		/**
		 * Create a poly result set containing the joined result set 
		 * obtained from left and right sub queries.
		 */
		PolyResultSet polyResLR = new PolyResultSet();
		List<PolyQuerySolution> polyQSListLR = new ArrayList<PolyQuerySolution>();
		List<String> varListLR = new ArrayList<String>();
		polyResLR.setVarNames(varListLR);
		polyResLR.setQuerySolList(polyQSListLR);
		
		
		/**
		 * Get sub queries from both edges of the leaf join.
		 */
		EExclusiveGroup leftSubQuery = (EExclusiveGroup) leftArg.getArg();
		EExclusiveGroup rightSubQuery = (EExclusiveGroup) rightArg.getArg();
		List<String> projListL = leftSubQuery.getProjectionList();
		List<String> projListR = rightSubQuery.getProjectionList();
		
		/**
		 * Create a combined projection list for left and right sub queries
		 */
		varListLR.addAll(projListL);
		for (String rightSQVar : projListR) {
			if(!varListLR.contains(rightSQVar)) {
				varListLR.add(rightSQVar);
			}
		}
		
//		System.out.println("Proj List LR: ****************** " + varListLR);
		
		/**
		 * Execute the left sub query.
		 */
		List<DataSource> leftSqDsList = leftSubQuery.getDatasourceList();
		for (DataSource dsL : leftSqDsList) {
			
			ResultSetWrapper resultSetWrapL = executeSubQuery(leftSubQuery, dsL);
			
			PolyQuerySolution polyQsL = null;
			while( (polyQsL = resultSetWrapL.getNextResult()) != null) {
				
				/**
				 * for each solution of the left sub query (leftSubQuery) edge of the join, create 
				 * a right sub query (valuesSubQuery) having fixed values for the join variables
				 */
				EExclusiveGroup valuesSubQuery = getValuesSubQuery(joinVars, polyQsL, rightSubQuery);
				
				/**
				 * Execute each of the valuesSubQuery
				 */
				List<DataSource> valuesSqDsList = valuesSubQuery.getDatasourceList();
				for (DataSource dsR : valuesSqDsList) {
					
					ResultSetWrapper resultSetWrapR = executeSubQuery(valuesSubQuery, dsR);
					
					PolyQuerySolution polyQsR = null;
					while( (polyQsR = resultSetWrapR.getNextResult()) != null) {
						
						PolyQuerySolution polyQsLR = new PolyQuerySolution();
						Map<String, String> varToValueMapL = new HashMap<String, String>();
						Map<String, String> varToValueMapR = new HashMap<String, String>();
						
						for (String varLR : varListLR) {
							
							if(joinVars.contains(varLR)) {
								varToValueMapL.put(varLR, polyQsL.getValue(varLR));
								varToValueMapR.put(varLR, polyQsL.getValue(varLR));
							} else if(projListL.contains(varLR) && projListR.contains(varLR)) {
								varToValueMapL.put(varLR, polyQsL.getValue(varLR));
								varToValueMapR.put(varLR, polyQsR.getValue(varLR));
							} else {
								if(projListL.contains(varLR)) {
									varToValueMapL.put(varLR, polyQsL.getValue(varLR));
									varToValueMapR.put(varLR, polyQsL.getValue(varLR));
								} else if(projListR.contains(varLR)) {
									varToValueMapL.put(varLR, polyQsR.getValue(varLR));
									varToValueMapR.put(varLR, polyQsR.getValue(varLR));
								}
							} 
						}
						
						List<PolyQuerySolution> tmpPolyQsListLR = new ArrayList<PolyQuerySolution>();
						
						polyQsLR.setVarList(varListLR);
						polyQsLR.setVarToValueMap(varToValueMapL);
						tmpPolyQsListLR.add(polyQsLR);
						
						if(!varToValueMapL.equals(varToValueMapR)) {
							PolyQuerySolution newJoinPolyQS = new PolyQuerySolution();
							newJoinPolyQS.setVarList(varListLR);
							newJoinPolyQS.setVarToValueMap(varToValueMapR);
							tmpPolyQsListLR.add(newJoinPolyQS);
						}
						
						List<PolyQuerySolution> polyqsList = getJoinedResults(nextJoinId, tmpPolyQsListLR);
						mainQueryPolyQsList.addAll(polyqsList);
						
					}
				}
				
			}
			
		}
		
		
		return mainQueryPolyRS;
	}
	
	
	
	
	public List<PolyQuerySolution> getJoinedResults(Integer nextJoinId, List<PolyQuerySolution> polyQsList) {
		
		for (PolyQuerySolution polyQs : polyQsList) {
			
			if(nextJoinId != null) {
				
				FederatedJoin<T> nextJoin = fedJoinMap.get(nextJoinId);
				Integer nextId = nextJoin.getId();
				JoinArg<T> nextLeftArg = nextJoin.getJoinArgLeft();
				JoinArg<T> nextRightArg = nextJoin.getJoinArgRight();
				List<String> nextJoinVars = nextJoin.getJoinVarList();
				nextJoinId = nextJoin.getNextJoinId();
				
				/**
				 * Create a poly result set containing the joined result set 
				 * obtained from left and right arguments.
				 */
				List<String> nextJoinVarList = new ArrayList<String>();
				
				EExclusiveGroup subQuery = null;
				
				if(nextLeftArg.getArgType().equals(JoinArg.QUERY_NODE))
					subQuery = (EExclusiveGroup) nextLeftArg.getArg();
				else if(nextRightArg.getArgType().equals(JoinArg.QUERY_NODE))
					subQuery = (EExclusiveGroup) nextRightArg.getArg();
				
				List<String> sqProjList = subQuery.getProjectionList();
				List<String> joinProjList = polyQs.getVarList();
				
				/**
				 * for each solution of the already joined results edge of the join, create 
				 * a sub query (nextValuesSubQuery) having fixed values for the join variables
				 */
				EExclusiveGroup nextValuesSubQuery = getValuesSubQuery(nextJoinVars, polyQs, subQuery);
				
				List<DataSource> nextValuesSqDsList = nextValuesSubQuery.getDatasourceList();
				for (DataSource dataSource : nextValuesSqDsList) {
					ResultSetWrapper nextResultSetWrapR = executeSubQuery(nextValuesSubQuery, dataSource);
					
					PolyQuerySolution nextPolyQs = null;
					while( (nextPolyQs = nextResultSetWrapR.getNextResult()) != null) {
						
						PolyQuerySolution nextJoinPolyQS = new PolyQuerySolution();
						Map<String, String> varToValueMap1 = new HashMap<String, String>();
						Map<String, String> varToValueMap2 = new HashMap<String, String>();
						
						nextJoinVarList.addAll(joinProjList);
						for (String sqVar : sqProjList) {
							if(!nextJoinVarList.contains(sqVar)) {
								nextJoinVarList.add(sqVar);
							}
						}
						
						
						for (String nextJoinVar : nextJoinVarList) {
							
							if(nextJoinVars.contains(nextJoinVar)) {
								varToValueMap1.put(nextJoinVar, polyQs.getValue(nextJoinVar));
								varToValueMap2.put(nextJoinVar, polyQs.getValue(nextJoinVar));
							} else if(joinProjList.contains(nextJoinVar) && sqProjList.contains(nextJoinVar)) {
								varToValueMap1.put(nextJoinVar, polyQs.getValue(nextJoinVar));
								varToValueMap2.put(nextJoinVar, nextPolyQs.getValue(nextJoinVar));
							} else {
								if(joinProjList.contains(nextJoinVar)) {
									varToValueMap1.put(nextJoinVar, polyQs.getValue(nextJoinVar));
									varToValueMap2.put(nextJoinVar, polyQs.getValue(nextJoinVar));
								} else if(sqProjList.contains(nextJoinVar)) {
									varToValueMap1.put(nextJoinVar, nextPolyQs.getValue(nextJoinVar));
									varToValueMap2.put(nextJoinVar, nextPolyQs.getValue(nextJoinVar));
								}
							} 
						}
						
						List<PolyQuerySolution> nextJoinPolyQSList = new ArrayList<PolyQuerySolution>();
						
						nextJoinPolyQS.setVarList(nextJoinVarList);
						nextJoinPolyQS.setVarToValueMap(varToValueMap1);
						nextJoinPolyQSList.add(nextJoinPolyQS);
						
						if(!varToValueMap1.equals(varToValueMap2)) {
							PolyQuerySolution newJoinPolyQS = new PolyQuerySolution();
							newJoinPolyQS.setVarList(nextJoinVarList);
							newJoinPolyQS.setVarToValueMap(varToValueMap2);
							nextJoinPolyQSList.add(newJoinPolyQS);
						}
						
						return getJoinedResults(nextJoinId, nextJoinPolyQSList);
						
					}
				}
			
			} else {
				
				return polyQsList;
				
			}
			
		}
		
		return new ArrayList<PolyQuerySolution>();
		
	}
	
	
	
	
	public ResultSetWrapper executeSubQuery(EExclusiveGroup subQuery, DataSource datasource) {
		String datasourceType = datasource.getType();
		QueryService_New queryService = queryServiceFactory.getQueryService(datasourceType);
		ResultSetWrapper resultSetWrapL = queryService.executeQuery(subQuery, datasource);
		
		return resultSetWrapL;
	}
	
	
	private EExclusiveGroup getValuesSubQuery(List<String> joinVars, PolyQuerySolution polyQS, EExclusiveGroup rightSubQuery) {
		
		EExclusiveGroup valuesSubQuery = new EExclusiveGroup();
		
		for (String joinVar : joinVars) {
			
			String joinVarValue = polyQS.getValue(joinVar);
//			System.out.println("JoinVar Value: " + joinVar + " = " + joinVarValue);
			
			List<Triple> tpList = rightSubQuery.getTpList();
			List<Triple> newTpList = new ArrayList<Triple>();
			for (Triple triple : tpList) {
				Node subject = triple.getSubject();
				Node object = triple.getObject();
				
				Triple newTriple = null;
				
				Resource resource = ResourceFactory.createResource(joinVarValue);
				
				if(subject.isVariable() && subject.getName().equals(joinVar)) {
					newTriple = Triple.create(resource.asNode(), triple.getPredicate(), object);
				} else if(object.isVariable() && object.getName().equals(joinVar)) {
					newTriple = Triple.create(subject, triple.getPredicate(), resource.asNode());
				}
				
				if(newTriple != null) {
//					System.out.println("Old Triple: " + triple.toString());
//					System.out.println("New Triple: " + newTriple.toString());
					newTpList.add(newTriple);
				} else {
					newTpList.add(triple);
				}
				
			} // End of tpList (Triple List) Loop
			
			valuesSubQuery.setTpList(newTpList);
			valuesSubQuery.setDatasourceList(rightSubQuery.getDatasourceList());
			valuesSubQuery.setVarMap(rightSubQuery.getVarMap());
		} // End of JoinVars List Loop
		
		return valuesSubQuery;
	}
	
}

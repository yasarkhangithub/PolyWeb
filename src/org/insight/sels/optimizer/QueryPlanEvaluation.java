package org.insight.sels.optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeVisitor;
import org.apache.jena.graph.Node_Concrete;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.reasoner.TriplePattern;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.insight.sels.query.SubQuery;
import org.insight.sels.queryservice.QueryService;
import org.insight.sels.queryservice.QueryServiceFactory;
import org.insight.sels.result.PolyQuerySolution;
import org.insight.sels.result.PolyResultSet;

/**
 * 
 * @author Yasar Khan
 * @param <T>
 *
 */
public class QueryPlanEvaluation<T> {

	
	public PolyResultSet evaluatePlan(QueryPlan<T> queryPlan) {
		
		Map<Integer, FederatedJoin<T>> fedJoinMap = queryPlan.getFedJoinMap();
		Integer leafJoinId = queryPlan.getLeafJoinID();
		Boolean joinHasResults = Boolean.FALSE;
		
		/**
		 * Get the leaf join to start evaluating the query plan.
		 * Leaf join means where both the arguments are sub queries.
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
		PolyResultSet joinPolyRes = new PolyResultSet();
		List<PolyQuerySolution> joinPolyQSList = new ArrayList<PolyQuerySolution>();
		List<String> joinVarList = new ArrayList<String>();
		joinPolyRes.setVarNames(joinVarList);
		joinPolyRes.setQuerySolList(joinPolyQSList);
		
		
		/**
		 * Get sub queries from both edges of the leaf join.
		 */
		SubQuery leftSubQuery = (SubQuery) leftArg.getArg();
		SubQuery rightSubQuery = (SubQuery) rightArg.getArg();
		List<String> leftSQProjList = leftSubQuery.getProjectionList();
		List<String> rightSQProjList = rightSubQuery.getProjectionList();
		
		
		/**
		 * Execute the left sub query.
		 */
		QueryServiceFactory queryServiceFactory = new QueryServiceFactory();
		QueryService queryService = queryServiceFactory.getQueryService(leftSubQuery.getDatasource().getType());
		PolyResultSet leftPolyRes = queryService.executeQuery(leftSubQuery);
		
		
		/**
		 * Iterate over the poly result set obtained from executing the 
		 * left sub query.
		 */
		while(leftPolyRes.hasNext()) {
			
			PolyQuerySolution leftPolyQS = leftPolyRes.next();
			
			/**
			 * for each solution of the left sub query (leftSubQuery) edge of the join, create 
			 * a right sub query (valuesSubQuery) having fixed values for the join variables
			 */
			SubQuery valuesSubQuery = getValuesSubQuery(joinVars, leftPolyQS, rightSubQuery);
			
			
			/**
			 * Execute each of the valuesSubQuery
			 */
			QueryService valuesQueryService = queryServiceFactory.getQueryService(valuesSubQuery.getDatasource().getType());
			PolyResultSet valuesPolyRes = valuesQueryService.executeQuery(valuesSubQuery);
			
			while(valuesPolyRes.hasNext()) {
				
				joinHasResults = Boolean.TRUE;
				
				PolyQuerySolution valuesPolyQS = valuesPolyRes.next();
				
				PolyQuerySolution joinPolyQS = new PolyQuerySolution();
				Map<String, String> varToValueMapLeft = new HashMap<String, String>();
				Map<String, String> varToValueMapRight = new HashMap<String, String>();
				
				joinVarList.addAll(leftSQProjList);
				
				for (String rightSQVar : rightSQProjList) {
					if(!joinVarList.contains(rightSQVar)) {
						joinVarList.add(rightSQVar);
					}
				}
				
				for (String joinVar : joinVarList) {
					
					if(joinVars.contains(joinVar)) {
						varToValueMapLeft.put(joinVar, leftPolyQS.getValue(joinVar));
						varToValueMapRight.put(joinVar, leftPolyQS.getValue(joinVar));
					} else if(leftSQProjList.contains(joinVar) && rightSQProjList.contains(joinVar)) {
						varToValueMapLeft.put(joinVar, leftPolyQS.getValue(joinVar));
						varToValueMapRight.put(joinVar, valuesPolyQS.getValue(joinVar));
					} else {
						if(leftSQProjList.contains(joinVar)) {
							varToValueMapLeft.put(joinVar, leftPolyQS.getValue(joinVar));
							varToValueMapRight.put(joinVar, leftPolyQS.getValue(joinVar));
						} else if(rightSQProjList.contains(joinVar)) {
							varToValueMapLeft.put(joinVar, valuesPolyQS.getValue(joinVar));
							varToValueMapRight.put(joinVar, valuesPolyQS.getValue(joinVar));
						}
					} 
				}
				
				joinPolyQS.setVarList(joinVarList);
				joinPolyQS.setVarToValueMap(varToValueMapLeft);
				joinPolyQSList.add(joinPolyQS);
				
				if(!varToValueMapLeft.equals(varToValueMapRight)) {
					PolyQuerySolution newJoinPolyQS = new PolyQuerySolution();
					newJoinPolyQS.setVarList(joinVarList);
					newJoinPolyQS.setVarToValueMap(varToValueMapRight);
					joinPolyQSList.add(newJoinPolyQS);
				}
				
			}
			
		} // End of Poly Result Set for left sub query While Loop
 		
		
		/**
		 * Now going up the join tree after executing the leaf join.
		 * 
		 * if nextJoinId = null, means that there is no further join,
		 * 
		 * if nextJoinId != null, means that there exists another join.
		 * But joinHasResults needs to be true for further execution 
		 * because joinHasResults = TRUE means that the previous join 
		 * is not an empty join.
		 * 
		 * This loop ensures that every time a join is completed, it checks 
		 * for next join to be executed until there is no further join or 
		 * the current join is not an empty join.
		 */
		while(nextJoinId != null && joinHasResults.equals(Boolean.TRUE)) {
			
			joinHasResults = Boolean.FALSE;
			
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
			PolyResultSet nextJoinPolyRes = new PolyResultSet();
			List<PolyQuerySolution> nextJoinPolyQSList = new ArrayList<PolyQuerySolution>();
			List<String> nextJoinVarList = new ArrayList<String>();
			nextJoinPolyRes.setVarNames(nextJoinVarList);
			nextJoinPolyRes.setQuerySolList(nextJoinPolyQSList);
			
			SubQuery subQuery = null;
			
			if(nextLeftArg.getArgType().equals(JoinArg.QUERY_NODE))
				subQuery = (SubQuery) nextLeftArg.getArg();
			else if(nextRightArg.getArgType().equals(JoinArg.QUERY_NODE))
				subQuery = (SubQuery) nextRightArg.getArg();
			
			List<String> sqProjList = subQuery.getProjectionList();
			List<String> joinProjList = joinPolyRes.getVarNames();
			
			while(joinPolyRes.hasNext()) {
				
				PolyQuerySolution joinPolyQS = joinPolyRes.next();
				
				/**
				 * for each solution of the already joined results edge of the join, create 
				 * a sub query (nextValuesSubQuery) having fixed values for the join variables
				 */
				SubQuery nextValuesSubQuery = getValuesSubQuery(nextJoinVars, joinPolyQS, subQuery);
				
				/**
				 * Execute each of the nextValuesSubQuery
				 */
				QueryService nextValuesqueryService = queryServiceFactory.getQueryService(nextValuesSubQuery.getDatasource().getType());
				PolyResultSet nextValuesPolyRes = nextValuesqueryService.executeQuery(nextValuesSubQuery);
				
				while(nextValuesPolyRes.hasNext()) {
					
					joinHasResults = Boolean.TRUE;
					
					PolyQuerySolution nextValuesPolyQS = nextValuesPolyRes.next();
					
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
							varToValueMap1.put(nextJoinVar, joinPolyQS.getValue(nextJoinVar));
							varToValueMap2.put(nextJoinVar, joinPolyQS.getValue(nextJoinVar));
						} else if(joinProjList.contains(nextJoinVar) && sqProjList.contains(nextJoinVar)) {
							varToValueMap1.put(nextJoinVar, joinPolyQS.getValue(nextJoinVar));
							varToValueMap2.put(nextJoinVar, nextValuesPolyQS.getValue(nextJoinVar));
						} else {
							if(joinProjList.contains(nextJoinVar)) {
								varToValueMap1.put(nextJoinVar, joinPolyQS.getValue(nextJoinVar));
								varToValueMap2.put(nextJoinVar, joinPolyQS.getValue(nextJoinVar));
							} else if(sqProjList.contains(nextJoinVar)) {
								varToValueMap1.put(nextJoinVar, nextValuesPolyQS.getValue(nextJoinVar));
								varToValueMap2.put(nextJoinVar, nextValuesPolyQS.getValue(nextJoinVar));
							}
						} 
					}
					
					
					nextJoinPolyQS.setVarList(nextJoinVarList);
					nextJoinPolyQS.setVarToValueMap(varToValueMap1);
					nextJoinPolyQSList.add(nextJoinPolyQS);
					
					if(!varToValueMap1.equals(varToValueMap2)) {
						PolyQuerySolution newJoinPolyQS = new PolyQuerySolution();
						newJoinPolyQS.setVarList(nextJoinVarList);
						newJoinPolyQS.setVarToValueMap(varToValueMap2);
						nextJoinPolyQSList.add(newJoinPolyQS);
					}
					
				} // End of nextValuesPolyRes while loop
				
			}
			
			joinPolyRes = new PolyResultSet();
			joinPolyRes.setQuerySolList(nextJoinPolyRes.getQuerySolList());
			joinPolyRes.setVarNames(nextJoinPolyRes.getVarNames());
			
		}
		
		return joinPolyRes;
		
	}
	
	
	private SubQuery getValuesSubQuery(List<String> joinVars, PolyQuerySolution polyQS, SubQuery rightSubQuery) {
		
		SubQuery valuesSubQuery = new SubQuery();
		
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
			valuesSubQuery.setDatasource(rightSubQuery.getDatasource());
			valuesSubQuery.setVarMap(rightSubQuery.getVarMap());
		} // End of JoinVars List Loop
		
		return valuesSubQuery;
	}
	
}

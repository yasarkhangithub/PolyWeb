package org.insight.sels.optimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.insight.sels.config.Config;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.query.TPGroup;
import org.sels.insight.schema.SPARQLQuery;

/**
 * 
 * @author Yasar Khan
 *
 * @param <T>
 */
public class QueryPlanner<T> {

	private Map<Integer, FederatedJoin<T>> fedJoinMap = new HashMap<Integer, FederatedJoin<T>>();
	private Integer leafJoinID;

	public void createPlan(Map<Triple, List<DataSource>> tripleToDataSourceMap) {

		List<TPGroup> tpGroupList = createTPGroups(tripleToDataSourceMap);

		for (int count = 0; count < tpGroupList.size(); count++) {

			TPGroup tpGroup1 = tpGroupList.get(count);
			Set<Triple> tripleSet1 = new HashSet<Triple>(tpGroup1.getTpList());

			for (int k = count + 1; k < tpGroupList.size(); k++) {

				TPGroup tpGroup2 = tpGroupList.get(k);
				Set<Triple> tripleSet2 = new HashSet<Triple>(tpGroup2.getTpList());

				List<String> joinVar = new ArrayList<String>();

				joinVar = findJoin(tripleSet1, tripleSet2);

				if (joinVar.isEmpty()) {
//					System.err.println("There is no JOIN and no UNION");
				} else {
//					System.err.print("tp-group-" + tpGroup1.getId() + " JOIN tp-group-" + tpGroup2.getId()
//							+ " on Variables ");

					Integer fedJoinId = Integer.parseInt(tpGroup1.getId() + "" + tpGroup2.getId());
					FederatedJoin<T> fedJoin = new FederatedJoin<T>();
					List<String> joinVarList = new ArrayList<String>();

					/**
					 * If true means that same join already exists,
					 * 
					 * else means that this is either a new join or one of its
					 * argument may exist in the join tree or both of its
					 * arguments exits in the join tree separately.
					 */
					if (fedJoinMap.containsKey(fedJoinId)) {
						fedJoin = fedJoinMap.get(fedJoinId);
						joinVarList = fedJoin.getJoinVarList();

					} else {

						JoinArg<T> leftArg = new JoinArg<T>();
						leftArg.setArg((T) tpGroup1);
						leftArg.setArgType(JoinArg.QUERY_NODE);
						
						JoinArg<T> rightArg = new JoinArg<T>();
						rightArg.setArg((T) tpGroup2);
						rightArg.setArgType(JoinArg.QUERY_NODE);

						fedJoin.setId(fedJoinId);

						T prevJoinRight = (T) getJoinWithSameArg(rightArg);
						T prevJoinLeft = (T) getJoinWithSameArg(leftArg);

						/**
						 * If only the right hand argument exists already in
						 * previous join
						 */
						if (prevJoinRight != null && prevJoinLeft == null) {

							JoinArg<T> newRightArg = new JoinArg<T>();
							newRightArg.setArg(prevJoinRight);
							newRightArg.setArgType(JoinArg.JOIN_NODE);

							fedJoin.setJoinArgRight(newRightArg);
							fedJoin.setJoinArgLeft(leftArg);
							((FederatedJoin<T>) prevJoinRight).setNextJoinId(fedJoin.getId());

							fedJoinMap.put(fedJoinId, fedJoin);

							/**
							 * If only the left hand argument exists already in
							 * previous join
							 */
						} else if (prevJoinLeft != null && prevJoinRight == null) {

							JoinArg<T> newLeftArg = new JoinArg<T>();
							newLeftArg.setArg(prevJoinLeft);
							newLeftArg.setArgType(JoinArg.JOIN_NODE);

							fedJoin.setJoinArgRight(rightArg);
							fedJoin.setJoinArgLeft(newLeftArg);

							((FederatedJoin<T>) prevJoinLeft).setNextJoinId(fedJoin.getId());

							fedJoinMap.put(fedJoinId, fedJoin);

							/**
							 * If both arguments do not exist in any previous
							 * joins
							 * 
							 * else do nothing as both arguments exists in
							 * previous joins
							 */
						} else if (prevJoinLeft == null && prevJoinRight == null) {
							fedJoin.setJoinArgRight(rightArg);
							fedJoin.setJoinArgLeft(leftArg);
							leafJoinID = fedJoin.getId();

							fedJoinMap.put(fedJoinId, fedJoin);
						}

					}

					/**
					 * Create a list of variables on which the join occurs
					 */
					for (String var : joinVar) {
						if (!joinVarList.contains(var)) {
							joinVarList.add(var);
							fedJoin.setJoinVarList(joinVarList);

							/**
							 * Adding the join variable to the projection list
							 * of sub queries, if it is not there already
							 */
							if (!tpGroup1.getProjectionList().contains(var)) {
								tpGroup1.getProjectionList().add(var);
							}
							if (!tpGroup2.getProjectionList().contains(var)) {
								tpGroup2.getProjectionList().add(var);
							}
						}

//						System.err.println(" " + var + " ");
					}
				}
			}
		}

		printQueryPlan();

	}

	/**
	 * 
	 * @param tripleToDataSourceMap
	 * @return
	 */
	public List<TPGroup> createTPGroups(Map<Triple, List<DataSource>> tripleToDataSourceMap) {
		List<String> groupIDList = new ArrayList<String>();
		Map<String, TPGroup> tpGroupMap = new HashMap<String, TPGroup>();

		Set<Triple> tripleSet = tripleToDataSourceMap.keySet();
		int groupCount = 1;
		for (Triple triple : tripleSet) {
			
			List<DataSource> datasourceList = tripleToDataSourceMap.get(triple);
			String groupID = "";
			for (DataSource dataSource : datasourceList) {
				groupID += dataSource.getId();
			}

			if (groupIDList.contains(groupID)) {
				TPGroup tpGroup = tpGroupMap.get(groupID);
				tpGroup.getTpList().add(triple);
			} else {
				TPGroup tpGroup = new TPGroup();
				tpGroup.setGroupID(groupID);
				tpGroup.setId(""+groupCount++);
				tpGroup.setDatasourceList(datasourceList);
				tpGroup.getTpList().add(triple);
				tpGroupMap.put(groupID, tpGroup);
				groupIDList.add(groupID);
			}

		}

		List<TPGroup> tpGroupOptList = new ArrayList<TPGroup>();
		SPARQLQuery sparqlQuery = Config.getInstance().getSparqlQuery();
		List<String> mainQueryProjList = Config.getInstance().getSparqlQuery().getProjectionList();
		for (String id : groupIDList) {
			TPGroup tpGroup = tpGroupMap.get(id);
			tpGroup.calculateCost();
			tpGroup.addFilterExpr(sparqlQuery.getFilter());
			tpGroup.setProjectionList(getTPGroupProjection(mainQueryProjList, tpGroup.getTpList()));
			tpGroupOptList.add(tpGroup);
		}

		Collections.sort(tpGroupOptList);

//		System.out.println("\n\n --------------------------- \n\n");

//		for (TPGroup tpGroup : tpGroupOptList) {
////			System.out.println("ID: " + tpGroup.getId() + " === " + "Group ID: " + tpGroup.getGroupID() + " === " + "Cost: " + tpGroup.getCost());
//			List<Triple> tpList = tpGroup.getTpList();
//			for (Triple triple : tpList) {
////				System.out.println(triple.toString());
//			}
////			System.out.println("Projection List: " + tpGroup.getProjectionList());
//		}

		return tpGroupOptList;

	}

	/**
	 * Returns list of projection variables for a Triple Pattern Group
	 * 
	 * @param mainQueryProjList
	 * @param tpGroupProjList
	 * @return
	 */
	private List<String> getTPGroupProjection(List<String> mainQueryProjList, List<Triple> tpGroupTripleList) {
		List<String> tpGroupProjList = new ArrayList<String>();

		for (Triple triple : tpGroupTripleList) {
			Node subject = triple.getSubject();
			Node predicate = triple.getPredicate();
			Node object = triple.getObject();

			if (subject.isVariable()) {
				String subjectVar = subject.getName();
				if (mainQueryProjList.contains(subjectVar)) {
					if(!tpGroupProjList.contains(subjectVar))
						tpGroupProjList.add(subjectVar);
				}
			}

			if (predicate.isVariable()) {
				String predicateVar = predicate.getName();
				if (mainQueryProjList.contains(predicateVar)) {
					if(!tpGroupProjList.contains(predicateVar))
						tpGroupProjList.add(predicateVar);
				}
			}

			if (object.isVariable()) {
				String objectVar = object.getName();
				if (mainQueryProjList.contains(objectVar)) {
					if(!tpGroupProjList.contains(objectVar))
						tpGroupProjList.add(objectVar);
				}
			}

		}

		return tpGroupProjList;
	}

	
	/**
	 * Finds join between two Triple Pattern Groups.
	 * 
	 * @param tripleSet1
	 * @param tripleSet2
	 */
	public List<String> findJoin(Set<Triple> tripleSet1, Set<Triple> tripleSet2) {

		List<String> joinVar = new ArrayList<String>();

		for (Triple triple1 : tripleSet1) {
			
			Node subject1 = triple1.getSubject();
			Node predicate1 = triple1.getPredicate();
			Node object1 = triple1.getObject();
			
			for (Triple triple2 : tripleSet2) {
				
				/**
				 * Checking for subject-subject, subject-object and subject-predicate joins
				 */
				if(subject1.isVariable()) {
					String varName = subject1.getName();
					if (triple2.subjectMatches(subject1)) {
						if (!joinVar.contains(varName))
							joinVar.add(varName);
					} else if (triple2.objectMatches(subject1)) {
						if (!joinVar.contains(varName))
							joinVar.add(varName);
					} else if(triple2.predicateMatches(subject1)) {
						if (!joinVar.contains(varName))
							joinVar.add(varName);
					}
				}
				
				
				/**
				 * Checking for object-subject, object-object and object-predicate joins
				 */
				if(object1.isVariable()) {
					
					String varName = object1.getName();
					if (triple2.subjectMatches(object1)) {
						if (!joinVar.contains(varName))
							joinVar.add(varName);
					} else if (triple2.objectMatches(object1)) {
						if (!joinVar.contains(varName))
							joinVar.add(varName);
					} else if(triple2.predicateMatches(object1)) {
						if (!joinVar.contains(varName))
							joinVar.add(varName);
					}
					
				}
				
				
				/**
				 * Checking for predicate-subject, predicate-object and predicate-predicate joins
				 */
				if(predicate1.isVariable()) {
					String varName = predicate1.getName();
					if (triple2.subjectMatches(predicate1)) {
						if (!joinVar.contains(varName))
							joinVar.add(varName);
					} else if (triple2.objectMatches(predicate1)) {
						if (!joinVar.contains(varName))
							joinVar.add(varName);
					} else if(triple2.predicateMatches(predicate1)) {
						if (!joinVar.contains(varName))
							joinVar.add(varName);
					}
				}
			}
			
		}

		return joinVar;
	}

	

	/**
	 * This method returns the join which has the same argument as provided as
	 * parameter to this method.
	 * 
	 * @param joinArg
	 * @return
	 */
	public FederatedJoin<T> getJoinWithSameArg(JoinArg<T> joinArg) {
		FederatedJoin<T> fedJoin = null;

		Set<Integer> joinIdSet = fedJoinMap.keySet();

		for (Integer joinId : joinIdSet) {

			FederatedJoin<T> join = fedJoinMap.get(joinId);

			if (joinArg.getArgType().equals(JoinArg.QUERY_NODE)) {

				if (join.hasArg(joinArg)) {
					Integer nextJoinId = join.getNextJoinId();
					while(nextJoinId != null) {
						join = fedJoinMap.get(nextJoinId);
						nextJoinId = join.getNextJoinId();
					}
					fedJoin = join;
					break;
				}

			}

		}

		return fedJoin;
	}

	
	
	/**
	 * Prints the query plan (Join Tree) prepared above.
	 */
	public void printQueryPlan() {

		Set<Integer> joinIdSet = fedJoinMap.keySet();

//		System.out.println("");
//		System.out.println("");
//		System.out.println("----------------Query Plan Start----------------------");
//		System.out.println("");

		for (Integer joinId : joinIdSet) {
			FederatedJoin<T> join = fedJoinMap.get(joinId);
			Integer id = join.getId();
			JoinArg<T> leftArg = join.getJoinArgLeft();
			JoinArg<T> rightArg = join.getJoinArgRight();

//			System.out.println("[" + id + "] " + leftArg.toString() + " ***JOIN*** " + rightArg.toString());
		}

//		System.out.println("");
//		System.out.println("");
//		System.out.println("Now Starting from Leaf Node ..... ");
//		System.out.println("");

		FederatedJoin<T> join = fedJoinMap.get(leafJoinID);
		Integer id = join.getId();
		JoinArg<T> leftArg = join.getJoinArgLeft();
		JoinArg<T> rightArg = join.getJoinArgRight();
//		System.out.println("[" + id + "] " + leftArg.toString() + " ***JOIN*** " + rightArg.toString());

		Integer nextJoinId = join.getNextJoinId();

		while (nextJoinId != null) {

			FederatedJoin<T> nextJoin = fedJoinMap.get(nextJoinId);
			Integer nextId = nextJoin.getId();
			JoinArg<T> nextLeftArg = nextJoin.getJoinArgLeft();
			JoinArg<T> nextRightArg = nextJoin.getJoinArgRight();
//			System.out.println("[" + nextId + "] " + nextLeftArg.toString() + " ***JOIN*** " + nextRightArg.toString());

			nextJoinId = nextJoin.getNextJoinId();
		}

//		System.out.println("");
//		System.out.println("----------------Query Plan End------------------------");
//		System.out.println("");
//		System.out.println("");

	}

	public Map<Integer, FederatedJoin<T>> getFedJoinMap() {
		return fedJoinMap;
	}

	public void setFedJoinMap(Map<Integer, FederatedJoin<T>> fedJoinMap) {
		this.fedJoinMap = fedJoinMap;
	}

	public Integer getLeafJoinID() {
		return leafJoinID;
	}

	public void setLeafJoinID(Integer leafJoinID) {
		this.leafJoinID = leafJoinID;
	}

}

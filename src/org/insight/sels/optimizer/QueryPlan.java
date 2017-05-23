package org.insight.sels.optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.insight.sels.query.SubQuery;


/**
 * 
 * @author Yasar Khan
 *
 * @param <T>
 */
public class QueryPlan<T> {

	private Map<Integer, FederatedJoin<T>> fedJoinMap = new HashMap<Integer, FederatedJoin<T>>();
	private Integer leafJoinID;
	
	public void createPlan(List<SubQuery> subQueryList) {
		
		Object[] subQueries = subQueryList.toArray();
		
		for(int count=0; count < subQueryList.size(); count++) {
			
//			SubQuery subQuery1 = (SubQuery) subQueries[count];
			SubQuery subQuery1 = subQueryList.get(count);
			Set<Triple> tripleSet1 = new HashSet<Triple>(subQuery1.getTpList());
			
			for(int k=count+1; k < subQueryList.size(); k++) {
//				SubQuery subQuery2 = (SubQuery) subQueries[k];
				SubQuery subQuery2 = subQueryList.get(k);
				Set<Triple> tripleSet2 = new HashSet<Triple>(subQuery2.getTpList());
				
				if(!tripleSet1.equals(tripleSet2)) {
//					System.err.println("sub-query-" + subQuery1.getId() + " JOIN sub-query-" + subQuery2.getId());
					
					Set<Triple> symmetricDiff = getSymmetricDiff(tripleSet1, tripleSet2);
					
					for (Triple symmTriple : symmetricDiff) {
//						System.out.println("Symmetric Triple: " + symmTriple.toString());
						
						List<String> joinVar = new ArrayList<String>();
						
						if(tripleSet1.contains(symmTriple)) {
							joinVar = findJoin(symmTriple, tripleSet2);
						} else {
							joinVar = findJoin(symmTriple, tripleSet1);
						}
						
						if(joinVar.isEmpty()) {
							System.err.println("There is no JOIN and no UNION");
						} else {
							System.err.print("sub-query-" + subQuery1.getId() + " JOIN sub-query-" + subQuery2.getId() + " on Variables ");
							
							Integer fedJoinId = Integer.parseInt(subQuery1.getId()+""+subQuery2.getId());
							FederatedJoin<T> fedJoin = new FederatedJoin<T>();
							List<String> joinVarList = new ArrayList<String>();
							
							
							/**
							 * If true means that same join already exists,
							 * 
							 * else means that this is either a new join or one of its argument
							 * may exist in the join tree or both of its arguments exits in the
							 * join tree separately.
							 */
							if(fedJoinMap.containsKey(fedJoinId)) {   
								fedJoin = fedJoinMap.get(fedJoinId);
								joinVarList = fedJoin.getJoinVarList();
							
							} else { 
								
								JoinArg<T> rightArg = new JoinArg<T>();
								rightArg.setArg((T) subQuery2);
								rightArg.setArgType(JoinArg.QUERY_NODE);
								
								JoinArg<T> leftArg = new JoinArg<T>();
								leftArg.setArg((T) subQuery1);
								leftArg.setArgType(JoinArg.QUERY_NODE);
								
								fedJoin.setId(fedJoinId);
								
								T prevJoinRight = (T) getJoinWithSameArg(rightArg);
								T prevJoinLeft = (T) getJoinWithSameArg(leftArg);
								
								/**
								 * If only the right hand argument exists already in previous join
								 */
								if(prevJoinRight != null && prevJoinLeft == null) {
									
									JoinArg<T> newRightArg = new JoinArg<T>();
									newRightArg.setArg(prevJoinRight);
									newRightArg.setArgType(JoinArg.JOIN_NODE);
									
									fedJoin.setJoinArgRight(newRightArg);
									fedJoin.setJoinArgLeft(leftArg);
									( (FederatedJoin<T>) prevJoinRight).setNextJoinId(fedJoin.getId());
									
									fedJoinMap.put(fedJoinId, fedJoin);
								
								/**
								 * If only the left hand argument exists already in previous join
								 */
								} else if(prevJoinLeft != null && prevJoinRight == null) {
									
									JoinArg<T> newLeftArg = new JoinArg<T>();
									newLeftArg.setArg(prevJoinLeft);
									newLeftArg.setArgType(JoinArg.JOIN_NODE);
									
									fedJoin.setJoinArgRight(rightArg);
									fedJoin.setJoinArgLeft(newLeftArg);
									
									( (FederatedJoin<T>) prevJoinLeft).setNextJoinId(fedJoin.getId());
									
									fedJoinMap.put(fedJoinId, fedJoin);
								
								/**
								 * If both arguments do not exist in any previous joins
								 * 
								 * else do nothing as both arguments exists in previous joins
								 */
								} else if(prevJoinLeft == null && prevJoinRight == null) {
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
								if(!joinVarList.contains(var)) {
									joinVarList.add(var);
									fedJoin.setJoinVarList(joinVarList);
									
									/**
									 * Adding the join variable to the projection list of 
									 * sub queries, if it is not there already
									 */
									if(!subQuery1.getProjectionList().contains(var)) {
										subQuery1.getProjectionList().add(var);
									}
									if(!subQuery2.getProjectionList().contains(var)) {
										subQuery2.getProjectionList().add(var);
									}
								}
									
								System.err.println(" " + var + " ");
							}
						}
					}
				} else {
					System.err.println("sub-query-" + subQuery1.getId() + " UNION sub-query-" + subQuery2.getId());
				}
			}
		}
		
		printQueryPlan();
		
	}
	
	
	/**
	 * Finds join between a symmetric triple and a triple list.
	 * 
	 * @param symmTriple
	 * @param tripleSet
	 */
	public List<String> findJoin(Triple symmTriple, Set<Triple> tripleSet) {
		
		List<String> joinVar = new ArrayList<String>();
		
		Node symmSubj = symmTriple.getSubject();
		Node symmObj = symmTriple.getObject();
		
		for (Triple triple : tripleSet) {
			
			if(symmSubj.isVariable()) {
				String varName = symmSubj.getName();
				if(triple.subjectMatches(symmSubj)) {
//					System.out.println("S-S Join exists on variable ?" + symmSubj.getName() + " with " + triple.toString());
					if(!joinVar.contains(varName))
						joinVar.add(varName);
				} else if(triple.objectMatches(symmSubj)) {
//					System.out.println("S-O Join exists on variable ?" + symmSubj.getName() + " with " + triple.toString());
					if(!joinVar.contains(varName))
						joinVar.add(varName);
				}
			}
			
			if(symmObj.isVariable()) {
				String varName = symmObj.getName();
				if(triple.subjectMatches(symmObj)) {
//					System.out.println("S-O Join exists on variable ?" + symmSubj.getName() + " with " + triple.toString());
					if(!joinVar.contains(varName))
						joinVar.add(varName);
				} else if(triple.objectMatches(symmObj)) {
//					System.out.println("O-O Join exists on variable ?" + symmSubj.getName() + " with " + triple.toString());
					if(!joinVar.contains(varName))
						joinVar.add(varName);
				}
			}
			
		}
		
		return joinVar;
	}
	
	
	
	/**
	 * 
	 * Returns the symmetric difference between two (Triple) Sets
	 * 
	 * @param tripleSet1
	 * @param tripleSet2
	 * @return
	 */
	public Set<Triple> getSymmetricDiff(Set<Triple> tripleSet1, Set<Triple> tripleSet2) {
		Set<Triple> symmetricDiff = new HashSet<Triple>(tripleSet1);
		symmetricDiff.addAll(tripleSet2);
		// symmetricDiff now contains the union
		Set<Triple> tmp = new HashSet<Triple>(tripleSet1);
		tmp.retainAll(tripleSet2);
		// tmp now contains the intersection
		symmetricDiff.removeAll(tmp);
		// union minus intersection equals symmetric-difference
		
		return symmetricDiff;
	}
	
	
	
	/**
	 * This method returns the join which has the same argument as
	 * provided as parameter to this method.
	 * 
	 * @param joinArg
	 * @return
	 */
	public FederatedJoin<T> getJoinWithSameArg(JoinArg<T> joinArg) {
		FederatedJoin<T> fedJoin = null;
		
		Set<Integer> joinIdSet = fedJoinMap.keySet();
		
		for (Integer joinId : joinIdSet) {
			
			FederatedJoin<T> join = fedJoinMap.get(joinId);
			
			if(joinArg.getArgType().equals(JoinArg.QUERY_NODE)) {
				
				if(join.hasArg(joinArg)) {
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
		
		System.out.println("");
		System.out.println("");
		System.out.println("----------------Query Plan Start----------------------");
		System.out.println("");
		
		for (Integer joinId : joinIdSet) {
			FederatedJoin<T> join = fedJoinMap.get(joinId);
			Integer id = join.getId();
			JoinArg<T> leftArg = join.getJoinArgLeft();
			JoinArg<T> rightArg = join.getJoinArgRight();
			
			System.out.println("[" + id + "] " + leftArg.toString() + " ***JOIN*** " + rightArg.toString());
		}
		
		System.out.println("");
		System.out.println("");
		System.out.println("Now Starting from Leaf Node ..... ");
		System.out.println("");
		
		FederatedJoin<T> join = fedJoinMap.get(leafJoinID);
		Integer id = join.getId();
		JoinArg<T> leftArg = join.getJoinArgLeft();
		JoinArg<T> rightArg = join.getJoinArgRight();
		System.out.println("[" + id + "] " + leftArg.toString() + " ***JOIN*** " + rightArg.toString());
		
		Integer nextJoinId = join.getNextJoinId();
		
		while(nextJoinId != null) {
			
			FederatedJoin<T> nextJoin = fedJoinMap.get(nextJoinId);
			Integer nextId = nextJoin.getId();
			JoinArg<T> nextLeftArg = nextJoin.getJoinArgLeft();
			JoinArg<T> nextRightArg = nextJoin.getJoinArgRight();
			System.out.println("[" + nextId + "] " + nextLeftArg.toString() + " ***JOIN*** " + nextRightArg.toString());
			
			nextJoinId = nextJoin.getNextJoinId();
		}
		
		System.out.println("");
		System.out.println("----------------Query Plan End------------------------");
		System.out.println("");
		System.out.println("");
		
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

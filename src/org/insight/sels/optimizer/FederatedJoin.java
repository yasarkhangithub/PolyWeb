package org.insight.sels.optimizer;

import java.util.ArrayList;
import java.util.List;

import org.insight.sels.query.SubQuery;
import org.insight.sels.query.EExclusiveGroup;

/**
 * 
 * @author yaskha
 *
 * @param <T>
 */
public class FederatedJoin<T> {

	private Integer id;
	private JoinArg<T> joinArgLeft;
	private JoinArg<T> joinArgRight;
	private List<String> joinVarList = new ArrayList<String>();
	private Integer nextJoinId = null;
	
	
	public Integer getId() {
		return id;
	}
	
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	/**
	 * This method returns true if the join argument sent as parameter 
	 * already exists as an argument in this join.
	 * 
	 * @param joinArg
	 * @return
	 */
	public Boolean hasArg(JoinArg<T> joinArg) {
		Boolean isArg = Boolean.FALSE;
		
		String tpGroupId = ( (EExclusiveGroup) joinArg.getArg()).getId();
		
		JoinArg<T> leftArg = getJoinArgLeft();
		JoinArg<T> rightArg = getJoinArgRight();
		
		if(leftArg.getArgType().equals(JoinArg.QUERY_NODE)) {
			EExclusiveGroup leftTPGroup = (EExclusiveGroup) leftArg.getArg();
			
			if(leftTPGroup.getId().equals(tpGroupId)) {
				isArg = true;
			}
		}
		
		if(rightArg.getArgType().equals(JoinArg.QUERY_NODE)) {
			EExclusiveGroup rightTPGroup = (EExclusiveGroup) rightArg.getArg();
			
			if(rightTPGroup.getId().equals(tpGroupId)) {
				isArg = true;
			}
		}
		
		return isArg;
	}
	
	
	public JoinArg<T> getJoinArgLeft() {
		return joinArgLeft;
	}
	
	
	public void setJoinArgLeft(JoinArg<T> joinArgLeft) {
		this.joinArgLeft = joinArgLeft;
	}
	
	
	public JoinArg<T> getJoinArgRight() {
		return joinArgRight;
	}
	
	
	public void setJoinArgRight(JoinArg<T> joinArgRight) {
		this.joinArgRight = joinArgRight;
	}
	
	
	public List<String> getJoinVarList() {
		return joinVarList;
	}
	
	
	public void setJoinVarList(List<String> joinVarList) {
		this.joinVarList = joinVarList;
	}


	public Integer getNextJoinId() {
		return nextJoinId;
	}


	public void setNextJoinId(Integer nextJoinId) {
		this.nextJoinId = nextJoinId;
	}
	
	
}

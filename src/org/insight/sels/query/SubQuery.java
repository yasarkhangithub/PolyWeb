package org.insight.sels.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.insight.sels.datasources.DataSource;

/**
 * 
 * @author Yasar Khan
 *
 */
public class SubQuery implements Comparable {
	
	private Integer id;
	private List<Triple> tpList = new ArrayList<Triple>();
	private List<String> projectionList = new ArrayList<String>();
	private Map<String, QueryVar> varMap = new HashMap<String, QueryVar>();
	private DataSource datasource;
	private int cost;
	
	public SubQuery() {
		
	}
	
	public SubQuery(Integer id) {
		this.id = id;
	}
	
	
	@Override
	public int compareTo(Object o) {
		int compareCost = ((SubQuery) o).getCost();
		
		return compareCost-this.cost;
	}
	
	
	@Override
	public String toString() {
		String subQueryString = "";
		
		System.out.println("Sub Query: ");
		
		for (Triple triple : tpList) {
			System.out.println(triple.toString());
		}
		
		return subQueryString;
	}
	
	
	public void calculateCost() {
		for (Triple tp : tpList) {
			this.cost = this.cost + getTPCost(tp);
		}
	}
	
	
	public int getTPCost(Triple tp) {
		int cost = 0;
		
		Node subject = tp.getSubject();
		Node predicate = tp.getPredicate();
		Node object = tp.getObject();
		
		if(subject.isConcrete()) {
			cost++;
		}
		
		if(predicate.isConcrete()) {
			cost++;
		}
		
		if(object.isConcrete()) {
			cost++;
		}
		
		return cost;
	}
	
	
	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public List<Triple> getTpList() {
		return tpList;
	}
	
	
	
	public void setTpList(List<Triple> tpList) {
		this.tpList = tpList;
	}
	
	
	
	public List<String> getProjectionList() {
		return projectionList;
	}
	
	
	
	public void setProjectionList(List<String> projectionList) {
		this.projectionList = projectionList;
	}


	
	public Map<String, QueryVar> getVarMap() {
		return varMap;
	}



	public void setVarMap(Map<String, QueryVar> varMap) {
		this.varMap = varMap;
	}



	public DataSource getDatasource() {
		return datasource;
	}



	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
	
}

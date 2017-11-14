package org.insight.sels.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.insight.sels.datasources.DataSource;

/**
 * 
 * @author Yasar Khan
 *
 */
public class EExclusiveGroup implements Comparable, Cloneable {
	
	private String groupID;
	private String id;
	private List<Triple> tpList = new ArrayList<Triple>();
	private int cost;
	private List<String> projectionList = new ArrayList<String>();
	private List<DataSource> datasourceList = new ArrayList<DataSource>();
	private Map<String, QueryVar> varMap = new HashMap<String, QueryVar>();
	private Set<String> varSet = new HashSet<String>();
	private ExprList filterExprList = new ExprList();
	
	
	public EExclusiveGroup() {
		
	}
	
	public EExclusiveGroup(String gID, String iD, List<Triple> tps, int c, List<String> projections, List<DataSource> datasources, Map<String, QueryVar> vMap, Set<String> vSet, ExprList filterExprs) {
		this.groupID = gID;
		this.id = iD;
		this.tpList = new ArrayList<Triple>(tps);
		this.cost = c;
		this.projectionList = new ArrayList<String>(projections);
		this.datasourceList = new ArrayList<DataSource>(datasources);
		this.varMap = new HashMap<String, QueryVar>(vMap);
		this.varSet = new HashSet<String>(vSet);
		this.filterExprList = filterExprs;
	}
	
	public void addFilterExpr(OpFilter filter) {
		if(filter != null) {
			ExprList exprs = filter.getExprs();
			for (Expr expr : exprs) {
				Set<Var> exprVarSet = expr.getVarsMentioned();
				
				for (Var var : exprVarSet) {
					if(varSet.contains(var.getName())) {
						filterExprList.add(expr);
						System.out.println("GID ============================== " + this.getGroupID());
						break;
					}
				}
				
//				if(!Collections.disjoint(exprVarSet, varSet)) {
//					filterExprList.add(expr);
//				}
			}
		}
		
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
		
		if(subject.isVariable()) {
			varSet.add(subject.getName());
			cost++;
		}
		
		if(predicate.isVariable()) {
			varSet.add(predicate.getName());
			cost++;
		}
		
		if(object.isVariable()) {
			varSet.add(object.getName());
			cost++;
		}
		
		return cost;
	}
	
	@Override
	public int compareTo(Object o) {
		int compareCost = ((EExclusiveGroup) o).getCost();
		
		return this.cost-compareCost;
	}
	
	
	@Override
	public Object clone() {
		EExclusiveGroup subQuery = new EExclusiveGroup(groupID, id, tpList, cost, projectionList, datasourceList, varMap, varSet, filterExprList);
		
		return subQuery;
	}
	
	public String getGroupID() {
		return groupID;
	}
	
	
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	
	
	public List<Triple> getTpList() {
		return tpList;
	}
	
	
	public void setTpList(List<Triple> tpList) {
		this.tpList = tpList;
	}
	
	
	public int getCost() {
		return cost;
	}
	
	
	public void setCost(int cost) {
		this.cost = cost;
	}


	public List<String> getProjectionList() {
		return projectionList;
	}


	public void setProjectionList(List<String> projectionList) {
		this.projectionList = projectionList;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public List<DataSource> getDatasourceList() {
		return datasourceList;
	}


	public void setDatasourceList(List<DataSource> datasourceList) {
		this.datasourceList = datasourceList;
	}


	public Map<String, QueryVar> getVarMap() {
		return varMap;
	}


	public void setVarMap(Map<String, QueryVar> varMap) {
		this.varMap = varMap;
	}


	public Set<String> getVarSet() {
		return varSet;
	}


	public void setVarSet(Set<String> varSet) {
		this.varSet = varSet;
	}

	public ExprList getFilterExprList() {
		return filterExprList;
	}

	public void setFilterExprList(ExprList filterExprList) {
		this.filterExprList = filterExprList;
	}


}

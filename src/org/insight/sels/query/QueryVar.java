package org.insight.sels.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.insight.sels.querywriter.URITemplate;

/**
 * 
 * @author Yasar Khan
 *
 */
public class QueryVar {
	
	private String varName;
	private Node isRangeOf;
	private List<Node> isDomainOf = new ArrayList<Node>();
	private Boolean isRange = false;
	private Boolean isDomain = false;
	private List<String> alternateNameList;
	private Boolean isResultVar = false;
	private String template;
	private Boolean isTemplateVar = false;
	private Boolean isValued = Boolean.FALSE;
	private Node valuedNode;
	private URITemplate uriTemplate;
	private String tableName;
	private Set<String> predicateSet = new HashSet<String>();
	
	
	public QueryVar() {
		alternateNameList = new ArrayList<String>();
	}
	
	
	public String getVarName() {
		return varName;
	}
	
	
	public void setVarName(String varName) {
		this.varName = varName;
	}
	
	
	public Node getIsRangeOf() {
		return isRangeOf;
	}
	
	
	public void setIsRangeOf(Node isRangeOf) {
		this.isRangeOf = isRangeOf;
	}
	
	
	public List<Node> getIsDomainOf() {
		return isDomainOf;
	}
	
	
	public void setIsDomainOf(List<Node> isDomainOf) {
		this.isDomainOf = isDomainOf;
	}


	public Boolean getIsRange() {
		return isRange;
	}


	public void setIsRange(Boolean isRange) {
		this.isRange = isRange;
	}


	public Boolean getIsDomain() {
		return isDomain;
	}


	public void setIsDomain(Boolean isDomain) {
		this.isDomain = isDomain;
	}


	public List<String> getAlternateNameList() {
		return alternateNameList;
	}


	public void setAlternateNameList(List<String> alternateNameList) {
		this.alternateNameList = alternateNameList;
	}


	public Boolean getIsResultVar() {
		return isResultVar;
	}


	public void setIsResultVar(Boolean isResultVar) {
		this.isResultVar = isResultVar;
	}


	public String getTemplate() {
		return template;
	}


	public void setTemplate(String template) {
		this.template = template;
	}


	public Boolean getIsTemplateVar() {
		return isTemplateVar;
	}


	public void setIsTemplateVar(Boolean isTemplateVar) {
		this.isTemplateVar = isTemplateVar;
	}


	public Boolean getIsValued() {
		return isValued;
	}


	public void setIsValued(Boolean isValued) {
		this.isValued = isValued;
	}


	public Node getValuedNode() {
		return valuedNode;
	}


	public void setValuedNode(Node valuedNode) {
		this.valuedNode = valuedNode;
	}


	public URITemplate getUriTemplate() {
		return uriTemplate;
	}


	public void setUriTemplate(URITemplate uriTemplate) {
		this.uriTemplate = uriTemplate;
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public Set<String> getPredicateSet() {
		return predicateSet;
	}


	public void setPredicateSet(Set<String> predicateSet) {
		this.predicateSet = predicateSet;
	}


}

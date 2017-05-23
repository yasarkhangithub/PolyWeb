package org.insight.sels.query;

import java.util.List;

/**
 * 
 * @author Yasar Khan
 *
 */
public class Filter {

	private String operator;
	private List<FilterExpression> expList;
	
	
	public String getOperator() {
		return operator;
	}
	
	
	public void setOperator(String operator) {
		this.operator = operator;
	}


	public List<FilterExpression> getExpList() {
		return expList;
	}


	public void setExpList(List<FilterExpression> expList) {
		this.expList = expList;
	}
	
}

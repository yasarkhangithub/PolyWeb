package org.insight.sels.query;



/**
 * 
 * @author Yasar Khan
 *
 */
public class FilterExpression {
	
	private String leftOperand;
	private String rightOperand;
	private String operator;
	
	
	public String getLeftOperand() {
		return leftOperand;
	}
	
	
	public void setLeftOperand(String leftOperand) {
		this.leftOperand = leftOperand;
	}
	
	
	public String getRightOperand() {
		return rightOperand;
	}
	
	
	public void setRightOperand(String rightOperand) {
		this.rightOperand = rightOperand;
	}
	
	
	public String getOperator() {
		return operator;
	}
	
	
	public void setOperator(String operator) {
		this.operator = operator;
	}

}

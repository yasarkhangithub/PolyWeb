package org.insight.sels.query;

import java.util.List;

import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprFunction;
import org.apache.jena.sparql.expr.ExprFunction0;
import org.apache.jena.sparql.expr.ExprFunction1;
import org.apache.jena.sparql.expr.ExprFunction2;
import org.apache.jena.sparql.expr.ExprFunction3;
import org.apache.jena.sparql.expr.ExprFunctionN;
import org.apache.jena.sparql.expr.ExprFunctionOp;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.ExprVisitor;
import org.apache.jena.sparql.expr.NodeValue;

/**
 * 
 * @author Yasar Khan
 *
 */
public class FilterProcessor {
	
	Filter filters = new Filter();

	public void processFilter(OpFilter filter) {

		ExprList expList = filter.getExprs();
		
//		for (Expr expr : expList) {
//			expr.visit(new ExprVisitor() {
//				
//				@Override
//				public void visit(ExprAggregator arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void visit(ExprVar arg0) {
//					System.out.println("Var;;; "+arg0.toString());
//					
//				}
//				
//				@Override
//				public void visit(NodeValue arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void visit(ExprFunctionOp arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void visit(ExprFunctionN arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void visit(ExprFunction3 arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void visit(ExprFunction2 arg0) {
//					System.out.println(arg0.toString());
//					
//				}
//				
//				@Override
//				public void visit(ExprFunction1 arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void visit(ExprFunction0 arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//		}
//
		
		for (Expr expr : expList) {

			ExprFunction expFunc = expr.getFunction();

			String operator = expFunc.getOpName();

			Expr arg1 = expFunc.getArg(1);
			Expr arg2 = expFunc.getArg(2);
			if (!arg1.isFunction()) {
				String expVar = arg1.toString();
				String expOp = expFunc.getOpName();
				String expValue = arg2.toString();
			} else {
				processArg(arg1);
				processArg(arg2);
				filters.setOperator(operator);
			}

		}

	}
	
	
	public void processArg(Expr expr) {
		
		ExprFunction expFunc = expr.getFunction();

		String operator = expFunc.getOpName();
		
		Expr arg1 = expFunc.getArg(1);
		Expr arg2 = expFunc.getArg(2);
		if (!arg1.isFunction()) {
			String expVar = arg1.toString();
			String expOp = expFunc.getOpName();
			String expValue = arg2.toString();

			FilterExpression filterExp = new FilterExpression();
			filterExp.setLeftOperand(expVar);
			filterExp.setRightOperand(expValue);
			filterExp.setOperator(expOp);
			
			filters.getExpList().add(filterExp);
		}
		
	}

}

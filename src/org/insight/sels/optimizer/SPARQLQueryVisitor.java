package org.insight.sels.optimizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.op.OpAssign;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpConditional;
import org.apache.jena.sparql.algebra.op.OpDatasetNames;
import org.apache.jena.sparql.algebra.op.OpDiff;
import org.apache.jena.sparql.algebra.op.OpDisjunction;
import org.apache.jena.sparql.algebra.op.OpDistinct;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpGroup;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpLabel;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpList;
import org.apache.jena.sparql.algebra.op.OpMinus;
import org.apache.jena.sparql.algebra.op.OpNull;
import org.apache.jena.sparql.algebra.op.OpOrder;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProcedure;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpPropFunc;
import org.apache.jena.sparql.algebra.op.OpQuad;
import org.apache.jena.sparql.algebra.op.OpQuadBlock;
import org.apache.jena.sparql.algebra.op.OpQuadPattern;
import org.apache.jena.sparql.algebra.op.OpReduced;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.algebra.op.OpSlice;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.op.OpTopN;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
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
import org.insight.sels.config.Config;
import org.insight.sels.query.FilterProcessor;
import org.insight.sels.structures.Object;
import org.insight.sels.structures.Predicate;
import org.insight.sels.structures.Subject;
import org.sels.insight.schema.SPARQLQuery;

/**
 * 
 * @author Yasar Khan
 *
 */
public class SPARQLQueryVisitor implements OpVisitor {

	List<Subject> subjectList = new ArrayList<Subject>();
	List<Node> subjectNodeList = new ArrayList<Node>();
	SPARQLQuery sparqlQuery = Config.getInstance().getSparqlQuery();
	
	
	@Override
	public void visit(OpBGP arg0) {
		
		List<Triple> tpList = new ArrayList<Triple>();
		BasicPattern basicPatterns = arg0.getPattern();
		Iterator<Triple> tripleIter = basicPatterns.iterator();
//		List<Predicate> predicateList;
//		Subject subject = new Subject();
		while(tripleIter.hasNext()) {
//			
			Triple triple = tripleIter.next();
			tpList.add(triple);
//			Node subjectNode = triple.getSubject();
//			Node predicateNode = triple.getPredicate();
//			Node ObjectNode = triple.getObject();
//			
//			if(!subjectNodeList.contains(subjectNode)) {
//				subjectNodeList.add(subjectNode);
//				subject = null;
//				subject = new Subject();
//			}
//			
//			
//			if(subjectNode.isURI()) {
//				subject.setUri(subjectNode.getURI());
//				subject.setLocalName(subjectNode.getLocalName());
//			} else if(subjectNode.isVariable()) {
//				subject.setLocalName(subjectNode.getName());
//			}
//			
//			if(!subjectList.contains(subject)) {
//				subjectList.add(subject);
//			}
//			
//			Predicate predicate = new Predicate();
//			predicate.setUri(predicateNode.getURI());
//			predicate.setLocalName(predicateNode.getLocalName());
//			
//			Object object = new Object();
//			if(ObjectNode.isVariable()) {
//				object.setValue(ObjectNode.getName());
//			} else if(ObjectNode.isLiteral()) {
//				object.setValue(ObjectNode.getLiteralValue().toString());
//			} else if(ObjectNode.isURI()) {
//				object.setUri(ObjectNode.getURI());
//			}
//			
//			predicate.setObject(object);
//			
//			subject.getPredicateList().add(predicate);
//			sparqlQuery.setSubjectList(subjectList);
			sparqlQuery.setTpList(tpList);
			
//			System.out.println(subject.toString());
			
		}
		
		
	}

	@Override
	public void visit(OpQuadPattern arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpQuadBlock arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpTriple arg0) {
				
	}

	@Override
	public void visit(OpQuad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpPath arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpTable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpNull arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpProcedure arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpPropFunc arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpFilter arg0) {
		
		sparqlQuery.setFilter(arg0);
		
	}

	@Override
	public void visit(OpGraph arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpService arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpDatasetNames arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpLabel arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpAssign arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpExtend arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpJoin arg0) {		
	}

	@Override
	public void visit(OpLeftJoin arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpUnion arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpDiff arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpMinus arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpConditional arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpSequence arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpDisjunction arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpList arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpOrder arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpProject arg0) {
		
		Config config = Config.getInstance();
		SPARQLQuery sparqlQuery = config.getSparqlQuery();
		
		List<String> projectionList = new ArrayList<String>();
		
		List<Var> varList = arg0.getVars();
		
		for (Var var : varList) {
			projectionList.add(var.getName());
		}
		
		sparqlQuery.setProjectionList(projectionList);
		
	}

	@Override
	public void visit(OpReduced arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpDistinct arg0) {
//		System.out.println("DISTINCT " + arg0.getSubOp().getName());
		
	}

	@Override
	public void visit(OpSlice arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpGroup arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OpTopN arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

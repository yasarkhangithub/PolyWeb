package org.sels.insight.schema;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.insight.sels.optimizer.SPARQLQueryVisitor;
import org.insight.sels.structures.Predicate;
import org.insight.sels.structures.Subject;

/**
 * 
 * @author Yasar Khan
 *
 */
public class SPARQLQuery {
	
	private String queryString;
	private List<Subject> subjectList;
	private List<Triple> tpList;
	private List<String> projectionList;
	private OpFilter filter;
	
	
	/**
	 * Constructor of SPARQLQuery
	 * 
	 * @param queryString
	 */
	public SPARQLQuery(String queryString) {
		this.queryString = queryString;
		subjectList = new ArrayList<Subject>();
		tpList = new ArrayList<Triple>();
		projectionList = new ArrayList<String>();
	}

	
	
	@Override
	public String toString() {
		
		String query = "---------------------------------------------------------------- " + System.lineSeparator();
		
		for (Subject subject : subjectList) {
			query += "[ " + subject.getLocalName() + " ] ---> ";
			List<Predicate> predicateList = subject.getPredicateList();
			
			query += " { ";
			for (Predicate predicate : predicateList) {
				query += predicate.getLocalName() + ", "; 
			}
			
			query += " } " + System.lineSeparator();
			
		}
		
		query += "---------------------------------------------------------------- " + System.lineSeparator();
		
		return query;
		
	}
	
	
	public void parseSPARQLQuery() {
		Query query = QueryFactory.create(queryString);
		
		if(query.isQueryResultStar()) {
			List<String> varList = query.getResultVars();
			this.setProjectionList(varList);
		}
		
		Op op = Algebra.compile(query);
		OpWalker.walk(op, new SPARQLQueryVisitor());
	}
	

	/**
	 * 
	 * @return List of Subjects in a SPARQLQuery
	 */
	public List<Subject> getSubjectList() {
		return subjectList;
	}


	/**
	 * 
	 * @param subjectList
	 */
	public void setSubjectList(List<Subject> subjectList) {
		this.subjectList = subjectList;
	}



	public String getQueryString() {
		return queryString;
	}



	public void setQueryString(String queryString) {
		this.queryString = queryString;
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



	public OpFilter getFilter() {
		return filter;
	}



	public void setFilter(OpFilter filter) {
		this.filter = filter;
	}

}

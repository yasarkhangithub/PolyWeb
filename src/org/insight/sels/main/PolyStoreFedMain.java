package org.insight.sels.main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.insight.sels.config.Config;
import org.insight.sels.config.Database;
import org.insight.sels.optimizer.QueryExecution;
import org.insight.sels.result.PolyQuerySolution;
import org.insight.sels.result.PolyResultSet;
import org.insight.sels.stats.QueryExecutionStats;
import org.sels.insight.schema.SPARQLQuery;

/**
 * 
 * @author Yasar Khan
 *
 */
public class PolyStoreFedMain {

	public static void main(String[] args) {
		
        
		
		try {
			Config config = Config.getInstance();
			config.initialize();
//			
			SPARQLQuery sparqlQuery = config.getSparqlQuery();
			
			QueryExecution qExec = new QueryExecution();
			PolyResultSet rs = qExec.executeQuery(sparqlQuery);
			
			QueryExecutionStats qeStats = Config.getInstance().getQueryExecStats();			
			
			
			while(rs.hasNext()) {
				rs.next();
				qeStats.incrementResultCount();
			}
//			
//			
////			printResultSet(rs);
//			
			qeStats.printStats();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		System.exit(0);
		
	}
	
	
	
	/**
	 * This method prints the result set in console
	 * @param rs
	 */
	public static void printResultSet(PolyResultSet rs) {
		
		int rsCount = 0;
		while(rs.hasNext()) {
			
			rsCount ++;
			
			PolyQuerySolution qs = rs.next();
			
			List<String> varList = qs.getVarList();
			
			System.out.print("[" + rsCount + "]. ");
			
			for (String varName : varList) {
				String value = qs.getValue(varName);
				
				System.out.print("(" + varName + " = " + value + ") \t ");
			}
			
			System.out.println();
			
		}
		
		if(rsCount == 0)
			System.out.println("No Results Returned ... ");
		
	}
	

}

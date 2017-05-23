package org.insight.sels.result.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;
import org.insight.sels.result.PolyQuerySolution;

/**
 * 
 * @author Yasar Khan
 *
 */
public class RDFResultTransform extends ResultTransform {

	@Override
	public PolyQuerySolution tranform(Object querySolution) {
		
		QuerySolution jenaQSol = (QuerySolution) querySolution;
		
		PolyQuerySolution polyQSol = new PolyQuerySolution();
		Map<String, String> varToValueMap = new HashMap<String, String>();
		polyQSol.setVarToValueMap(varToValueMap);
		
		Iterator<String> varIter = jenaQSol.varNames();
		List<String> varList = new ArrayList<String>();
		while(varIter.hasNext()) {
			String varName = varIter.next();
			varList.add(varName);
			RDFNode varValueNode = jenaQSol.get(varName);
			String varValue = "";
			if(varValueNode.isLiteral())
				varValue = varValueNode.asLiteral().getValue().toString();
			else 
				varValue = varValueNode.toString();
			varToValueMap.put(varName, varValue);
		}
		
		polyQSol.setVarList(varList);
		polyQSol.setVarToValueMap(varToValueMap);
		
		return polyQSol;
	}

}

package org.insight.sels.result.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.insight.sels.query.QueryVar;
import org.insight.sels.querywriter.URITemplate;
import org.insight.sels.result.PolyQuerySolution;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * @author Yasar Khan
 *
 */
public class CSVResultTransform extends ResultTransform {

	@Override
	public PolyQuerySolution tranform(Object querySolution) {
		
		JsonNode csvRow = (JsonNode) querySolution;
		
		PolyQuerySolution polyQSol = new PolyQuerySolution();
		Map<String, String> varToValueMap = new HashMap<String, String>();
		List<String> varList = new ArrayList<String>();
		
		for (String varName : varKeySet) {
			
			QueryVar queryVar = varMap.get(varName);
			List<String> varAltList = queryVar.getAlternateNameList();
			
			String varValue = null;
			
			if(queryVar.getIsTemplateVar()) {
				URITemplate uriTemplate = queryVar.getUriTemplate();
				Map<String, String> varValueMap = new HashMap<String, String>();
				for (String varAltName : varAltList) {
					JsonNode n = csvRow.findValue(varAltName);
					if(n != null)
						varValueMap.put(varAltName, n.asText());
				}
				varValue = uriTemplate.putValuesInTemplate(varValueMap);
			} else {
				JsonNode n = csvRow.findValue(varAltList.get(0));
				if(n != null)
					varValue = n.asText();
			}
			
			varList.add(varName);
			varToValueMap.put(varName, varValue);
		}
		
		polyQSol.setVarToValueMap(varToValueMap);
		polyQSol.setVarList(varList);
		
		return polyQSol;
	}

}

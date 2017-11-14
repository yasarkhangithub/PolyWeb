package org.insight.sels.result.transform;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insight.sels.query.QueryVar;
import org.insight.sels.query.EExclusiveGroup;
import org.insight.sels.querywriter.URITemplate;
import org.insight.sels.result.PolyQuerySolution;

/**
 * 
 * @author Yasar Khan
 *
 */
public class RDBResultTransform extends ResultTransform {

	@Override
	public PolyQuerySolution tranform(Object querySolution) throws SQLException {
		
		ResultSet rs = (ResultSet) querySolution;
		
		PolyQuerySolution qs = new PolyQuerySolution();
		Map<String, String> varToValueMap = new HashMap<String, String>();
		qs.setVarToValueMap(varToValueMap);
		
		for (String varName : varKeySet) {
			
			QueryVar queryVar = varMap.get(varName);
			List<String> varAltList = queryVar.getAlternateNameList();
			String varValue = null;
			
			if(queryVar.getIsTemplateVar()) {
				URITemplate uriTemplate = queryVar.getUriTemplate();
				Map<String, String> varValueMap = new HashMap<String, String>();
				for (String varAltName : varAltList) {
					String value = rs.getString(varAltName);
//					System.out.println(varAltName + " =========== " + value);
					varValueMap.put(varAltName, value);
				}
				varValue = uriTemplate.putValuesInTemplate(varValueMap);
//				System.out.println("Value with Template +++++++++++++++++++++ " + varValue);
			} else {
				varValue = rs.getString(varAltList.get(0));
			}
			
			varToValueMap.put(varName, varValue);
			
		}
		
		return qs;
	}
	
	
	
}

package org.insight.sels.querywriter;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Yasar Khan
 *
 */
public class Operators {

	private Map<String, String> sqlOpMap = new HashMap<String, String>();
	private Map<String, String> csvOpMap = new HashMap<String, String>();
	
	public Operators() {
		sqlOpMap.put(">", ">");
		sqlOpMap.put(">=", ">=");
		sqlOpMap.put("<", "<");
		sqlOpMap.put("<=", "<=");
		sqlOpMap.put("=", "=");
		sqlOpMap.put("!=", "!=");
		
		csvOpMap.put("||", "OR");
		csvOpMap.put("&&", "AND");
		csvOpMap.put(">", ">");
		csvOpMap.put(">=", ">=");
		csvOpMap.put("<", "<");
		csvOpMap.put("<=", "<=");
		csvOpMap.put("=", "=");
		csvOpMap.put("!=", "!=");
	}
	
	public String getSQLOp(String op) {
		return sqlOpMap.get(op);
	}
	
	public String getCSVOp(String op) {
		return csvOpMap.get(op);
	}
	
}

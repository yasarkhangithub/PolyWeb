package org.insight.sels.query;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Yasar Khan
 *
 */
public class SQLQuery {

	private List<String> projectionList = new ArrayList<String>();
	private List<String> tableList = new ArrayList<String>();
	private String whereClause;
	
	/**
	 * Generates and returns SQL query string
	 * @return
	 */
	public String getQueryString() {
		
		String queryString = "";
				
		String projection = "";
		String tables = "";
		int count = 1;
		for (String proj : projectionList) {
			projection += proj;

			if (count < projectionList.size())
				projection += ",";

			count++;
		}

		for (String tableStr : tableList) {
			tables += tableStr;
		}

		if(whereClause.isEmpty())
			queryString += "SELECT DISTINCT " + projection + " FROM " + tables + " ";
		else
			queryString += "SELECT DISTINCT " + projection + " FROM " + tables + " WHERE " + whereClause + " ";
		
		return queryString;
	}
	
	public List<String> getProjectionList() {
		return projectionList;
	}
	
	
	public void setProjectionList(List<String> projectionList) {
		this.projectionList = projectionList;
	}
	
	
	public List<String> getTableList() {
		return tableList;
	}
	
	
	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
	
}

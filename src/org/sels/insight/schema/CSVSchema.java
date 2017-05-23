package org.sels.insight.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Yasar Khan
 *
 */
public class CSVSchema {

	private List<String> columnList = new ArrayList<String>();
	
	@Override
	public String toString() {
		
		String columnListString = "----------------------------------" + System.lineSeparator();
		columnListString += "No.\t" + "Column Name" + System.lineSeparator();
		columnListString += "----------------------------------";
		
		int count = 0;
		for (String columnName : columnList) {
			count++;
			
			columnListString += System.lineSeparator();
			columnListString += count + "  \t" + columnName;
			
		}
		
		columnListString += "----------------------------------" + System.lineSeparator();
		
		return columnListString;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getColumnList() {
		return columnList;
	}

	
	/**
	 * 
	 * @param columnList
	 */
	public void setColumnList(List<String> columnList) {
		this.columnList = columnList;
	}
	
}

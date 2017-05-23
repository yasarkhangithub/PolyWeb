package org.insight.sels.structures;

import java.util.List;

/**
 * 
 * @author Yasar Khan
 *
 */
public class Table {
	
	private String name;
	private List<String> columnList;
	
	
	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public List<String> getColumnList() {
		return columnList;
	}
	
	
	public void setColumnList(List<String> columnList) {
		this.columnList = columnList;
	}
	

}

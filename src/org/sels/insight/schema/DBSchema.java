package org.sels.insight.schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insight.sels.structures.Table;

/**
 * 
 * @author Yasar Khan
 *
 */
public class DBSchema {
	
	private List<Table> tableList;
	private Map<String, Table> columnToTableMap = new HashMap<String, Table>();
	
	
	@Override
	public String toString() {
		
		String dbSchemaString = "----------------------------------" + System.lineSeparator();
		dbSchemaString += "No.\t" + "Table\t" + "Columns" + System.lineSeparator();
		dbSchemaString += "----------------------------------" + System.lineSeparator();
		
		int count = 0;
		for (Table table : tableList) {
			count++;
			
			dbSchemaString += count + "\t" + table.getName() + "\t";
			
			List<String> columnList = table.getColumnList();
			for (String columnName : columnList) {
				dbSchemaString += columnName + ", ";
			}
			dbSchemaString += System.lineSeparator();
			
		}
		
		dbSchemaString += "----------------------------------" + System.lineSeparator();
		
		return dbSchemaString;
	}
	
	
	public List<Table> getTableList() {
		return tableList;
	}
	
	
	public void setTableList(List<Table> tableList) {
		this.tableList = tableList;
	}
	
	
	public Map<String, Table> getColumnToTableMap() {
		return columnToTableMap;
	}
	
	
	public void setColumnToTableMap(Map<String, Table> propToTableMap) {
		this.columnToTableMap = propToTableMap;
	}
	
	

}

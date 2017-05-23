package org.insight.sels.datasources;



/**
 * 
 * @author Yasar Khan
 *
 */
public class DataSourceFactory {
	
	
	
	/**
	 * Returns data source object based on the type of data source
	 * @param dataSourceType
	 * @return 
	 */
	public DataSource getDataSource(String dataSourceType) {
		
		if(dataSourceType == null) {
			return null;
		}
		
		if(dataSourceType.equalsIgnoreCase("RDF")) {
			return new RDFDataSource();
		} else if(dataSourceType.equalsIgnoreCase("RDB")) {
			return new RDBDataSource();
		} else if(dataSourceType.equalsIgnoreCase("CSV")) {
			return new CSVDataSource();
		}
		
		return null;
	}

}

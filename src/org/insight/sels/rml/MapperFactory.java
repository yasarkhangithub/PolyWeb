package org.insight.sels.rml;

import org.insight.sels.datasources.DataSource;
import org.insight.sels.result.transform.CSVResultTransform;
import org.insight.sels.result.transform.RDBResultTransform;
import org.insight.sels.result.transform.RDFResultTransform;

/**
 * 
 * @author Yasar Khan
 *
 */
public class MapperFactory {
	
	public Mapper getMapper(DataSource ds) {
		
		if(ds.getType() == null) {
			return null;
		}
		
		if(ds.getType().equalsIgnoreCase("RDB")) {
			return new R2RMLMapper(ds.getMapperModel());
		} else if(ds.getType().equalsIgnoreCase("CSV")) {
			return new RMLMapper(ds.getMapperModel());
		}
		
		return null;
		
	}

}

package org.insight.sels.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.insight.sels.datasources.DataSource;
import org.insight.sels.datasources.DataSourceFactory;
import org.insight.sels.rml.Mapper;
import org.insight.sels.rml.MapperFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author Yasar Khan
 *
 */
public class DataSourceUtil {
	
	
	public static List<DataSource> getDataSourceList(String filePath) {
		
		List<DataSource> dataSourceList = new ArrayList<DataSource>();
		DataSourceFactory dataSourceFactory = new DataSourceFactory();
		MapperFactory mapperFactory = new MapperFactory();
		
		JSONParser parser = new JSONParser();
		
		try {
			
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(filePath));
			
			Set keySet = jsonObject.keySet();
			Iterator iter = keySet.iterator();
			
			while(iter.hasNext()) {
				
				String key = (String) iter.next();
				JSONObject classArray = (JSONObject) jsonObject.get(key);
								
				String id = classArray.get("id").toString().trim();
				String type = classArray.get("type").toString().trim();
				String url = classArray.get("url").toString().trim();
				String username = classArray.get("username").toString().trim();
				String password = classArray.get("password").toString().trim();
				String dirPath = classArray.get("dirPath").toString().trim();
				String mapperPath = classArray.get("mapperPath").toString().trim();
				String defaultGraphs = classArray.get("defaultGraphs").toString().trim();
				
				
				DataSource dataSource = dataSourceFactory.getDataSource(type);
				
				dataSource.setId(Integer.parseInt(id));
				dataSource.setType(type);
				dataSource.setDataSourceURL(url);
				dataSource.setUserName(username);
				dataSource.setPassword(password);
				dataSource.setDirPath(dirPath);
				dataSource.setMapperPath(mapperPath);
//				Model m = loadModel(mapperPath);
				if(!mapperPath.isEmpty())
					dataSource.setMapperModel(loadModel(mapperPath));
				if(!defaultGraphs.isEmpty())
					dataSource.setDefaultGraphList(getDefaultGraphList(defaultGraphs));
				Mapper mapper = mapperFactory.getMapper(dataSource);
				if(mapper != null) {
					mapper.populatePredMap();
					mapper.populateColumnMap();
					mapper.populateTemplateMap();
					mapper.populateTripleMapSet();
				}
				dataSource.setMapper(mapper);
				
				dataSourceList.add(dataSource);
				
			}
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dataSourceList;
	}
	
	
	public static Model loadModel(String modelPath) {
		Model model = RDFDataMgr.loadModel(modelPath);
		
		return model;
	}
	
	private static List<String> getDefaultGraphList(String defaultGraphs) {
		List<String> defaultGraphList = new ArrayList<String>();
		
		String graphs[] = defaultGraphs.split("\\;");
		
		for (String graph : graphs) {
			defaultGraphList.add(graph);
		}
		
		return defaultGraphList;
	}
	
	public static void main(String args[]) {
		
		String filePath = "config/datasources.json";
		
		DataSourceUtil.getDataSourceList(filePath);
		
	}

}

package org.insight.sels.datasources;

import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.insight.sels.rml.Mapper;
import org.sels.insight.schema.Schema;

/**
 * 
 * @author Yasar Khan
 *
 */
public abstract class DataSource {
	
	private Integer id;
	private String type;
	private String dataSourceURL;
	private String userName;
	private String password;
	private String dirPath;
	private Schema schema;
	private String mapperPath;
	private Model mapperModel;
	private List<String> defaultGraphList;
	private Mapper mapper;
	
	
	public DataSource() {
		schema = new Schema();
	}
	
	
	public abstract void generateSchema();
	
	
	public abstract Boolean containPredicate(Node predicate);
	
	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getDataSourceURL() {
		return dataSourceURL;
	}
	
	
	public void setDataSourceURL(String dataSourceURL) {
		this.dataSourceURL = dataSourceURL;
	}
	
	
	public String getUserName() {
		return userName;
	}
	
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	public String getPassword() {
		return password;
	}
	
	
	public void setPassword(String password) {
		this.password = password;
	}



	public String getDirPath() {
		return dirPath;
	}


	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}


	public Schema getSchema() {
		return schema;
	}



	public void setSchema(Schema schema) {
		this.schema = schema;
	}


	public String getMapperPath() {
		return mapperPath;
	}


	public void setMapperPath(String mapperPath) {
		this.mapperPath = mapperPath;
	}


	public Model getMapperModel() {
		return mapperModel;
	}


	public void setMapperModel(Model mapperModel) {
		this.mapperModel = mapperModel;
	}


	public List<String> getDefaultGraphList() {
		return defaultGraphList;
	}


	public void setDefaultGraphList(List<String> defaultGraphList) {
		this.defaultGraphList = defaultGraphList;
	}


	public Mapper getMapper() {
		return mapper;
	}


	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

}

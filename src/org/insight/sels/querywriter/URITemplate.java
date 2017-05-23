package org.insight.sels.querywriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;

/**
 * 
 * @author Yasar Khan
 *
 */
public class URITemplate {
	
	private String templatedURI;
	private List<String> templateVarList;
	private String valuedURI;
	private String nameSpace;
	private String template;
	
	private int templateStartEndIndex;
	private List<String> templateArray = new ArrayList<String>();
	
	public URITemplate(String templatedURI) {
		setTemplatedURI(templatedURI);
		
		templateVarList  = new ArrayList<String>();
		
		processTemplate();
	}
	
	
	public void processTemplate() {
		
		int startIndexOf = templatedURI.indexOf('{');
		int searchFromIndex = 0;
		
		templateStartEndIndex = startIndexOf;
		nameSpace = templatedURI.substring(0, templateStartEndIndex);
		template = templatedURI.substring(templateStartEndIndex);
		
		startIndexOf = 0;
		while(startIndexOf != -1) {
			
			int endIndexOfVar = template.indexOf('}', searchFromIndex);
			int startIndexOfVar = startIndexOf+1;
			String templateVar = template.substring(startIndexOfVar, endIndexOfVar);
			templateVarList.add(templateVar);
			
			templateArray.add(templateVar);
			
			searchFromIndex = endIndexOfVar+1;
			
			startIndexOf = template.indexOf('{', searchFromIndex);
			if(startIndexOf != -1) {
				templateArray.add(template.substring(endIndexOfVar+1, startIndexOf));
			}
		}
		
//		for (String t : templateArray) {
//			System.out.println("Template Array Element: " + t);
//		}
		
	}
	
	
	public String putValuesInTemplate(Map<String, String> varValueMap) {
		
		Set<String> varSet = varValueMap.keySet();
		
		valuedURI = templatedURI;
		
		for (String var : varSet) {
			String value = varValueMap.get(var);
			valuedURI = valuedURI.replaceAll("\\{" + var + "\\}", value);
//			System.out.println("[Var="+var+"]---[value="+value+"]----[valuedURi="+valuedURI+"]");
		}
		
		return valuedURI;
	}
	
	
	public Map<String, String> getValuesInTemplate(String valuedURI) {
		
		Map<String, String> varValueMap = new HashMap<String, String>();
		String valuedTemplate = null;
		try {
			valuedTemplate = valuedURI.substring(templateStartEndIndex);
		} catch (Exception e) {
			System.out.println("Valued URI: " + valuedURI + " ====== " + templateStartEndIndex);
			System.exit(0);
		}
		
		
//		System.out.println("Valued Template: " + valuedTemplate);
		String var = null;
		String value = null;
		int startIndex = 0;
		int endIndex = 0;
		int count = 1;
		for (String elem : templateArray) {
			
			if(templateVarList.contains(elem)) {
				var = elem;
				value = valuedTemplate.substring(startIndex);
			} else {
				endIndex = valuedTemplate.indexOf(elem);
				value = valuedTemplate.substring(startIndex, endIndex);
				startIndex = endIndex+1;
			}
			
			if((count % 2) == 0 || count == templateArray.size()) {
				varValueMap.put(var, value);
				var = null;
				value = null;
			}
			count++;
		}
		
		return varValueMap;
	}
	
	
	public String getTemplatedURI() {
		return templatedURI;
	}

	
	public void setTemplatedURI(String templatedURI) {
		this.templatedURI = templatedURI;
	}


	public List<String> getTemplateVarList() {
		return templateVarList;
	}


	public void setTemplateVarList(List<String> templateVarList) {
		this.templateVarList = templateVarList;
	}


	public String getValuedURI() {
		return valuedURI;
	}


	public void setValuedURI(String uri) {
		this.valuedURI = uri;
	}


	public String getNameSpace() {
		return nameSpace;
	}


	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}


	public String getTemplate() {
		return template;
	}


	public void setTemplate(String template) {
		this.template = template;
	}
	
	

}

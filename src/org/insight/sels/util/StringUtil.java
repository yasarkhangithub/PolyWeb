package org.insight.sels.util;

import org.apache.jena.graph.Node;

/**
 * 
 * @author Yasar Khan
 *
 */
public class StringUtil {
	

	public static String getTemplateVar(String template) {
		
		String templateVar = template.substring(template.indexOf('{')+1, template.indexOf('}'));
		
		return templateVar;
		
	}

	public static String getTemplatedValue(String template, String value, String column) {
		
		String templatedValue = "";
		
		templatedValue = template.replaceAll("\\{" + column + "\\}", value);
		
		return templatedValue;
	}
	
	
	public static String getLocalName(Node rdfNode) {
		
		String localName = rdfNode.getLocalName();
		
		if(localName.isEmpty()) {
			String uri = rdfNode.getURI();
			
			if(uri.indexOf('#') == -1) {
				localName = uri.substring(uri.lastIndexOf('/')+1);
			} else {
				localName = uri.substring(uri.lastIndexOf('#')+1);
			}
		}
		
		return localName;
	}
	
}

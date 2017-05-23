package org.insight.sels.test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.insight.sels.querywriter.URITemplate;

/**
 * 
 * @author Yasar Khan
 *
 */
public class URITemplateTest {

	public static void main(String[] args) {
		
		processTemplateTest();

	}
	
	public static void processTemplateTest() {
		
		System.out.println("------------- Test 1 -------------");
		
		String template1 = "http://sels.insight.org/cancer-genomics/cnv/{id}";
		String valuedURI1 = "http://sels.insight.org/cancer-genomics/cnv/1";
		System.out.println("Template = " + template1);
		URITemplate uriTemplate1 = new URITemplate(template1);
		System.out.println("Namespace: " + uriTemplate1.getNameSpace());
		System.out.println("Template: " + uriTemplate1.getTemplate());
		List<String> templateVarList = uriTemplate1.getTemplateVarList();
		for (String templateVar : templateVarList) {
			System.out.println("Template Var: " + templateVar);
		}
		Map<String, String> varValueMap1 = uriTemplate1.getValuesInTemplate(valuedURI1);
		printMap(varValueMap1);
		
		
		System.out.println("------------- Test 2 -------------");
		
		String template2 = "http://sels.insight.org/cancer-genomics/cnv/{start}-{end}";
		String valuedURI2 = "http://sels.insight.org/cancer-genomics/cnv/123-456";
		System.out.println("Template = " + template2);
		URITemplate uriTemplate2 = new URITemplate(template2);
		System.out.println("Namespace: " + uriTemplate2.getNameSpace());
		System.out.println("Template: " + uriTemplate2.getTemplate());
		templateVarList = uriTemplate2.getTemplateVarList();
		for (String templateVar : templateVarList) {
			System.out.println("Template Var: " + templateVar);
		}
		Map<String, String> varValueMap2 = uriTemplate2.getValuesInTemplate(valuedURI2);
		printMap(varValueMap2);
		
		
		System.out.println("------------- Test 3 -------------");
		
		String template3 = "http://sels.insight.org/cancer-genomics/cnv/{start}-{end}/{id}";
		String valuedURI3 = "http://sels.insight.org/cancer-genomics/cnv/123-456/1";
		System.out.println("Template = " + template3);
		URITemplate uriTemplate3 = new URITemplate(template3);
		System.out.println("Namespace: " + uriTemplate3.getNameSpace());
		System.out.println("Template: " + uriTemplate3.getTemplate());
		templateVarList = uriTemplate3.getTemplateVarList();
		for (String templateVar : templateVarList) {
			System.out.println("Template Var: " + templateVar);
		}
		Map<String, String> varValueMap3 = uriTemplate3.getValuesInTemplate(valuedURI3);
		printMap(varValueMap3);
		
	}
	
	public static void printMap(Map<String, String> varValueMap) {
		Set<String> varSet = varValueMap.keySet();
		for (String var : varSet) {
			System.out.println("[Map] " + var + " = " + varValueMap.get(var));
		}
	}

}

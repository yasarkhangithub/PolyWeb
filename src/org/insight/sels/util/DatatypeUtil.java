package org.insight.sels.util;


/**
 * 
 * @author Yasar Khan
 *
 */
public class DatatypeUtil {
	
	
	public static Boolean isInt(String datatypeURI) {
		Boolean isInt = false;
		
		if(datatypeURI.equals(new String("http://www.w3.org/2001/XMLSchema#int")))
			isInt = true;
		
		return isInt;
	}
	
	
	public static Boolean isInteger(String datatypeURI) {
		Boolean isInteger = false;
		
		if(datatypeURI.equals(new String("http://www.w3.org/2001/XMLSchema#integer")))
			isInteger = true;
		
		return isInteger;
	}
	
	
	public static Boolean isFloat(String datatypeURI) {
		Boolean isFloat = false;
		
		if(datatypeURI.equals(new String("http://www.w3.org/2001/XMLSchema#float")))
			isFloat = true;
		
		return isFloat;
	}
	
	
	public static Boolean isDouble(String datatypeURI) {
		Boolean isDouble = false;
		
		if(datatypeURI.equals(new String("http://www.w3.org/2001/XMLSchema#double")))
			isDouble = true;
		
		return isDouble;
	}
	
	
	public static Boolean isLong(String datatypeURI) {
		Boolean isLong = false;
		
		if(datatypeURI.equals(new String("http://www.w3.org/2001/XMLSchema#long")))
			isLong = true;
		
		return isLong;
	}
	
	
	public static Boolean isShort(String datatypeURI) {
		Boolean isShort = false;
		
		if(datatypeURI.equals(new String("http://www.w3.org/2001/XMLSchema#short")))
			isShort = true;
		
		return isShort;
	}

}

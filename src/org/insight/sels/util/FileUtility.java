package org.insight.sels.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 
 * @author Yasar Khan
 *
 */
public class FileUtility {
	
	
	public static String readQueryString(String filePath) {
		
		Path queryPath = Paths.get(filePath);
        String queryString = null;
		try {
			queryString = new String(Files.readAllBytes(queryPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return queryString;
	}
	
}

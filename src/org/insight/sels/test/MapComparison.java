package org.insight.sels.test;

import java.util.HashMap;
import java.util.Map;

public class MapComparison {

	public static void main(String[] args) {
		
		Map<String, String> mapA = new HashMap<String, String>();
		mapA.put("A", "1");
		mapA.put("B", "2");

		Map<String, String> mapB = new HashMap<String, String>();
		mapB.put("B", "2");
		mapB.put("A", "1");
		
		System.out.println(mapA.equals(mapB));

	}

}

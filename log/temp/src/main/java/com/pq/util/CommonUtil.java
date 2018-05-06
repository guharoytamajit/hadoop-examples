package com.pq.util;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;

public class CommonUtil {

	// private Logger log = Logger.getLogger(Util.class);

	/**
	 * 
	 **/
	public static int calculateTimeDifference(long lastModTime, long currentTime) {
		long diffTime = currentTime - lastModTime;
		int retTime = (int) TimeUnit.MILLISECONDS.toMinutes(diffTime);
		return retTime;
	}

	public static boolean checkMatch(String line, String pattern, boolean isCaseSensitive) {
		Pattern p = null;
		if (isCaseSensitive) {
			p = Pattern.compile(pattern);
		} else {
			p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		}
		Matcher m = p.matcher(line);
		return m.find();
	}

	public static Integer getMapValue(Map<String, Integer> accessionNumberMap, String an) {
		Integer value = null;
//		for (Entry<String, Integer> entry : accessionNumberMap.entrySet()) {
//			if (entry.getKey().equals(an)) {
//				value = entry.getValue();
//			}
//		}
		return accessionNumberMap.get(an);
	}

	public static String updateFinalMapKey(Map<String, Integer> accessionNumberMap, String an) {
//		String updatedKey = "";
//		for (Entry<String, Integer> entry : accessionNumberMap.entrySet()) {
//			if (entry.getKey().equals(an)) {
//				updatedKey = entry.getKey() + "|" + String.valueOf(entry.getValue());
//			}
//		}
//		return updatedKey;
		return an + "|" + String.valueOf(accessionNumberMap.get(an));
	}
	
	public static String convertToJson(Object clsObj) {
		String jsonVal = "";
		ObjectMapper objMapper = new ObjectMapper();
		try {
			jsonVal = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(clsObj);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonVal;
	}
	
	public static String extractSequenceNumber(String inputFile,String dbName) {
		//TODO: this logic needs to be updated
		if(dbName.equalsIgnoreCase("MEDLINE-IP"))
		return inputFile.substring(7, 14).trim();
		else if(dbName.equalsIgnoreCase("BIOSISPreviews-IP")) {
		int indexOf = inputFile.indexOf(".");
		return inputFile.substring(indexOf-4, indexOf);
		}else if(dbName.equalsIgnoreCase("Inspec-IP")) {
			int indexOf = inputFile.indexOf(".");
			return inputFile.substring(indexOf-8, indexOf);
			}else {
			return null;
		}
		
		
	/*	Pattern r = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		  Matcher matcher = r.matcher(inputFile);
		  if (matcher.find()) {
	           return matcher.group(1);
	        }else {
	        	return null;
	        }*/
	}

}

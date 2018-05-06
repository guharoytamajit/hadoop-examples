package com.pq.bean.recordtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecordTypeConfig {
	private List<String> priority = null;
	private Map<String, List<Object>> processType;
	private String defaultType;
	
	public List<String> getPriority() {
		if (priority == null) {
			priority = new ArrayList<>();
		}
		return priority;
	}

	public void setPriority(List<String> priority) {
		this.priority = priority;
	}

	public String getDefaultType() {
		return defaultType;
	}

	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}

	public Map<String, List<Object>> getProcessType() {
		if(processType==null) {
			processType=new HashMap<>();
		}
		return processType;
	}

	public void setProcessType(Map<String, List<Object>> processType) {
		this.processType = processType;
	}
}
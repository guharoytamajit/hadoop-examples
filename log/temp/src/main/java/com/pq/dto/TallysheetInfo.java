package com.pq.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.pq.bean.RecordCount;

public class TallysheetInfo {
	private String ipName;
	private String acqDateTime;
	private String sysDateTime;
	private Map<String, RecordCount> acqRecordCounter;
	private Map<String, RecordCount> normRecordCounter;
	private String fileSize;

	public String getIpName() {
		return ipName;
	}

	public void setIpName(String ipName) {
		this.ipName = ipName;
	}

	public String getAcqDateTime() {
		return acqDateTime;
	}

	public void setAcqDateTime(String acqDateTime) {
		this.acqDateTime = acqDateTime;
	}

	public String getSysDateTime() {
		return sysDateTime;
	}

	public void setSysDateTime(String sysDateTime) {
		this.sysDateTime = sysDateTime;
	}

	public Map<String, RecordCount> getAcqRecordCounter() {
		if(acqRecordCounter==null) {
			acqRecordCounter=new TreeMap<String, RecordCount>();
		}
		return acqRecordCounter;
	}

	public void setAcqRecordCounter(Map<String, RecordCount> acqRecordCounter) {
		this.acqRecordCounter = acqRecordCounter;
	}

	public Map<String, RecordCount> getNormRecordCounter() {
		if(normRecordCounter==null) {
			normRecordCounter=new TreeMap<String, RecordCount>();
		}
		return normRecordCounter;
	}

	public void setNormRecordCounter(Map<String, RecordCount> normRecordCounter) {
		this.normRecordCounter = normRecordCounter;
	}

	public TallysheetInfo(String ipName, String acqDateTime, String sysDateTime,String fileSize) {
		super();
		this.ipName = ipName;
		this.acqDateTime = acqDateTime;
		this.sysDateTime = sysDateTime;
		this.fileSize = fileSize;
	}

	public TallysheetInfo() {
		super();

	}
	
	public  TallysheetInfo merge(TallysheetInfo tallysheetInfo) {
		this.setAcqDateTime(tallysheetInfo.getAcqDateTime());
		this.setIpName(tallysheetInfo.getIpName());
		this.setSysDateTime(tallysheetInfo.getSysDateTime());
		for(Map.Entry<String, RecordCount> entry:tallysheetInfo.getAcqRecordCounter().entrySet()) {
			
			RecordCount recordCount = entry.getValue();
			recordCount.setSize(tallysheetInfo.getFileSize());
			this.getAcqRecordCounter().put(entry.getKey(), recordCount);
		}
		for(Map.Entry<String, RecordCount> entry:tallysheetInfo.getNormRecordCounter().entrySet()) {
			RecordCount recordCount = entry.getValue();
			this.getNormRecordCounter().put(entry.getKey(), recordCount);
		}
		return this;
	}

	
	public RecordCount fetchTotalAcqRecords() {
		RecordCount recordCount=new RecordCount();
		for(Map.Entry<String, RecordCount> entry:acqRecordCounter.entrySet()) {
			recordCount=RecordCount.merge(recordCount, entry.getValue());
		}
		return recordCount;
	}
	
	public RecordCount fetchTotalNormRecords() {
		RecordCount recordCount=new RecordCount();
		for(Map.Entry<String, RecordCount> entry:normRecordCounter.entrySet()) {
			recordCount=RecordCount.merge(recordCount, entry.getValue());
		}
		return recordCount;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

}

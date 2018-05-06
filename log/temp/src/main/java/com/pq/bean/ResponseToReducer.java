package com.pq.bean;


public class ResponseToReducer {

	private String record;
	private CfInfo cfInfo;
	private String sysDateTime;
	private String currentFileModDateTime;
	private String sequenceNumber;
	
	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}
	public CfInfo getCfInfo() {
		return cfInfo;
	}
	public void setCfInfo(CfInfo cfInfo) {
		this.cfInfo = cfInfo;
	}
	public String getSysDateTime() {
		return sysDateTime;
	}
	public void setSysDateTime(String sysDateTime) {
		this.sysDateTime = sysDateTime;
	}
	public String getCurrentFileModDateTime() {
		return currentFileModDateTime;
	}
	public void setCurrentFileModDateTime(String currentFileModDateTime) {
		this.currentFileModDateTime = currentFileModDateTime;
	}
	public String getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}

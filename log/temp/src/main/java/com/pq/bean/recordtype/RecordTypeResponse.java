package com.pq.bean.recordtype;

public class RecordTypeResponse{
	String recordType;
	String recordDetectionType;
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getRecordDetectionType() {
		return recordDetectionType;
	}
	public void setRecordDetectionType(String recordDetectionType) {
		this.recordDetectionType = recordDetectionType;
	}
	public RecordTypeResponse(String recordType, String recordDetectionType) {
		super();
		this.recordType = recordType;
		this.recordDetectionType = recordDetectionType;
	}
	
	public RecordTypeResponse() {
		super();
	}
	
}

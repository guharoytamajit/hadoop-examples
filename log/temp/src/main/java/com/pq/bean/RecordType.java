package com.pq.bean;

public enum RecordType {
	STORY("A"), DELETE("D");

	private final String code;

	RecordType(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}
	
	  public static RecordType fromString(String text) {
		    for (RecordType b : RecordType.values()) {
		      if (b.code.equalsIgnoreCase(text)) {
		        return b;
		      }
		    }
		    return null;
		  }

}

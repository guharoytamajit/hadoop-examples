package com.pq.bean;

import org.apache.hadoop.fs.FSDataOutputStream;

public class NormFileDetail {
	FSDataOutputStream outputStream;
	String fileName;
	public FSDataOutputStream getOutputStream() {
		return outputStream;
	}
	public void setOutputStream(FSDataOutputStream outputStream) {
		this.outputStream = outputStream;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	
	
}

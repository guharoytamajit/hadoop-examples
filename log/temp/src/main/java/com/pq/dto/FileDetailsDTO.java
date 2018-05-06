package com.pq.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder( { "name", "size", "fileRecCount" })
public class FileDetailsDTO {

	@JsonProperty("NAME")
	private String name;

	@JsonProperty("SIZE")
	private int size;

	@JsonProperty("FILE_REC_COUNT")
	private int fileRecCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getFileRecCount() {
		return fileRecCount;
	}

	public void setFileRecCount(int fileRecCount) {
		this.fileRecCount = fileRecCount;
	}
}

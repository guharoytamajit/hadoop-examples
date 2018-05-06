package com.pq.bean.tallysheet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "size", "fileRecCount" })
public class FileDetails {

	@XmlElement(name = "NAME")
	private String name;

	@XmlElement(name = "SIZE")
	private String size;

	@XmlElement(name = "FILE_REC_COUNT")
	private String fileRecCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getFileRecCount() {
		return fileRecCount;
	}

	public void setFileRecCount(String fileRecCount) {
		this.fileRecCount = fileRecCount;
	}
}

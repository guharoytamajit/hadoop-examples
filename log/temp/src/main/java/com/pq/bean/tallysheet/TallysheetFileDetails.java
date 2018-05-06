package com.pq.bean.tallysheet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TallysheetFileDetails {

	@XmlElement(name = "FILE")
	private List<FileDetails> fileDetails;

	public List<FileDetails> getFileDetails() {
		if(fileDetails==null) {
			fileDetails=new ArrayList<FileDetails>();
		}
		return fileDetails;
	}

	public void setFileDetails(List<FileDetails> fileDetails) {
		this.fileDetails = fileDetails;
	}
}

package com.pq.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"add", "aud", "aup", "del", "tot", "upd", "tallysheetFileDetails"})
public class CountSuppliedByIpDTO {
	
	private String add = "0";
		
	private String aud = "0";
		
	private String aup = "0";
		
	private String del = "0";
		
	//private String files;
		
	private String tot = "0";
		
	private String upd = "0";
		
	private List<FileDetailsDTO> fileDetails;
	
	public String getAdd() {
		return add;
	}

	@JsonProperty("ADD")
	public void setAdd(String add) {
		this.add = add;
	}

	public String getAud() {
		return aud;
	}

	@JsonProperty("AUD")
	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getAup() {
		return aup;
	}

	@JsonProperty("AUP")
	public void setAup(String aup) {
		this.aup = aup;
	}

	public String getDel() {
		return del;
	}

	@JsonProperty("DEL")
	public void setDel(String del) {
		this.del = del;
	}

//	public String getFiles() {
//		return files;
//	}
//
//	@XmlElement(name="FILES")
//	public void setFiles(String files) {
//		this.files = files;
//	}

	public String getTot() {
		return tot;
	}

	@JsonProperty("TOT")
	public void setTot(String tot) {
		this.tot = tot;
	}

	public String getUpd() {
		return upd;
	}

	@JsonProperty("UPD")
	public void setUpd(String upd) {
		this.upd = upd;
	}

	@JsonProperty("VENDOR_FILES")
	public List<FileDetailsDTO> getFileDetails() {
		if(fileDetails ==null) {
			fileDetails=new ArrayList<>();
		}
		return fileDetails;
	}

	public void setFileDetails(List<FileDetailsDTO> fileDetails) {
		this.fileDetails = fileDetails;
	}

}

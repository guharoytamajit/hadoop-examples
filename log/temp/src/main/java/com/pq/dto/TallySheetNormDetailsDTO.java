package com.pq.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"add", "aud", "aup", "del", "err", "jobName", "normDate", "tallysheetFileDetails", "setName", "tot", "upd"})
public class TallySheetNormDetailsDTO {

	@JsonProperty( "ADD")
	private int add;

	@JsonProperty("AUD")
	private int aud;

	@JsonProperty( "AUP")
	private int aup;

	@JsonProperty("DEL")
	private int del;

	@JsonProperty("ERR")
	private int err;

//	@JsonProperty( "JOBNAME")
//	private String jobName;

	@JsonProperty( "NORM_DATE")
	private String normDate;

	@JsonProperty( "NORMALIZED_FILES")
	private List<FileDetailsDTO> fileDetails;

	@JsonProperty("SETNAME")
	private String setName;

	@JsonProperty("TOT")
	private int tot;

	@JsonProperty("UPD")
	private int upd;

	public int getAdd() {
		return add;
	}

	public void setAdd(int add) {
		this.add = add;
	}

	public int getAud() {
		return aud;
	}

	public void setAud(int aud) {
		this.aud = aud;
	}

	public int getAup() {
		return aup;
	}

	public void setAup(int aup) {
		this.aup = aup;
	}

	public int getDel() {
		return del;
	}

	public void setDel(int del) {
		this.del = del;
	}

	public int getErr() {
		return err;
	}

	public void setErr(int err) {
		this.err = err;
	}

//	public String getJobName() {
//		return jobName;
//	}
//
//	public void setJobName(String jobName) {
//		this.jobName = jobName;
//	}

	public String getNormDate() {
		return normDate;
	}

	public void setNormDate(String normDate) {
		this.normDate = normDate;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public int getTot() {
		return tot;
	}

	public void setTot(int tot) {
		this.tot = tot;
	}

	public int getUpd() {
		return upd;
	}

	public void setUpd(int upd) {
		this.upd = upd;
	}
	public List<FileDetailsDTO> getFileDetails() {
		if(fileDetails==null) {
			fileDetails=new ArrayList<FileDetailsDTO>();
		}
		return fileDetails;
	}

	public void setFileDetails(List<FileDetailsDTO> fileDetails) {
		this.fileDetails = fileDetails;
	}

}

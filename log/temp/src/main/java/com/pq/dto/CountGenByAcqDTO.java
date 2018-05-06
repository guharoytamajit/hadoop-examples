package com.pq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"add", "aud", "aup", "del", "files", "tot", "upd"})
public class CountGenByAcqDTO {
	
	private int add ;
		
	private int aud ;
		
	private int aup ;
		
	private int del ;
		
	private int files ;
		
	private int tot ;
		
	private int upd ;

	public int getAdd() {
		return add;
	}

	@JsonProperty("ADD")
	public void setAdd(int add) {
		this.add = add;
	}

	public int getAud() {
		return aud;
	}

	@JsonProperty("AUD")
	public void setAud(int aud) {
		this.aud = aud;
	}

	public int getAup() {
		return aup;
	}

	@JsonProperty("AUP")
	public void setAup(int aup) {
		this.aup = aup;
	}

	public int getDel() {
		return del;
	}

	@JsonProperty("DEL")
	public void setDel(int del) {
		this.del = del;
	}

	public int getFiles() {
		return files;
	}

	@JsonProperty("FILES")
	public void setFiles(int files) {
		this.files = files;
	}

	public int getTot() {
		return tot;
	}

	@JsonProperty("TOT")
	public void setTot(int tot) {
		this.tot = tot;
	}

	public int getUpd() {
		return upd;
	}

	@JsonProperty("UPD")
	public void setUpd(int upd) {
		this.upd = upd;
	}

}

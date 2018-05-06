package com.pq.bean.tallysheet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"add", "aud", "aup", "del", "tot", "upd", "tallysheetFileDetails"})
public class CountSuppliedByIp {
	
	private String add = "0";
		
	private String aud = "0";
		
	private String aup = "0";
		
	private String del = "0";
		
	//private String files;
		
	private String tot = "0";
		
	private String upd = "0";
		
	private TallysheetFileDetails tallysheetFileDetails;
	
	public String getAdd() {
		return add;
	}

	@XmlElement(name="ADD")
	public void setAdd(String add) {
		this.add = add;
	}

	public String getAud() {
		return aud;
	}

	@XmlElement(name="AUD")
	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getAup() {
		return aup;
	}

	@XmlElement(name="AUP")
	public void setAup(String aup) {
		this.aup = aup;
	}

	public String getDel() {
		return del;
	}

	@XmlElement(name="DEL")
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

	@XmlElement(name="TOT")
	public void setTot(String tot) {
		this.tot = tot;
	}

	public String getUpd() {
		return upd;
	}

	@XmlElement(name="UPD")
	public void setUpd(String upd) {
		this.upd = upd;
	}

	public TallysheetFileDetails getTallysheetFileDetails() {
		return tallysheetFileDetails;
	}

	@XmlElement(name="VENDOR_FILES")
	public void setTallysheetFileDetails(TallysheetFileDetails tallysheetFileDetails) {
		this.tallysheetFileDetails = tallysheetFileDetails;
	}
}

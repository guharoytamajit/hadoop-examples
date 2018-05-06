package com.pq.bean.tallysheet;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"add", "aud", "aup", "del", "err", "jobName", "normDate", "tallysheetFileDetails", "setName", "tot", "upd"})
public class TallySheetNormDetails {

	@XmlElement(name = "ADD")
	private String add;

	@XmlElement(name = "AUD")
	private String aud;

	@XmlElement(name = "AUP")
	private String aup;

	@XmlElement(name = "DEL")
	private String del;

	@XmlElement(name = "ERR")
	private String err;

	@XmlElement(name = "JOBNAME")
	private String jobName;

	@XmlElement(name = "NORM_DATE")
	private String normDate;

	@XmlElement(name = "NORMALIZED_FILES")
	private TallysheetFileDetails tallysheetFileDetails;

	@XmlElement(name = "SETNAME")
	private String setName;

	@XmlElement(name = "TOT")
	private String tot;

	@XmlElement(name = "UPD")
	private String upd;

	public String getAdd() {
		return add;
	}

	public void setAdd(String add) {
		this.add = add;
	}

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getAup() {
		return aup;
	}

	public void setAup(String aup) {
		this.aup = aup;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	public String getErr() {
		return err;
	}

	public void setErr(String err) {
		this.err = err;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

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

	public String getTot() {
		return tot;
	}

	public void setTot(String tot) {
		this.tot = tot;
	}

	public String getUpd() {
		return upd;
	}

	public void setUpd(String upd) {
		this.upd = upd;
	}

	public TallysheetFileDetails getTallysheetFileDetails() {
		return tallysheetFileDetails;
	}

	public void setTallysheetFileDetails(TallysheetFileDetails tallysheetFileDetails) {
		this.tallysheetFileDetails = tallysheetFileDetails;
	}
}

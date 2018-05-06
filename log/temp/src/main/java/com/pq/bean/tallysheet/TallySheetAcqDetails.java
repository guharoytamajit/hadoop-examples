package com.pq.bean.tallysheet;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"acqJob", "configName", "tallysheetFileDetails", "acqDate", "countGenByAcq", "vendorDate", "countSuppliedByIp"})
public class TallySheetAcqDetails {
	
	@XmlElement(name="ACQ_JOB")
	private String acqJob;	
	
	@XmlElement(name="CONFIG_NAME")
	private String configName;	
	
	@XmlElement(name="ACQ_FILES")
	private TallysheetFileDetails tallysheetFileDetails;	
	
	@XmlElement(name="ACQ_DATE")
	private String acqDate;
	
	@XmlElement(name="COUNT_GENERATED_BY_ACQ")
	private CountGenByAcq countGenByAcq;
	
	@XmlElement(name="VENDOR_DATE")
	private String vendorDate;	
	
	@XmlElement(name="COUNT_SUPPLIED_BY_IP")
	private CountSuppliedByIp countSuppliedByIp;

	
	public String getAcqJob() {
		return acqJob;
	}

	
	public void setAcqJob(String acqJob) {
		this.acqJob = acqJob;
	}

	public String getConfigName() {
		return configName;
	}

	
	public void setConfigName(String configName) {
		this.configName = configName;
	}

	

	public String getAcqDate() {
		return acqDate;
	}

	
	public void setAcqDate(String acqDate) {
		this.acqDate = acqDate;
	}

	public CountGenByAcq getCountGenByAcq() {
		return countGenByAcq;
	}

	
	public void setCountGenByAcq(CountGenByAcq countGenByAcq) {
		this.countGenByAcq = countGenByAcq;
	}

	public String getVendorDate() {
		return vendorDate;
	}

	
	public void setVendorDate(String vendorDate) {
		this.vendorDate = vendorDate;
	}

	public CountSuppliedByIp getCountSuppliedByIp() {
		return countSuppliedByIp;
	}

	
	public void setCountSuppliedByIp(CountSuppliedByIp countSuppliedByIp) {
		this.countSuppliedByIp = countSuppliedByIp;
	}


	public TallysheetFileDetails getTallysheetFileDetails() {
		return tallysheetFileDetails;
	}


	public void setTallysheetFileDetails(TallysheetFileDetails tallysheetFileDetails) {
		this.tallysheetFileDetails = tallysheetFileDetails;
	}
}

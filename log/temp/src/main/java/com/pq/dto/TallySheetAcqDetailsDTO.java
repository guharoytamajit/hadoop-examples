package com.pq.dto;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonPropertyOrder({"acqJob", "configName", "tallysheetFileDetails", "acqDate", "countGenByAcq", "vendorDate", "countSuppliedByIp"})
public class TallySheetAcqDetailsDTO {
//	@JsonProperty("ACQ_JOB")
//	private String acqJob;	
	
//	@JsonProperty("CONFIG_NAME")
//	private String configName;	
	
	@JsonProperty("ACQ_FILES")
	private List<FileDetailsDTO> fileDetails;
	
	@JsonProperty("ACQ_DATE")
	private String acqDate;
	
	@JsonProperty("COUNT_GENERATED_BY_ACQ")
	private CountGenByAcqDTO countGenByAcq;
	
//	@JsonProperty("VENDOR_DATE")
//	private String vendorDate;	
	
//	@JsonProperty("COUNT_SUPPLIED_BY_IP")
//	private CountSuppliedByIpDTO countSuppliedByIp;

//	public String getAcqJob() {
//		return acqJob;
//	}

//	public void setAcqJob(String acqJob) {
//		this.acqJob = acqJob;
//	}

//	public String getConfigName() {
//		return configName;
//	}
//
//	public void setConfigName(String configName) {
//		this.configName = configName;
//	}



	public List<FileDetailsDTO> getFileDetails() {
		if(fileDetails==null) {
			fileDetails=new ArrayList<>();
		}
		return fileDetails;
	}

	public void setFileDetails(List<FileDetailsDTO> fileDetails) {
		this.fileDetails = fileDetails;
	}

	public String getAcqDate() {
		return acqDate;
	}

	public void setAcqDate(String acqDate) {
		this.acqDate = acqDate;
	}

	public CountGenByAcqDTO getCountGenByAcq() {
		return countGenByAcq;
	}

	public void setCountGenByAcq(CountGenByAcqDTO countGenByAcq) {
		this.countGenByAcq = countGenByAcq;
	}

//	public String getVendorDate() {
//		return vendorDate;
//	}
//
//	public void setVendorDate(String vendorDate) {
//		this.vendorDate = vendorDate;
//	}
//
//	public CountSuppliedByIpDTO getCountSuppliedByIp() {
//		return countSuppliedByIp;
//	}
//
//	public void setCountSuppliedByIp(CountSuppliedByIpDTO countSuppliedByIp) {
//		this.countSuppliedByIp = countSuppliedByIp;
//	}
	
}

package com.pq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder( {"configName","batchId", "tallySheetAcqDetails","lastUpdateDate", "tallySheetNormDetails" , "version"})
public class TallySheetDTO {

	@JsonProperty("CONFIG_NAME")
	private String configName;
	
	@JsonProperty("BATCHID")
	private String batchId;
	

	@JsonProperty("ACQUISITION")
	private TallySheetAcqDetailsDTO tallySheetAcqDetails;

	@JsonProperty("NORMALIZATION")
	private TallySheetNormDetailsDTO tallySheetNormDetails;
	
	@JsonProperty("LAST_UPDATE_DATE")
	private String lastUpdateDate;
	@JsonProperty("VERSION")
	private int version;
	
	@JsonProperty("BATCH_STATUS")
	private String finalStatus;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public TallySheetAcqDetailsDTO getTallySheetAcqDetails() {
		return tallySheetAcqDetails;
	}

	public void setTallySheetAcqDetails(TallySheetAcqDetailsDTO tallySheetAcqDetails) {
		this.tallySheetAcqDetails = tallySheetAcqDetails;
	}

	public TallySheetNormDetailsDTO getTallySheetNormDetails() {
		return tallySheetNormDetails;
	}

	public void setTallySheetNormDetails(TallySheetNormDetailsDTO tallySheetNormDetails) {
		this.tallySheetNormDetails = tallySheetNormDetails;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(String finalStatus) {
		this.finalStatus = finalStatus;
	}
	
	

}

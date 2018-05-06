package com.pq.bean.tallysheet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "TALLY_SHEET")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "version", "tallySheetAcqDetails", "tallySheetNormDetails" })
public class TallySheetXmlConfig {

	@XmlElement(name = "VERSION")
	private String version;

	@XmlElement(name = "ACQUISITION")
	private TallySheetAcqDetails tallySheetAcqDetails;

	@XmlElement(name = "NORMALIZATION")
	private TallySheetNormDetails tallySheetNormDetails;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public TallySheetAcqDetails getTallySheetAcqDetails() {
		return tallySheetAcqDetails;
	}

	public void setTallySheetAcqDetails(TallySheetAcqDetails tallySheetAcqDetails) {
		this.tallySheetAcqDetails = tallySheetAcqDetails;
	}

	public TallySheetNormDetails getTallySheetNormDetails() {
		return tallySheetNormDetails;
	}

	public void setTallySheetNormDetails(TallySheetNormDetails tallySheetNormDetails) {
		this.tallySheetNormDetails = tallySheetNormDetails;
	}

}

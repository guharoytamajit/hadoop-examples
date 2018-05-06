package com.pq.tallysheet.service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pq.bean.RecordCount;
import com.pq.dto.TallysheetInfo;
import com.pq.util.AccessS3Util;

public class LocalTallysheetSvcImpl implements ITallysheetSvc {

	private  String DB_LOCATION = null;
	public ObjectMapper mapper = new ObjectMapper();

	@Override
	public TallysheetInfo init(String jobName,String seqNum,String ipName, String acqDateTime, String sysDateTime,String fileSize,String mode)
			throws IOException {
		TallysheetInfo tallysheetInfo = new TallysheetInfo(ipName, acqDateTime, sysDateTime,fileSize);
		String dir = DB_LOCATION + jobName;
		if(mode.equals("LOCAL")) {
		FileWriter fileWriter = new FileWriter(dir+"/"+seqNum);
		mapper.writeValue(fileWriter, tallysheetInfo);
		fileWriter.close();
		}else {
			
			String content = mapper.writeValueAsString(tallysheetInfo);
			new AccessS3Util().uploadToS3(dir+"/"+seqNum, content);
			
		}
		return tallysheetInfo;
	}

	@Override
	public TallysheetInfo populateCounts(String jobName,String seqNum, Map<String, RecordCount> acqRecordCounter,
			Map<String, RecordCount> normRecordCounter,String mode) throws IOException {
		TallysheetInfo tallysheetInfo =null;
		if(mode.equals("LOCAL")) {
		 tallysheetInfo = mapper.readValue(new FileReader(DB_LOCATION + jobName+"/"+seqNum), TallysheetInfo.class);
		}else {
			System.out.println("jsonfile loction:"+DB_LOCATION + jobName+"/"+seqNum);
			tallysheetInfo = mapper.readValue(new AccessS3Util().readFromS3(DB_LOCATION + jobName+"/"+seqNum), TallysheetInfo.class);
		}
		for(Map.Entry<String, RecordCount> entry:acqRecordCounter.entrySet()) {
			tallysheetInfo.getAcqRecordCounter().put(entry.getKey(), entry.getValue());
		}
		for(Map.Entry<String, RecordCount> entry:normRecordCounter.entrySet()) {
			tallysheetInfo.getNormRecordCounter().put(entry.getKey(), entry.getValue());
		}
		if(mode.equals("LOCAL")) {
		FileWriter fileWriter = new FileWriter(DB_LOCATION + jobName+"/"+seqNum);
		mapper.writeValue(fileWriter, tallysheetInfo);
		fileWriter.close();
		}else {
			String content = mapper.writeValueAsString(tallysheetInfo);
			new AccessS3Util().uploadToS3(DB_LOCATION + jobName+"/"+seqNum, content);
		}
		return tallysheetInfo;
	}

	public LocalTallysheetSvcImpl(String dbLocation) {
		super();
		this.DB_LOCATION = dbLocation;
	}
	
	

}

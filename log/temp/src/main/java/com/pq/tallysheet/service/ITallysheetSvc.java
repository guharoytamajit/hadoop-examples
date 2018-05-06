package com.pq.tallysheet.service;

import java.io.IOException;
import java.util.Map;

import com.pq.bean.RecordCount;
import com.pq.dto.TallysheetInfo;

public interface ITallysheetSvc {
	public  TallysheetInfo init(String jobName,String seqNum,String ipName,String acqDateTime,String sysDateTime,String fileSize,String mode) throws  IOException;
	
	public TallysheetInfo populateCounts(String jobName,String seqNum, Map<String, RecordCount> acqRecordCounter,Map<String, RecordCount> normRecordCounter,String mode) throws  IOException;

}

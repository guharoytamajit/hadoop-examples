package com.pq.transformer;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pq.bean.tallysheet.CountGenByAcq;
import com.pq.bean.tallysheet.CountSuppliedByIp;
import com.pq.bean.tallysheet.FileDetails;
import com.pq.bean.tallysheet.TallySheetAcqDetails;
import com.pq.bean.tallysheet.TallySheetNormDetails;
import com.pq.bean.tallysheet.TallySheetXmlConfig;
import com.pq.bean.tallysheet.TallysheetFileDetails;
import com.pq.dto.CountGenByAcqDTO;
import com.pq.dto.CountSuppliedByIpDTO;
import com.pq.dto.FileDetailsDTO;
import com.pq.dto.TallySheetAcqDetailsDTO;
import com.pq.dto.TallySheetDTO;
import com.pq.dto.TallySheetNormDetailsDTO;

public class TallysheetTransformer {
public static TallySheetDTO beanToDTO(TallySheetXmlConfig tallysheet) {
	TallySheetDTO tallysheetDTO=null;
	if(tallysheet!=null) {
		tallysheetDTO=new TallySheetDTO();
		tallysheetDTO.setVersion(stringToInt(tallysheet.getVersion()));
		tallysheetDTO.setFinalStatus("Normalization-Completed");
		
		TallySheetAcqDetails tallySheetAcqDetails = tallysheet.getTallySheetAcqDetails();
		tallysheetDTO.setTallySheetAcqDetails(tallySheetAcqDetailsBeanToDTO(tallySheetAcqDetails));
		tallysheetDTO.setTallySheetNormDetails(tallySheetNormDetailsBeanToDTO(tallysheet.getTallySheetNormDetails()));
		
		if (tallySheetAcqDetails != null) {
			tallysheetDTO.setConfigName(tallySheetAcqDetails.getConfigName());
			tallysheetDTO.setBatchId(tallySheetAcqDetails.getAcqJob());
		}
		
	}
	
	
	return tallysheetDTO;
}

	private static TallySheetNormDetailsDTO tallySheetNormDetailsBeanToDTO(TallySheetNormDetails tallySheetNormDetails) {
		TallySheetNormDetailsDTO tallySheetNormDetailsDTO=null;
		if(tallySheetNormDetails!=null) {
			tallySheetNormDetailsDTO=new TallySheetNormDetailsDTO();
			tallySheetNormDetailsDTO.setAdd(stringToInt(tallySheetNormDetails.getAdd()));
			tallySheetNormDetailsDTO.setAud(stringToInt(tallySheetNormDetails.getAud()));
			tallySheetNormDetailsDTO.setAup(stringToInt(tallySheetNormDetails.getAup()));
			tallySheetNormDetailsDTO.setDel(stringToInt(tallySheetNormDetails.getDel()));
			tallySheetNormDetailsDTO.setErr(stringToInt(tallySheetNormDetails.getErr()));
//			tallySheetNormDetailsDTO.setJobName(tallySheetNormDetails.getJobName());
			tallySheetNormDetailsDTO.setNormDate(tallySheetNormDetails.getNormDate());
			tallySheetNormDetailsDTO.setSetName(tallySheetNormDetails.getSetName());
			for(FileDetails fileDetails:tallySheetNormDetails.getTallysheetFileDetails().getFileDetails()) {
				tallySheetNormDetailsDTO.getFileDetails().add((fileDetailsBeanToDTO(fileDetails)));
			}
			tallySheetNormDetailsDTO.setTot(stringToInt(tallySheetNormDetails.getTot()));
			tallySheetNormDetailsDTO.setUpd(stringToInt(tallySheetNormDetails.getUpd()));
			
		}
	return tallySheetNormDetailsDTO;
}

	public static TallySheetAcqDetailsDTO tallySheetAcqDetailsBeanToDTO(TallySheetAcqDetails tallySheetAcqDetails) {
		TallySheetAcqDetailsDTO tallySheetAcqDetailsDTO = null;
		if (tallySheetAcqDetails != null) {
			tallySheetAcqDetailsDTO = new TallySheetAcqDetailsDTO();

			tallySheetAcqDetailsDTO.setAcqDate(tallySheetAcqDetails.getAcqDate());
//			tallySheetAcqDetailsDTO.setAcqJob(tallySheetAcqDetails.getAcqJob());
//			tallySheetAcqDetailsDTO.setConfigName(tallySheetAcqDetails.getConfigName());
//			tallySheetAcqDetailsDTO.setVendorDate(tallySheetAcqDetails.getVendorDate());

			CountGenByAcq countGenByAcq = tallySheetAcqDetails.getCountGenByAcq();
			if (countGenByAcq != null) {
				CountGenByAcqDTO countGenByAcqDTO = new CountGenByAcqDTO();
				countGenByAcqDTO.setAdd(stringToInt(countGenByAcq.getAdd()));
				countGenByAcqDTO.setAud(stringToInt(countGenByAcq.getAud()));
				countGenByAcqDTO.setAup(stringToInt(countGenByAcq.getAup()));
				countGenByAcqDTO.setDel(stringToInt(countGenByAcq.getDel()));
				countGenByAcqDTO.setFiles(stringToInt(countGenByAcq.getFiles()));
				countGenByAcqDTO.setTot(stringToInt(countGenByAcq.getTot()));
				countGenByAcqDTO.setUpd(stringToInt(countGenByAcq.getUpd()));
				tallySheetAcqDetailsDTO.setCountGenByAcq(countGenByAcqDTO);
			}
			CountSuppliedByIp countSuppliedByIp = tallySheetAcqDetails.getCountSuppliedByIp();
			if (countSuppliedByIp != null) {
				CountSuppliedByIpDTO countSuppliedByIpDTO=new CountSuppliedByIpDTO();
				countSuppliedByIpDTO.setAdd(countSuppliedByIp.getAdd());
				countSuppliedByIpDTO.setAud(countSuppliedByIp.getAud());
				countSuppliedByIpDTO.setAup(countSuppliedByIp.getAup());
				countSuppliedByIpDTO.setDel(countSuppliedByIp.getDel());
				countSuppliedByIpDTO.setTot(countSuppliedByIp.getTot());
				countSuppliedByIpDTO.setUpd(countSuppliedByIp.getUpd());
				for(FileDetails fileDetails:countSuppliedByIp.getTallysheetFileDetails().getFileDetails()) {
					
					countSuppliedByIpDTO.getFileDetails().add((fileDetailsBeanToDTO(fileDetails)));
				}
			//	tallySheetAcqDetailsDTO.setCountSuppliedByIp(countSuppliedByIpDTO);
			}
			for(FileDetails fileDetails:tallySheetAcqDetails.getTallysheetFileDetails().getFileDetails()) {
				tallySheetAcqDetailsDTO.getFileDetails().add(fileDetailsBeanToDTO(fileDetails));
			}
			

		}

		return tallySheetAcqDetailsDTO;

	}
	public static FileDetailsDTO fileDetailsBeanToDTO(FileDetails fileDetail){
		FileDetailsDTO fileDetailsDTO=null;
		if(fileDetail!=null) {
				 fileDetailsDTO=new FileDetailsDTO();
				fileDetailsDTO.setName(fileDetail.getName());
				fileDetailsDTO.setSize(stringToInt(fileDetail.getSize()));
				fileDetailsDTO.setFileRecCount(stringToInt(fileDetail.getFileRecCount()));
			
		}
		
		return fileDetailsDTO;
	}
	
	public static int stringToInt(String s) {
		int value=0;
		if(StringUtils.isNotEmpty(s)) {
			try {
				value=Integer.parseInt(s);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			
		}
		return value;
	}
}

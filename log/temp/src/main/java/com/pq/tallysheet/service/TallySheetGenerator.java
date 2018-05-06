package com.pq.tallysheet.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pq.bean.RecordCount;
import com.pq.bean.tallysheet.CountGenByAcq;
import com.pq.bean.tallysheet.CountSuppliedByIp;
import com.pq.bean.tallysheet.FileDetails;
import com.pq.bean.tallysheet.TallySheetAcqDetails;
import com.pq.bean.tallysheet.TallySheetNormDetails;
import com.pq.bean.tallysheet.TallySheetXmlConfig;
import com.pq.bean.tallysheet.TallysheetFileDetails;
import com.pq.constant.SSConstants;
import com.pq.dto.TallySheetAcqDetailsDTO;
import com.pq.dto.TallySheetDTO;
import com.pq.dto.TallysheetInfo;
import com.pq.transformer.TallysheetTransformer;
import com.pq.util.AccessS3Util;
import com.pq.util.CommonUtil;


public class TallySheetGenerator {
	private static TallysheetInfo tallysheetInfo=new TallysheetInfo();
	private static String jobName;

	public static void generateTallysheet(String dbDir,String tallysheetDir,String ucSubmitDir,String mode,String apiKey,String updateUrl,String jName) throws  IOException {
		mergeAllTallysheetInfo(dbDir,mode);
		
		jobName=jName;
		TallySheetXmlConfig tallySheetXmlConfig = new TallySheetXmlConfig();
		tallySheetXmlConfig.setVersion(SSConstants.TALLYSHEET_VERSION);
		
		TallySheetAcqDetails tallySheetAcqDetails = populateACQDetails();
	    tallySheetXmlConfig.setTallySheetAcqDetails(tallySheetAcqDetails);
	     
	     
		TallySheetNormDetails tallySheetNormDetails = populateNormDetails();
		tallySheetXmlConfig.setTallySheetNormDetails(tallySheetNormDetails);
	     
		
		
		OutputStream fsOutputStreamTallysheet=null;
		if(mode.equals("LOCAL")) {
		File file = new File(tallysheetDir);
		if(!file.exists()) {
			file.mkdirs();
		}
		 fsOutputStreamTallysheet=new FileOutputStream(new File(tallysheetDir + "tallysheet.ts.xml"));
		}else {
			Path tallysheetPath = new Path(tallysheetDir + "tallysheet.ts.xml");
			FileSystem fs = FileSystem.get(URI.create(tallysheetPath.toString()), new Configuration());
			 fsOutputStreamTallysheet = fs.create(tallysheetPath);
		}
		
		
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(TallySheetXmlConfig.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(tallySheetXmlConfig, fsOutputStreamTallysheet);

		} catch (JAXBException e) {
			e.printStackTrace();
		} finally {
			
			fsOutputStreamTallysheet.close();
		}
//		
//		if(mode.equals("LOCAL")) {
//			
//		}else {
			  Path ucSubmitTallysheetPath = new Path(ucSubmitDir + "tallysheet.ts.xml");
	             Path tallysheetPath=new Path(tallysheetDir + "tallysheet.ts.xml");
	             FileSystem fs = FileSystem.get(URI.create(ucSubmitDir + "tallysheet.ts.xml"), new Configuration());
	             boolean status = FileUtil.copy(fs, tallysheetPath, fs, ucSubmitTallysheetPath, false, false, new Configuration());
//		}
		
		////////////////////////
		TallySheetDTO tallysheetDTO = TallysheetTransformer.beanToDTO(tallySheetXmlConfig);
		tallysheetDTO.setLastUpdateDate(tallysheetInfo.getSysDateTime());
		CloseableHttpClient client = HttpClients.createDefault();
	    HttpPost httpPost = new HttpPost(updateUrl);
//	    String json = "{\"CONFIG_NAME\":\"Test-IP\",\"BATCHID\":\"TESTINDIA\"}";
	     String json = new ObjectMapper().writeValueAsString(tallysheetDTO);
	    StringEntity entity = new StringEntity(json);
	    System.out.println(json);
	    httpPost.setEntity(entity);
	    httpPost.setHeader("X-API-KEY", apiKey);
	    httpPost.setHeader("Content-type", "application/json");
	    CloseableHttpResponse response = client.execute(httpPost);
		

		// UC Submit
/*		try {
			Path ucSubmitPath = new Path(
					conf.get("ucsubmit.loc") + tallysheetFileName.toString() );
			boolean status = FileUtil.copy(fs, tallysheetPath, fs, ucSubmitPath, false, false, conf);
			System.out.println("Tallysheet Copy status: " + status);
		} catch (Exception ex) {
			ex.printStackTrace();
		}*/
		


	}

	private static TallySheetNormDetails populateNormDetails() {
		TallySheetNormDetails tallySheetNormDetails = new TallySheetNormDetails();
	
		 int ERR_N = 0;
		List<Integer> errCount = new ArrayList<Integer>();
		// NORM Count
		RecordCount normRecordCountSummary = tallysheetInfo.fetchTotalNormRecords();
		tallySheetNormDetails.setAdd(String.valueOf(normRecordCountSummary.getAdd()));
		tallySheetNormDetails.setAud(String.valueOf(normRecordCountSummary.getAud()));
		tallySheetNormDetails.setAup(String.valueOf(normRecordCountSummary.getAup()));
		tallySheetNormDetails.setDel(String.valueOf(normRecordCountSummary.getDel()));
		tallySheetNormDetails.setUpd(String.valueOf(normRecordCountSummary.getUpd()));
		tallySheetNormDetails.setErr(String.valueOf(errCount.size()));
		
		TallysheetFileDetails tallysheetFileDetailforNorm = new TallysheetFileDetails();
		for(Map.Entry<String, RecordCount> entry:tallysheetInfo.getNormRecordCounter().entrySet()) {
			FileDetails fileDetailsForNorm = new FileDetails();
			fileDetailsForNorm.setName(entry.getKey());
			fileDetailsForNorm.setSize(entry.getValue().getSize()+"");
			fileDetailsForNorm.setFileRecCount(entry.getValue().getTotal() + "");
			tallysheetFileDetailforNorm.getFileDetails().add(fileDetailsForNorm);
		}
		// NORM Job Name
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh-mm-ss");
		String normDateTime = df.format(new Date());
		tallySheetNormDetails.setJobName(tallysheetInfo.getIpName()  + "_NORM_" + normDateTime);
		// NORM Date Time
		tallySheetNormDetails.setNormDate(normDateTime);
		// NORM File List
		tallySheetNormDetails.setTallysheetFileDetails(tallysheetFileDetailforNorm);
		// NORM SET NAME
		tallySheetNormDetails.setSetName(SSConstants.TALLYSHEET_NORM_SET_NAME);
		// NORM Total
//		int normTotalCount = normRecordCountSummary.getAdd() + normRecordCountSummary.getAud() + normRecordCountSummary.getAup() + normRecordCountSummary.getDel();
		tallySheetNormDetails.setTot(String.valueOf(normRecordCountSummary.getTotal()));
		// NORM UPD
		
		return tallySheetNormDetails;
	}

	private static TallySheetAcqDetails populateACQDetails() {
//		int AUP_R=0;
//		int DEL_R=0;
//		for (Map.Entry<String, RecordCount> rec : tallysheetInfo.getAcqRecordCounter().entrySet()) {
//			AUP_R += rec.getValue().getAup();
//			DEL_R += rec.getValue().getDel();
//		}
		RecordCount acqRecordCountSummary = tallysheetInfo.fetchTotalAcqRecords();
		
		TallySheetAcqDetails tallySheetAcqDetails = new TallySheetAcqDetails();

//		tallySheetAcqDetails.setAcqJob(
//				tallysheetInfo.getIpName() + "_ACQ_" + tallysheetInfo.getAcqDateTime());
	
		tallySheetAcqDetails.setAcqJob(jobName);
		
		tallySheetAcqDetails.setConfigName(tallysheetInfo.getIpName());
		TallysheetFileDetails tallysheetFileDetails = new TallysheetFileDetails();
		for (Map.Entry<String, RecordCount> rec : tallysheetInfo.getAcqRecordCounter().entrySet()) {
			FileDetails fileDetails = new FileDetails();
			fileDetails.setName(rec.getKey());
			fileDetails.setSize(rec.getValue().getSize()+"");
			fileDetails.setFileRecCount(rec.getValue().getTotal() + "");
			tallysheetFileDetails.getFileDetails().add(fileDetails);
		}
		tallySheetAcqDetails.setTallysheetFileDetails(tallysheetFileDetails);

		tallySheetAcqDetails.setAcqDate(tallysheetInfo.getAcqDateTime());
		tallySheetAcqDetails.setVendorDate(tallysheetInfo.getAcqDateTime());
		// ACQ Count
		CountGenByAcq countGenByAcq = new CountGenByAcq();
		countGenByAcq.setAup(String.valueOf(acqRecordCountSummary.getAup()));
		countGenByAcq.setAdd(String.valueOf(acqRecordCountSummary.getAdd()));
		countGenByAcq.setAud(String.valueOf(acqRecordCountSummary.getAud()));
		countGenByAcq.setDel(String.valueOf(acqRecordCountSummary.getDel()));
		countGenByAcq.setUpd(String.valueOf(acqRecordCountSummary.getUpd()));
		countGenByAcq.setFiles(tallysheetInfo.getAcqRecordCounter().size()+"");
		/*int tot = this.acqTallysheetComponent.getADD_R() + this.acqTallysheetComponent.getAUD_R()
				+ this.acqTallysheetComponent.getAUP_R() + this.acqTallysheetComponent.getDEL_R()
				+ this.acqTallysheetComponent.getUPD_acqRecordCountSummary.getAup()R();*/
		countGenByAcq.setTot((String.valueOf(acqRecordCountSummary.getTotal())));
		tallySheetAcqDetails.setCountGenByAcq(countGenByAcq);
		// ACQ Count supplied by IP
		CountSuppliedByIp countSuppliedByIp = new CountSuppliedByIp();
		TallysheetFileDetails tallysheetFileDetails2 = new TallysheetFileDetails();
		FileDetails fileDetails2 = new FileDetails();
		fileDetails2.setName(SSConstants.IP_SUPPLIED_FILE_NAME_DEFAULT);
		fileDetails2.setSize(SSConstants.IP_SUPPLIED_FILE_SIZE_DEFAULT);
		fileDetails2.setFileRecCount(SSConstants.IP_SUPPLIED_FILE_RECORD_COUNT_DEFAULT);
		tallysheetFileDetails2.getFileDetails().add(fileDetails2);
		countSuppliedByIp.setTallysheetFileDetails(tallysheetFileDetails2);
		tallySheetAcqDetails.setCountSuppliedByIp(countSuppliedByIp);
		return tallySheetAcqDetails;
	}

	private static void mergeAllTallysheetInfo(String dir,String mode)
			throws IOException, JsonParseException, JsonMappingException, FileNotFoundException {
		ObjectMapper mapper=new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		if(mode.equals("LOCAL")) {
		File fileDir = new File(dir);
		if (!fileDir.exists()) {
			new RuntimeException("cannot generate tallysheet as input location :" + dir + " does not exist");
		}
		if (!fileDir.isDirectory()) {
			new RuntimeException("cannot generate tallysheet as input location :" + dir + " is not a directory");
		}
		for (File file : fileDir.listFiles()) {
			TallysheetInfo tallysheet = mapper.readValue(new FileReader(file), TallysheetInfo.class);
			tallysheetInfo.merge(tallysheet);
		}
		}
		else {
		FileSystem fs = FileSystem.get(URI.create(dir), new Configuration());
		RemoteIterator<LocatedFileStatus> files = fs.listFiles(new Path(dir), true);
		while (files.hasNext()) {
			LocatedFileStatus next = files.next();
			String string = next.getPath().toString();
			TallysheetInfo tallysheet = mapper.readValue(new AccessS3Util().readFromS3(string), TallysheetInfo.class);
			tallysheetInfo.merge(tallysheet);
		}
		}
	}

}

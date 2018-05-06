package com.pq.reducer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.pq.bean.NormFileDetail;
import com.pq.bean.RecordCount;
import com.pq.bean.RecordType;
import com.pq.bean.ResponseToReducer;
import com.pq.bean.recordtype.RecordTypeConfig;
import com.pq.bean.recordtype.RecordTypeResponse;
import com.pq.service.RecordTypeConfigService;
import com.pq.tallysheet.service.ITallysheetSvc;
import com.pq.tallysheet.service.LocalTallysheetSvcImpl;
import com.pq.util.AccessS3Util;
import com.pq.util.CommonUtil;
import com.pq.util.XmlValidationUtil;

 public class GenericProcessReducer extends Reducer<Text, Text, Text, Text> {
	private static final String FILENAME_FIELD = "Filename";

	private Logger log = Logger.getLogger(GenericProcessReducer.class);

	protected Configuration conf;
	private ResponseToReducer responseToReducer;
	private ObjectMapper objectMapper = null;
	private Map<String, RecordCount> acqRecordCounter;
	private Map<String, RecordCount> normRecordCounter;
	private String processedNormFile;
//	private String  recordType;

	private ITallysheetSvc tallysheetSvc;
	String sequenceNumber = null;
	FileSystem normFileFS = null;

	private Map<String,NormFileDetail> normFileMap = null;
	RecordTypeConfig recordTypeConfig=null;
	RecordTypeResponse recordTypeResponse=null;

	@Override
	public void setup(Context context) throws java.io.IOException, java.lang.InterruptedException {
		log.info("Inside setup() of ProcessReducer");
		conf = context.getConfiguration();
		conf.setBoolean("dfs.support.append", true);
		objectMapper = new ObjectMapper();
		acqRecordCounter = new HashMap<String, RecordCount>();
		normRecordCounter = new HashMap<String, RecordCount>();
		tallysheetSvc = new LocalTallysheetSvcImpl(conf.get("tallysheet.temp.location"));
		normFileMap=new HashMap<>();
		recordTypeConfig = RecordTypeConfigService.constructRecordTypeConfig2(conf.get("recordtype.config"));
		recordTypeResponse=new RecordTypeResponse();
	}

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws java.io.IOException, java.lang.InterruptedException {
		String res = "";
		context.write(key, new Text(res));
//		String recordType = key.toString().split("\\|")[0];
		String accessionId = key.toString().split("\\|")[1];
		sequenceNumber = key.toString().split("\\|")[2];

		for (Text value : values) {
			res += value;
		}

		responseToReducer = objectMapper.readValue(res, ResponseToReducer.class);
		
//		recordType=detectRecordType(responseToReducer.getRecord());
		if(!StringUtils.equals(FILENAME_FIELD, recordTypeResponse.getRecordDetectionType())) {
			//if record  detection type is filename no need to check record type again.
			recordTypeResponse=RecordTypeConfigService.detectRecordType(recordTypeConfig, responseToReducer.getCfInfo().getOrgZipFile(), responseToReducer.getRecord(), recordTypeResponse.getRecordDetectionType());
		}

		acqRecordCounter.putIfAbsent(responseToReducer.getCfInfo().getSourceFileName(), new RecordCount());
		updateTallysheetCounts(recordTypeResponse.getRecordType(),acqRecordCounter,responseToReducer.getCfInfo().getSourceFileName());

		try {
			if (normFileMap.get(recordTypeResponse.getRecordType())==null) {

				StringBuilder normFileBuild = generateNormFileName(conf.get("config.name").split("-")[0],responseToReducer,recordTypeResponse.getRecordType());
				String normFilePath = conf.get("norm.loc") + normFileBuild.toString();
				FSDataOutputStream outputStream = null;
				// Path normFile = new Path(conf.get("norm.loc") + normFileBuild.toString());
				normFileFS = FileSystem.get(URI.create(normFilePath), conf);
				// if (!normFileFS.exists(normFile)) {
				// processedNormFiles.add(normFileBuild.toString());
				// contentBuilder = new StringBuilder();
				outputStream = normFileFS.create(new Path(normFilePath));
				outputStream.write(responseToReducer.getCfInfo().getFileHead().getBytes());
				outputStream.write("\n".getBytes());

				NormFileDetail normFileDetail = new NormFileDetail();
				normFileDetail.setFileName(normFilePath);
				normFileDetail.setOutputStream(outputStream);
				normFileMap.put(recordTypeResponse.getRecordType(), normFileDetail);
				// }
			}

			String record = responseToReducer.getRecord();
			// log.info("Inside reduce() of ProcessReducer");

			normFileMap.get(recordTypeResponse.getRecordType()).getOutputStream().write(record.getBytes());

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public void updateTallysheetCounts(String recordType,Map<String, RecordCount> recordCounter,String filename) {
		if (recordType.equals("ADD")) {
			recordCounter.get(filename).addIncrement();
		} else if (recordType.equals("UPD")) {
			recordCounter.get(filename).updIncrement();;
		}else if (recordType.equals("DEL")) {
			recordCounter.get(filename).delIncrement();
		}else if (recordType.equals("AUP")) {
			recordCounter.get(filename).aupIncrement();;
		}else if (recordType.equals("AUD")) {
			recordCounter.get(filename).audIncrement();;
		}
	}

//	public abstract  String detectRecordType(String record);
	
	 public StringBuilder generateNormFileName(String dbName,ResponseToReducer responseToReducer,String recordType) {
		StringBuilder normFileBuild = new StringBuilder();
		normFileBuild.append(dbName.split("-")[0]);
		normFileBuild.append("-");
		normFileBuild.append(responseToReducer.getSequenceNumber());
		normFileBuild.append("."+recordType+".");
		normFileBuild.append(responseToReducer.getSysDateTime());
//		normFileBuild.append(responseToReducer.getCurrentFileModDateTime());
		normFileBuild.append("Z.ACQ.");
		normFileBuild.append(responseToReducer.getCurrentFileModDateTime());
		return normFileBuild;
		
//		StringBuilder normFileBuild = new StringBuilder();
//		normFileBuild.append(dbName);
//		normFileBuild.append("-");
//		normFileBuild.append(responseToReducer.getSequenceNumber());
//		normFileBuild.append("."+recordType+".");
//		normFileBuild.append(responseToReducer.getSysDateTime());
//		normFileBuild.append("Z.ACQ.");
//		normFileBuild.append(responseToReducer.getCurrentFileModDateTime());
	}

	@Override
	public void cleanup(Context context) throws IOException, InterruptedException {
		// add file tailer to all norm files
		for (NormFileDetail normFileDetail : normFileMap.values()) {
		normFileDetail.getOutputStream().write(responseToReducer.getCfInfo().getFileTail().getBytes());
		normFileDetail.getOutputStream().close();
		}

		// validate all norm files.In case of error we are appending ".ERR"
		for (NormFileDetail normFileDetail : normFileMap.values()) {
			String filename = normFileDetail.getFileName();
			Path normFilePath = new Path(filename);
			FileSystem fs = FileSystem.get(URI.create(normFilePath.toString()), conf);
			FSDataInputStream fsInputStream = fs.open(normFilePath);
			boolean normValid = XmlValidationUtil.validateXmlWithDtd(fs.open(normFilePath));
			fsInputStream.close();
			if (!normValid) {
				fs.rename(new Path(filename), new Path(filename + ".ERR"));
				normFileDetail.setFileName(filename + ".ERR");
			}
			
			//move norm file to uc submit
			String normFile = normFileDetail.getFileName();
             Path ucSubmitPath = new Path(conf.get("ucsubmit.loc") + normFile.substring(normFile.lastIndexOf("/") + 1, normFile.length()));
             Path newNormPath=new Path(normFile);
             boolean status = FileUtil.copy(fs, newNormPath, fs, ucSubmitPath, false, false, conf);
             
		}
		// getting tallysheet counts for norm files
		for (Map.Entry<String,NormFileDetail> normFileDetailEntry : normFileMap.entrySet()) {
			NormFileDetail normFileDetail =normFileDetailEntry.getValue();
			String fullFileName = normFileDetail.getFileName();
			String fileName = fullFileName.substring(fullFileName.lastIndexOf("/") + 1);
			normRecordCounter.putIfAbsent(fileName, new RecordCount());
			 Path normFilePath = new Path(fullFileName);
			 FileSystem fs = FileSystem.get(URI.create(fullFileName), conf);
			FileStatus[] fileStatus = fs.listStatus(normFilePath);
			long fileLength = fileStatus[0].getLen();
			normRecordCounter.get(fileName).setSize(String.valueOf(fileLength));

			 FSDataInputStream fsInputStream = fs.open(normFilePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fsInputStream));

			try {
				String line;
				while ((line = br.readLine()) != null) {
					// story record count
					if (CommonUtil.checkMatch(line, conf.get("story.start.tag"), true) ) {
//						normRecordCounter.get(fileName).aupIncrement();
						updateTallysheetCounts(normFileDetailEntry.getKey(),normRecordCounter, fileName);
					}
				}
			} finally {
				br.close();
			}
			tallysheetSvc.populateCounts(conf.get("job.name"), sequenceNumber, acqRecordCounter, normRecordCounter,
					conf.get("mode"));
	}
	}

}

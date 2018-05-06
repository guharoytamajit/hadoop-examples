package com.pq.mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.log4j.Logger;

import com.pq.bean.CfInfo;
import com.pq.bean.ResponseToReducer;
import com.pq.util.CommonUtil;
import com.pq.util.XmlValidationUtil;

public class MedlineProcessMapper extends Mapper<Text, Text, Text, Text> {
	private Logger log = Logger.getLogger(MedlineProcessMapper.class);
	private Configuration conf;
	private String fileName = null;
	private DateFormat df = null;
	private String fileTail;
	private String startStory;
	private String endStory;
	private String delStartStory;
	private String delStopStory;
	private String recordPattern;
	private long recordSequence = 0l;
	private String orgZipFileName;
	private String fileModTime ;
	private String sysDateTime ;
	private String sequenceNumber;
	private String acqDateTime;
	// private String size;
	////////////////////////
	
	public static long PROCESS_MAPPING=0l;
	
	/////////////////////

	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		log.info("Inside setup() of ProcessMapper");

		conf = context.getConfiguration();
		df = new SimpleDateFormat("yyyy-MM-dd'T'hh-mm-ss");

		fileTail = conf.get("root.end.tag");
		startStory = conf.get("story.start.tag");
		endStory = conf.get("story.end.tag");
		delStartStory = conf.get("delete.start.tag");
		delStopStory = conf.get("delete.end.tag");
		recordPattern = conf.get("accession.xpath");
	}

	@Override
	public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
//		log.info("Inside map() of ProcessMapper");
//		log.info("key: " + key + ", value: " + value);
		long PROCESS_MAPPING_TIME=System.nanoTime();
		String options[] = value.toString().split("\\|");
		String file = options[0];
		orgZipFileName = options[1];
		sysDateTime = options[2];
		acqDateTime = options[3];

		FileSystem fs = FileSystem.get(URI.create(file), conf);
		Path path = new Path(file);
		FileStatus[] fileStatus = fs.listStatus(path);
		long modTime = fileStatus[0].getModificationTime();

		 fileModTime = df.format(new Date(modTime));
		fileName = file.substring(file.lastIndexOf("/") + 1, file.length());
		sequenceNumber=CommonUtil.extractSequenceNumber(fileName,conf.get("db.name"));
		
		FSDataInputStream fsInputStream = fs.open(path);
		processRecord(context, fsInputStream, startStory, endStory, delStartStory, delStopStory);
		PROCESS_MAPPING=PROCESS_MAPPING+(System.nanoTime()-PROCESS_MAPPING_TIME);

	/*	Path tempOutputPath = new Path(conf.get("norm.loc") + fileName+".tmp");
		FSDataOutputStream fsOutputStream = fs.create(tempOutputPath);

		try {
			boolean xmlValid = XmlValidationUtil.validateXmlWithDtd(fs.open(new Path(file)));
			log.info("***** XML validation status: " + xmlValid);

			// opening the file again
			fsInputStream = fs.open(path);
			byte[] b = new byte[1024];
			int numBytes = 0;
			while ((numBytes = fsInputStream.read(b)) > 0) {
				fsOutputStream.write(b, 0, numBytes);
			}

			if (!xmlValid) {
				boolean renameStatus = fs.rename(new Path(conf.get("norm.loc") + fileName+".tmp"),
						new Path(conf.get("norm.loc") + fileName+ "_" + acqDateTime + ".ERR"));
				log.info("***** File rename status: " + renameStatus);
			}
		} finally {
			fsInputStream.close();
			fsOutputStream.close();
		}
*/
	}

	private void processRecord(Context context, FSDataInputStream fsInputStream, String startStory, String endStory,
			String startDelete, String endDelete) throws InterruptedException {
		BufferedReader br = new BufferedReader(new InputStreamReader(fsInputStream));
		StringBuilder headLine = new StringBuilder();
		StringBuilder recordContent = new StringBuilder();
		StringBuilder deleteRecordContent= new StringBuilder();
		String line = "";
		headLine = new StringBuilder();
		boolean headRead = true;
		int readCount = 0;
		boolean deleteRead = false;
		int delCount = 0;
		try {
			while ((line = br.readLine()) != null) {
				// read the header
				if (headRead && !CommonUtil.checkMatch(line, startStory, true)) {
					headLine.append(line);
					headLine.append("\n");
				} else {
					headRead = false;
					if (!deleteRead) {

						if (readCount == 1 && !CommonUtil.checkMatch(line, startStory, true)
								&& !CommonUtil.checkMatch(line, endStory, true)
								&& !CommonUtil.checkMatch(line, startDelete, true)) {
							recordContent.append(line);
							recordContent.append("\n");
						} else if (CommonUtil.checkMatch(line, endStory, true)
								&& !CommonUtil.checkMatch(line, startDelete, true)) {
							recordContent.append(line);
							recordContent.append("\n");
							processStory(context, "", headLine, recordContent.toString());
							readCount = 0;
							recordContent = new StringBuilder();
						} else if (CommonUtil.checkMatch(line, startDelete, true)) {
							deleteRecordContent.append(line);
							deleteRecordContent.append("\n");
							deleteRead = true;
							delCount = 1;
						} else {
							recordContent.append(line);
							recordContent.append("\n");
							readCount++;
						}
					}

					// if (deleteRead) {
					else {
						// System.out.println("delete: "+line);
						
						if (delCount == 1 && !CommonUtil.checkMatch(line, endDelete, true)) {
							// System.out.println("delete: " + line);
							deleteRecordContent.append(line);
							deleteRecordContent.append("\n");
						//	processDelete(context, headLine, deleteRecordContent.toString());
						} else if (CommonUtil.checkMatch(line, endDelete, true)) {
							// System.out.println("delete: " + line);
							delCount = 0;
							deleteRecordContent.append(line);
							deleteRecordContent.append("\n");
							processDelete(context, headLine, deleteRecordContent.toString());
						}
					}
				}

			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("File Head: " + headLine.toString());

	}

	private void processStory(Context context, String recordTail, StringBuilder fileHead, String str)
			throws IOException, InterruptedException {
		CfInfo cfInfo = new CfInfo();
		ResponseToReducer responseToReducer = new ResponseToReducer();
		Pattern r = Pattern.compile(recordPattern, Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(str);
		if (m.find()) {
			String accessionNumber = m.group(1);
			cfInfo.setFileHead(fileHead.toString());
			cfInfo.setFileTail(fileTail);
			cfInfo.setRecordHead("");
			cfInfo.setRecordTail(recordTail);
			cfInfo.setSourceFileName(fileName);
			cfInfo.setOrgZipFile(orgZipFileName);
			responseToReducer.setCfInfo(cfInfo);
			responseToReducer.setRecord(str);
			responseToReducer.setCurrentFileModDateTime(fileModTime);
			responseToReducer.setSysDateTime(sysDateTime);
			responseToReducer.setSequenceNumber(sequenceNumber);

			context.write(new Text("A|" + String.format("%09d", ++recordSequence) + "|" + accessionNumber+"|"+sequenceNumber),
					new Text(CommonUtil.convertToJson(responseToReducer)));
		}
	}

	private void processDelete(Context context, StringBuilder fileHead, String delRecord)
			throws IOException, InterruptedException {
		CfInfo cfInfo = new CfInfo();
		ResponseToReducer responseToReducer = new ResponseToReducer();
		Pattern r = Pattern.compile(recordPattern, Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(delRecord);
		if (m.find()) {
			String accessionNumber = "0";
			cfInfo.setFileHead(fileHead.toString());
			cfInfo.setFileTail(fileTail);
			cfInfo.setRecordHead(delStartStory);
			cfInfo.setRecordTail(delStopStory);
			cfInfo.setSourceFileName(fileName);
			cfInfo.setOrgZipFile(orgZipFileName);
			responseToReducer.setCfInfo(cfInfo);
			responseToReducer.setRecord(delRecord);
			responseToReducer.setCurrentFileModDateTime(fileModTime);
			responseToReducer.setSysDateTime(sysDateTime);
			responseToReducer.setSequenceNumber(sequenceNumber);
			context.write(
					new Text("D|" + String.format("%09d", ++recordSequence) + "|" + accessionNumber+"|"+sequenceNumber),
					new Text(CommonUtil.convertToJson(responseToReducer)));
		}
	}

	@Override
	protected void cleanup(Mapper<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		System.out.println("###### Process mapping time: "+PROCESS_MAPPING);
	}
	

}

package com.pq.mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.pq.bean.CfInfo;
import com.pq.bean.ResponseToReducer;
import com.pq.util.CommonUtil;

public class GenericProcessMapper extends Mapper<Text, Text, Text, Text> {
	private Logger log = Logger.getLogger(MedlineProcessMapper.class);
	private Configuration conf;
	private String fileName = null;
	private DateFormat df = null;
	private String fileTail;
	private String startStory;
	private String endStory;
//	private String delStartStory;
//	private String delStopStory;
	private String accessionXpath;
	private long recordSequence = 0l;
	private String orgZipFileName;
	private String fileModTime;
	private String sysDateTime;
	private String sequenceNumber;
	private String acqDateTime;
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		log.info("Inside setup() of ProcessMapper");

		conf = context.getConfiguration();
		df = new SimpleDateFormat("yyyy-MM-dd'T'hh-mm-ss");

		fileTail = conf.get("root.end.tag");
		startStory = conf.get("story.start.tag");
		endStory = conf.get("story.end.tag");
//		delStartStory = conf.get("delete.start.tag");
//		delStopStory = conf.get("delete.end.tag");
		accessionXpath = conf.get("accession.xpath");
	}

	public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
		// log.info("Inside map() of ProcessMapper");
		// log.info("key: " + key + ", value: " + value);
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
		sequenceNumber = CommonUtil.extractSequenceNumber(orgZipFileName,conf.get("db.name"));

		FSDataInputStream fsInputStream = fs.open(path);
		processRecord(context, fsInputStream, startStory, endStory);

	}

	private void processRecord(Context context, FSDataInputStream fsInputStream, String startStory, String endStory) throws InterruptedException {
		BufferedReader br = new BufferedReader(new InputStreamReader(fsInputStream));
		StringBuilder headLine = new StringBuilder();
		StringBuilder recordContent = new StringBuilder();
		String line = "";
		headLine = new StringBuilder();
		String header=null;
		boolean headRead = true;
		int readCount = 0;
		try {
			while ((line = br.readLine()) != null) {
				// read the header
				if (headRead && !line.startsWith(startStory)) {
					headLine.append(line);
					headLine.append("\n");
				} else {
					if(headRead) {
						String xmlValidationConfig = conf.get("xml.validation");
						header=headLine.toString();
						String[] split=null;
						if(xmlValidationConfig!=null) {
							split = xmlValidationConfig.split(",");	
						}
						if(split!=null&&split.length>1&&split[0].equals("dtd")) {
							header=header.replaceFirst("(?<=<!DOCTYPE).*?(?=>)", split[1]);
						}
					}
					headRead = false;

					if (readCount == 1 && !line.startsWith(startStory)
							&& !line.startsWith(endStory)) {
						recordContent.append(line);
						recordContent.append("\n");
					} else if (line.startsWith(endStory)
							) {
						recordContent.append(line);
						recordContent.append("\n");
						processStory(context, "", header, recordContent.toString());
						readCount = 0;
						recordContent = new StringBuilder();
					} else {
						recordContent.append(line);
						recordContent.append("\n");
						readCount++;
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
		System.out.println("File Head: " + header);

	}

	private void processStory(Context context, String recordTail, String fileHead, String str)
			throws IOException, InterruptedException {
		String accessionNumber = extractAccessionNumber( str, accessionXpath);
		CfInfo cfInfo = new CfInfo();
		cfInfo.setFileHead(fileHead);
		cfInfo.setFileTail(fileTail);
//		cfInfo.setRecordHead("");
//		cfInfo.setRecordTail(recordTail);
		cfInfo.setSourceFileName(fileName);
		cfInfo.setOrgZipFile(orgZipFileName);
		ResponseToReducer responseToReducer = new ResponseToReducer();
		responseToReducer.setCfInfo(cfInfo);
		responseToReducer.setRecord(str);
		responseToReducer.setCurrentFileModDateTime(fileModTime);
		responseToReducer.setSysDateTime(sysDateTime);
		responseToReducer.setSequenceNumber(sequenceNumber);
		
		context.write(new Text( String.format("%09d", ++recordSequence) + "|" + accessionNumber+"|"+sequenceNumber),
				new Text(CommonUtil.convertToJson(responseToReducer)));
/*		Pattern r = Pattern.compile(recordPattern, Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(str);
		if (m.find()) {
			String accessionNumber = m.group(1);
			cfInfo.setFileHead(fileHead.toString());
			cfInfo.setFileTail(fileTail);
			cfInfo.setRecordHead("");
			cfInfo.setRecordTail(recordTail);
			cfInfo.setSourceFileName(fileName);
			responseToReducer.setCfInfo(cfInfo);
			responseToReducer.setRecord(str);
			responseToReducer.setCurrentFileModDateTime(fileModTime);
			responseToReducer.setSysDateTime(sysDateTime);
			responseToReducer.setSequenceNumber(sequenceNumber);

			context.write(new Text( String.format("%09d", ++recordSequence) + "|" + accessionNumber+"|"+sequenceNumber),
					new Text(CommonUtil.convertToJson(responseToReducer)));
		}*/
		
	}
	public static String  extractAccessionNumber(String xml,String xpathStr) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Document doc;
		String accNum=null; 
		try {
			doc = factory.newDocumentBuilder()
						.parse(new InputSource(new StringReader(xml)));
			XPath xpath = XPathFactory.newInstance().newXPath();
			 accNum = (String)xpath.evaluate(xpathStr, doc, XPathConstants.STRING);
		} catch (Exception e) {
			e.printStackTrace();
		} 
			return accNum;
		
	}
}

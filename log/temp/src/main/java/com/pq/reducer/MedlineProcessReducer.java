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
import com.pq.tallysheet.service.ITallysheetSvc;
import com.pq.tallysheet.service.LocalTallysheetSvcImpl;
import com.pq.util.AccessS3Util;
import com.pq.util.CommonUtil;
import com.pq.util.XmlValidationUtil;

public class MedlineProcessReducer extends Reducer<Text, Text, Text, Text> {
	private Logger log = Logger.getLogger(MedlineProcessReducer.class);

	private Configuration conf;
	private ResponseToReducer responseToReducer;
	private ObjectMapper objectMapper = null;
	private Map<String, RecordCount> acqRecordCounter;
	private Map<String, RecordCount> normRecordCounter;
	private Map<String, Integer> accessionNumberMap;
	private List<String> processedNormFiles;
	private int occurance;

	private ITallysheetSvc tallysheetSvc;
	String sequenceNumber = null;
	FileSystem normFileFS = null;

	private List<NormFileDetail> normFileList = null;

	@Override
	public void setup(Context context) throws java.io.IOException, java.lang.InterruptedException {
		log.info("Inside setup() of ProcessReducer");
		conf = context.getConfiguration();
		conf.setBoolean("dfs.support.append", true);
		objectMapper = new ObjectMapper();
		acqRecordCounter = new HashMap<String, RecordCount>();
		normRecordCounter = new HashMap<String, RecordCount>();
		processedNormFiles = new ArrayList<String>();
		accessionNumberMap = new HashMap<String, Integer>();
		tallysheetSvc = new LocalTallysheetSvcImpl(conf.get("tallysheet.temp.location"));
		normFileList = new ArrayList<NormFileDetail>();
	}

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws java.io.IOException, java.lang.InterruptedException {
		String res = "";
		context.write(key, new Text(res));
		String recordType = key.toString().split("\\|")[0];
		String accessionId = key.toString().split("\\|")[2];
		sequenceNumber = key.toString().split("\\|")[3];

		for (Text value : values) {
			res += value;
		}

		responseToReducer = objectMapper.readValue(res, ResponseToReducer.class);

		acqRecordCounter.putIfAbsent(responseToReducer.getCfInfo().getSourceFileName(), new RecordCount());
		// acqRecordCounter.get(responseToReducer.getCfInfo().getSourceFileName()).setSize(responseToReducer.getCfInfo().get);
		if (RecordType.fromString(recordType) == RecordType.STORY) {
			if (accessionNumberMap.get(accessionId) != null) {

				occurance = accessionNumberMap.get(accessionId) + 1;
				accessionNumberMap.put(accessionId, occurance);
			} else {
				accessionNumberMap.put(accessionId, 1);
				occurance = 1;
			}
			acqRecordCounter.get(responseToReducer.getCfInfo().getSourceFileName()).aupIncrement();

		} else if (RecordType.fromString(recordType) == RecordType.DELETE) {
			// TODO:For MEDLINE delete record os 0 or 1,which may not be true for other ips.
			occurance = 1;

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			Document doc = null;
			try {
				doc = factory.newDocumentBuilder()
						.parse(new InputSource(new StringReader(responseToReducer.getRecord())));
				XPath xpath = XPathFactory.newInstance().newXPath();
				// TODO:xpath should be taken from property file
				NodeList nodes = (NodeList) xpath.evaluate("//DeleteCitation/PMID", doc, XPathConstants.NODESET);
				System.out.println(nodes.getLength());
				acqRecordCounter.get(responseToReducer.getCfInfo().getSourceFileName()).setDel(nodes.getLength());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		int index = occurance - 1;
		try {
			if (normFileList.size() < occurance) {

				StringBuilder normFileBuild = new StringBuilder();
				normFileBuild.append(conf.get("config.name").split("-")[0]);
				normFileBuild.append("-");
				normFileBuild.append(responseToReducer.getSequenceNumber());
				normFileBuild.append("-00");
				normFileBuild.append(occurance);
				normFileBuild.append(".AUP.");
				normFileBuild.append(responseToReducer.getSysDateTime());
				normFileBuild.append("Z.ACQ.");
				normFileBuild.append(responseToReducer.getCurrentFileModDateTime());
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
				normFileList.add(normFileDetail);
				// }
			}

			String record = responseToReducer.getRecord();
			// log.info("Inside reduce() of ProcessReducer");

			normFileList.get(index).getOutputStream().write(record.getBytes());

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	@Override
	public void cleanup(Context context) throws IOException, InterruptedException {
		// add file tailer to all norm files
		for (NormFileDetail normFileDetail : normFileList) {
			normFileDetail.getOutputStream().write(responseToReducer.getCfInfo().getFileTail().getBytes());
			normFileDetail.getOutputStream().close();
		}

		// validate all norm files.In case of error we are appending ".ERR"
		for (NormFileDetail normFileDetail : normFileList) {
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
		for (NormFileDetail normFileDetail : normFileList) {
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
				boolean readRecordFlag = true;
				while ((line = br.readLine()) != null) {
					// story record count
					if (CommonUtil.checkMatch(line, conf.get("story.start.tag"), true) && readRecordFlag) {
						normRecordCounter.get(fileName).aupIncrement();
					}
					// delete record count
					if (!readRecordFlag || CommonUtil.checkMatch(line, conf.get("delete.start.tag"), true)) {
						readRecordFlag = false;
						String delLine[] = line.split("\n");
						for (String del : delLine) {

							if (!readRecordFlag && CommonUtil.checkMatch(del, conf.get("accession.xpath"), true)) {
								normRecordCounter.get(fileName).delIncrement();
							}
						}
					}
				}
			} finally {
				br.close();
			}
			tallysheetSvc.populateCounts(conf.get("job.name"), sequenceNumber, acqRecordCounter, normRecordCounter,
					conf.get("mode"));
		}
	}

/*	public void cleanup2(Context context) throws java.io.IOException, java.lang.InterruptedException {
		// append filetail to all norm files

		// log.info("Can append in cleanup ?"+conf.get("dfs.support.append"));
		for (String normFile : processedNormFiles) {
			String pathString = conf.get("norm.loc") + normFile;
			if (conf.get("mode").equals("LOCAL")) {
				FileWriter fileWriter = new FileWriter(pathString, true);
				fileWriter.write(responseToReducer.getCfInfo().getFileTail());
				fileWriter.close();
			} else {
				Path normFilePath = new Path(pathString);
				FileSystem fs = FileSystem.get(URI.create(pathString), conf);
				FSDataOutputStream outputStream = fs.append(normFilePath);
				// boolean status = FileUtil.copy(fs, normFilePath, fs, new Path("/norm/test"),
				// false, false, conf);
				outputStream.write(responseToReducer.getCfInfo().getFileTail().getBytes());
				outputStream.close();
				fs.close();
				try {
					// processCommand(pathString, responseToReducer.getCfInfo().getFileTail(),
					// conf);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		XmlValidationUtil xmlValidation = new XmlValidationUtil();

		List<String> newNormFileList = new ArrayList<String>();
		for (String normFile : processedNormFiles) {
			String filename = conf.get("norm.loc") + normFile;

			if (conf.get("mode").equals("LOCAL")) {
				File file = new File(filename);
				FileInputStream fileInputStream = new FileInputStream(file);
				boolean normValid = xmlValidation.validateXmlWithDtd(fileInputStream);
				if (!normValid) {
					file.renameTo(new File(filename + ".ERR"));
					newNormFileList.add(normFile + ".ERR");
				} else {
					newNormFileList.add(normFile);
				}

			} else {
				Path normFilePath = new Path(filename);
				FileSystem fs = FileSystem.get(URI.create(normFilePath.toString()), conf);
				FSDataInputStream fsInputStream = fs.open(normFilePath);
				boolean normValid = xmlValidation.validateXmlWithDtd(fs.open(normFilePath));
				// TODO rename for cluster mode
				newNormFileList.add(normFile);
			}
		}
		processedNormFiles = newNormFileList;
		for (String normFile : processedNormFiles) {
			normRecordCounter.putIfAbsent(normFile, new RecordCount());
			BufferedReader br = null;
			String filename = conf.get("norm.loc") + normFile;

			if (conf.get("mode").equals("LOCAL")) {
				File file = new File(filename);
				normRecordCounter.get(normFile).setSize(file.length() + "");
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			} else {

				Path normFilePath = new Path(filename);
				FileSystem fs = FileSystem.get(URI.create(normFilePath.toString()), conf);
				FSDataInputStream fsInputStream = fs.open(normFilePath);
				br = new BufferedReader(new InputStreamReader(fsInputStream));

			}

			try {
				String line;
				boolean readRecordFlag = true;
				while ((line = br.readLine()) != null) {
					// story record count
					if (CommonUtil.checkMatch(line, conf.get("story.start.tag"), true) && readRecordFlag) {
						normRecordCounter.get(normFile).aupIncrement();
					}
					// delete record count
					if (!readRecordFlag || CommonUtil.checkMatch(line, conf.get("delete.start.tag"), true)) {
						readRecordFlag = false;
						String delLine[] = line.split("\n");
						for (String del : delLine) {

							if (!readRecordFlag && CommonUtil.checkMatch(del, conf.get("accession.xpath"), true)) {
								normRecordCounter.get(normFile).delIncrement();
							}
						}
					}
				}
			} finally {
				br.close();
				// fsInputStream.close();
			}
		}

		tallysheetSvc.populateCounts(conf.get("job.name"), sequenceNumber, acqRecordCounter, normRecordCounter,
				conf.get("mode"));

	}*/

	public static void backupFile(String tempFilePath, Configuration conf, FileSystem fs,
			FSDataInputStream sourceContent) throws IOException {

		FSDataOutputStream out = fs.create(new Path(tempFilePath), true);
		IOUtils.copyBytes(sourceContent, out, 4096, false);
		out.close();

	}

	private static void processCommand(String originalFilePath, String content, Configuration conf) throws Exception {
		Path path = new Path(originalFilePath);
		FileSystem fs = FileSystem.get(conf);
		FSDataInputStream in = fs.open(path);
		String tempFilePath = originalFilePath + ".tmp";

		backupFile(tempFilePath, conf, fs, in);

		in.close();

		FSDataOutputStream out = fs.create((path), true);
		FSDataInputStream backup = fs.open(new Path(tempFilePath));

		int offset = 0;
		int bufferSize = 4096;

		int result = 0;

		byte[] buffer = new byte[bufferSize];
		// pre read a part of content from input stream
		result = backup.read(offset, buffer, 0, bufferSize);
		// loop read input stream until it does not fill whole size of buffer
		while (result == bufferSize) {
			out.write(buffer);
			// read next segment from input stream by moving the offset pointer
			offset += bufferSize;
			result = backup.read(offset, buffer, 0, bufferSize);
		}

		if (result > 0 && result < bufferSize) {
			for (int i = 0; i < result; i++) {
				out.write(buffer[i]);
			}

		}
		out.writeBytes(content + "\n");
		out.close();
		backup.close();
		fs.delete(new Path(tempFilePath));

	}
}

package com.pq.mapper;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import com.pq.tallysheet.service.ITallysheetSvc;
import com.pq.tallysheet.service.LocalTallysheetSvcImpl;
import com.pq.util.CommonUtil;
import com.pq.util.ExtractUtil;

public class UnarchiveMapper extends Mapper<Object, Text, Text, Text> {
	private Logger log = Logger.getLogger(UnarchiveMapper.class);

	private Path filePath;
	private Configuration conf;
	private String filePathString;
	private String zipFileName;
	private String archiveType;
	private FileSystem fs;
	private DateFormat dfDate = null;
	private String acqDateTime = "";

	private ITallysheetSvc tallysheetSvc = null;
	////////////////////////
	
	public long UNARCHIVE_MAPPING=0;
	//////////////////////

	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		log.info("Inside setup() of UnarchiveMapper");
		dfDate = new SimpleDateFormat("yyyy-MM-dd'T'hh-mm-ss");
		conf = context.getConfiguration();
		tallysheetSvc = new LocalTallysheetSvcImpl(conf.get("tallysheet.temp.location"));

	}

	@Override
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		UNARCHIVE_MAPPING=System.nanoTime();

		filePath = new Path(value.toString());
		filePathString = filePath.toString();
		fs = FileSystem.get(URI.create(filePathString), conf);
		filePath = new Path(value.toString());
		filePathString = filePath.toString();
		zipFileName = filePath.getName();
		archiveType = zipFileName.substring(zipFileName.lastIndexOf(".") + 1, zipFileName.length());
		log.info("filePathString: " + filePathString + ", fileName: " + zipFileName + ", archiveType: " + archiveType);
		boolean filePattern = CommonUtil.checkMatch(zipFileName, conf.get("data.pattern"), false);
		// Start ACQ processing date time
		Date dt = new Date();
		acqDateTime = dfDate.format(dt);

		// calculate the file size
		Path path = new Path(filePathString);
		FileStatus[] fileStatus = fs.listStatus(path);
		long fileLength = fileStatus[0].getLen();

		log.info("ACQ Date Time: " + acqDateTime);

		log.info("***** Processing zip file - " + zipFileName);
//		if (filePattern) {
			List<String> files = ExtractUtil.process(filePathString, archiveType, conf.get("unarchive.loc"), false);
			String sysDateTime = dfDate.format(new Date());
			for (String file : files) {
				// String val = file + "|" + acqDateTime + "|" + zipFileName + "|" +
				// String.valueOf(fileLength) + "|"
				// + sysDateTime;
				String val = file + "|" + zipFileName + "|" + sysDateTime + "|" + acqDateTime;
				log.info("unarchive mapper output: " + val);
				context.write(new Text("unarchive"), new Text(val));
				String fileName = file.substring(file.lastIndexOf("/") + 1, file.length());
				tallysheetSvc.init(conf.get("job.name"), CommonUtil.extractSequenceNumber(zipFileName,conf.get("db.name")),
						conf.get("config.name"), acqDateTime, sysDateTime, String.valueOf(fileLength),
						conf.get("mode"));
			}
//		}
		
		System.out.println("###### Unarchive mapping time: "+(System.nanoTime()-UNARCHIVE_MAPPING));

	}

}

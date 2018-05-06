package com.pq.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.pq.inputformat.FileNameInputFormat;
import com.pq.mapper.MedlineProcessMapper;
import com.pq.mapper.UnarchiveMapper;
import com.pq.partitioner.MyPartitioner;
import com.pq.reducer.MedlineProcessReducer;
import com.pq.tallysheet.service.TallySheetGenerator;
import com.pq.util.AccessS3Util;
import com.pq.util.CommonUtil;

public class SSNormalizationDriver extends Configured implements Tool {

	private static Logger log = Logger.getLogger(SSNormalizationDriver.class);
	static Configuration cf;
	// private static AmazonS3 s3 = null;
	private static final String LOCAL = "LOCAL";
	private static final String CLUSTER = "CLUSTER";
	private static String MODE = CLUSTER;

	
	public static void main(String[] args) throws Exception {
		log.info("Inside main() of SSNormalizationMapReduce job...");
		Properties prop = new Properties();
		populateProperties(args, prop);
		int res = ToolRunner.run(cf, new SSNormalizationDriver(), populateArgsFromProperties(prop));
		System.exit(res);
	}

	private static String[] populateArgsFromProperties(Properties prop) {
		String[] pargs = { prop.getProperty("DatabaseName"), prop.getProperty("InputFilePattern"),
				prop.getProperty("NormInputLocation"), prop.getProperty("HadoopOutputLocation"),
				prop.getProperty("NormLocation"), prop.getProperty("RawBackupLocation"),
				prop.getProperty("ErrorNormLocation"), prop.getProperty("ExpectedRecordCount"),
				prop.getProperty("TallysheetLocation"), prop.getProperty("UCSubmitLocation"),
				prop.getProperty("DocumentStartTag"), prop.getProperty("DocumentEndTag"),
				prop.getProperty("DeleteStartTag"), prop.getProperty("DeleteEndTag"), prop.getProperty("RootEndTag"),
				prop.getProperty("AccessionXPath"), prop.getProperty("ACQMinAge"), prop.getProperty("JobName"),
				prop.getProperty("TempTallysheetLocation"), prop.getProperty("ExecutionPlanLocation"),
				prop.getProperty("TallysheetAPIKey"), prop.getProperty("TallysheetUpdateServiceEndpoint"),
				prop.getProperty("ZipFileSeqRegex",""),prop.getProperty("RecordType",""),
				prop.getProperty("xmlValidation","")};
		return pargs;
	}

	private static void populateProperties(String[] args, Properties prop) throws FileNotFoundException, IOException {
		if (args.length > 1 && args[1].equalsIgnoreCase("LOCAL")) {
			MODE = LOCAL;
			InputStream input = new FileInputStream(args[0]);
			prop.load(input);
			input.close();
		} else {
			BufferedReader reader = new AccessS3Util().readFromS3(args[0]);
			prop.load(reader);
			reader.close();
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		int retVal = 0;

		// creating the new configuration
		cf = new Configuration();
		cf.set("job.name", args[17]); // job name
		cf.set("db.name", args[0]); // database name
		cf.set("data.pattern", args[1]); // file pattern
		cf.set("input.loc", args[2] + args[17] + "/"); // base location + job name
		cf.set("output.loc", args[3] + args[17] + "/"); // base location + job name
		// cf.set("output.loc", "/output/"+ args[17] + "/");
		// //////////////////////////////////////////////////////////////
		cf.set("norm.loc", args[4] + args[0] + "/" + args[17] + "/"); // base location + database name + job name
		cf.set("backup.loc", args[5] + args[0] + "/" + args[17] + "/"); // base location + database name + job name
		// cf.set("backup.loc", args[5] + args[0] + "/"); // base location + database
		// name
		cf.set("error.loc", args[6] + args[0] + "/" + args[17] + "/"); // base location + database name + job name
		cf.set("unarchive.loc", args[2] + "temp/");
		cf.set("tallysheet.loc", args[8] + args[17] + "/"); // base location + job name
		cf.set("ucsubmit.loc", args[9] + args[17] + "/"); // base location + job name

		cf.set("exp.record", args[7]);
		cf.set("config.name", args[0]);
		cf.set("story.start.tag", args[10]);
		cf.set("story.end.tag", args[11]);
		cf.set("delete.start.tag", args[12]);
		cf.set("delete.end.tag", args[13]);
		cf.set("root.end.tag", args[14]);
		cf.set("accession.xpath", args[15]);
		cf.set("acq.min.age", args[16]);
		cf.set("tallysheet.temp.location", args[18]); // used to store tallysheet info produced by different reducers
		cf.set("executionplan.location", args[19]);
		cf.set("mode", MODE);
		cf.set("tallysheet.api.key", args[20]);
		cf.set("tallysheet.update.url", args[21]);
	
		cf.set("zip.deq.regex", args[22]);
		cf.set("recordtype.config", args[23]);
		cf.set("xml.validation", args[24]);
		
		Job j = Job.getInstance(cf);
		// j.setSpeculativeExecution(false);
		// cf.set("dfs.support.broken.append", "true");
		// cf.set("dfs.support.append", "true");
		// cf.set("dfs.namenode.stale.datanode.interval", 30000);
		prepareAndLoadExecutionPlan(j, cf.get("input.loc"));

		// job part

		/********************************
		 * CHAIN MAPPER AREA STARTS
		 ********************************/
		Properties prop = new Properties();
		InputStream input = null;
//		String filepath = "src/main/resources/workflow.properties";
//		File file = new File(filepath);
//		input = new FileInputStream(file);
//		input=ClassLoader.getSystemResourceAsStream("workflow.properties");
		input=getClass().getResourceAsStream("../../../workflow.properties");
		// load a properties file
		prop.load(input);
		String mapperDetails = prop.getProperty(cf.get("db.name")+".mappers");
		String[] mapperDetailsList= mapperDetails.split("\\|");
		for(String mapperDetail:mapperDetailsList) {
			String[] splits = mapperDetail.split(",");
			
			Configuration unArchieveMapConfig = new Configuration(false);
			ChainMapper.addMapper(j,Class.forName(splits[0]).asSubclass(org.apache.hadoop.mapreduce.Mapper.class),Class.forName(splits[1]),Class.forName(splits[2]), Class.forName(splits[3]), Class.forName(splits[4]),
					unArchieveMapConfig);
		}
		
		
	

		// configuration for second mapper
/*		Configuration processMapConfig = new Configuration(false);
		// Chain Mapper class
		ChainMapper.addMapper(j, MedlineProcessMapper.class, Text.class, Text.class, Text.class, Text.class, processMapConfig);*/
		/********************************
		 * CHAIN MAPPER AREA FINISHES
		 ********************************/

		// now proceeding with the normal delivery
		j.setJarByClass(SSNormalizationDriver.class);
		String reducerDetails = prop.getProperty(cf.get("db.name")+".reducer");
		String[] reducerDetailsList= reducerDetails.split(",");
		j.setReducerClass(Class.forName(reducerDetailsList[0]).asSubclass(org.apache.hadoop.mapreduce.Reducer.class));
		j.setInputFormatClass(FileNameInputFormat.class);
		j.setOutputKeyClass(Class.forName(reducerDetailsList[3]));
		j.setOutputValueClass(Class.forName(reducerDetailsList[4]));

		// set the input and output URI
		FileInputFormat.setInputDirRecursive(j, true);
		FileInputFormat.addInputPath(j, new Path(cf.get("input.loc")));
		FileOutputFormat.setOutputPath(j, new Path(cf.get("output.loc")));

		log.info("PartitionerClass: " + j.getPartitionerClass().getName());
		log.info("NoOfReducers: " + j.getNumReduceTasks());

		retVal = j.waitForCompletion(true) ? 0 : 1;
		if (j.isSuccessful()) {
			TallySheetGenerator.generateTallysheet(cf.get("tallysheet.temp.location") + cf.get("job.name"),
					cf.get("tallysheet.loc"),cf.get("ucsubmit.loc"), MODE,cf.get("tallysheet.api.key"),cf.get("tallysheet.update.url"),cf.get("job.name"));
			System.out.println("job success: " + j.isSuccessful());
			if (MODE.equalsIgnoreCase(CLUSTER)) {
				DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd'T'hh-mm-ss");
				String backupTime = dfDate.format(new Date());

				System.out.println("moving input files to backup");
				 new AccessS3Util().listAllRawFilesInS3AndMove(cf.get("input.loc"), cf,
				 backupTime);
			}
		}
		return retVal;

	}

	private void prepareAndLoadExecutionPlan(Job j, String inputLocation) throws IOException, FileNotFoundException {
		FileSystem fs = FileSystem.get(URI.create(cf.get("input.loc")), cf);
		int counter = 0;
		Properties props = new Properties();
		RemoteIterator<LocatedFileStatus> files = fs.listFiles(new Path(inputLocation), true);
		while (files.hasNext()) {
			LocatedFileStatus next = files.next();
			String string = next.getPath().toString();
			System.out.println("####################filepath:" + string);
			int indexOf = string.lastIndexOf('/');
			if (string.endsWith("/") || string.endsWith("properties") || string.endsWith("end"))
				continue;
			props.put(CommonUtil.extractSequenceNumber(string.substring(indexOf + 1),cf.get("db.name")), "" + counter++);
		}
		String sharedFile = cf.get("executionplan.location") + cf.get("job.name")+"_execution_plan";
		if (cf.get("mode").equals("LOCAL")) {

			props.store(new FileWriter(sharedFile), sharedFile);
		} else {
			StringWriter sw = new StringWriter();
			props.list(new PrintWriter(sw));
			new AccessS3Util().uploadToS3(sharedFile, sw.getBuffer().toString());
		}

		j.addCacheFile(new Path(sharedFile).toUri());
		log.info("ValueOfCounter" + counter);
		j.setNumReduceTasks(counter);

		j.setPartitionerClass(MyPartitioner.class);
	}

}

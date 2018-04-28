package com;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class Driver extends Configured implements Tool {

	String INPUT_DIR = "input/partitioner";
	String OUTPUT_DIR = "output/partitioner";

	@Override
	public int run(String[] arg0) throws Exception {
		
		Configuration conf = getConf();
		
		FileSystem fs = FileSystem.get(conf);
		fs.deleteOnExit(new Path(OUTPUT_DIR));
		fs.close();
		
		int counter = 0;
		Properties props=new Properties();

		RemoteIterator<LocatedFileStatus> files = fs.listFiles(new Path(INPUT_DIR), true);
		while (files.hasNext()) {
			LocatedFileStatus next = files.next();
			String string = next.getPath().toString();
			int indexOf = string.lastIndexOf('/');
			props.put(string.substring(indexOf + 1),""+counter++);
		}
		String sharedFile = "execution_plan";
		props.store(new FileWriter(sharedFile), "execution plan");
		
		Job job = Job.getInstance(conf);
		job.addCacheFile(new Path(sharedFile).toUri());
		
		FileInputFormat.addInputPath(job, new Path(INPUT_DIR));
		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_DIR));
		job.setNumReduceTasks(counter);
		job.setPartitionerClass(MyPartitioner.class);
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new Driver(), args);
		System.exit(run);
	}

}

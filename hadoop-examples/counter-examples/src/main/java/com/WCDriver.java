package com;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WCDriver extends Configured implements Tool


{
	private static final String INPUT_DIR = "input/counter";
	private static final String OUTPUT_DIR = "output/counter";
	public int run(String[] args) throws Exception {
		
		
		Configuration conf = getConf();
		FileSystem fs = FileSystem.get(conf);
		fs.deleteOnExit(new Path(OUTPUT_DIR));
		fs.close();
		Job job = Job.getInstance(conf);
		job.setJarByClass(WCDriver.class);
		FileInputFormat.addInputPath(job, new Path(INPUT_DIR));
		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_DIR));
		job.setMapperClass(WCMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setReducerClass(WCReducer.class);
		job.setPartitionerClass(WCPartitioner.class);
		job.setNumReduceTasks(2);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		int status = job.waitForCompletion(true) ? 0 : 1;
		Counter counter = job.getCounters().findCounter(RecordCounter.MAP_RECORDS);
		System.out.println(counter.getDisplayName()+" "+counter.getName()+" "+counter.getValue());
		
		Counter counter2 = job.getCounters().findCounter(RecordCounter.REDUCE_RECORDS);
		System.out.println(counter2.getDisplayName()+" "+counter2.getName()+" "+counter2.getValue());
		return status;
	}

	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new WCDriver(), args);
		System.exit(run);

	}

}

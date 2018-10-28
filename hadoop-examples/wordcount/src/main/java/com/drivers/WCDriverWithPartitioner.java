package com.drivers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.WCMapper;
import com.WCPartitioner;
import com.WCReducer;

public class WCDriverWithPartitioner extends Configured implements Tool

{

	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		// Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(WCDriverWithPartitioner.class);
		FileInputFormat.addInputPath(job, new Path("input"));
		FileOutputFormat.setOutputPath(job, new Path("output/partitioned"));
		job.setMapperClass(WCMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setCombinerClass(WCReducer.class);
		job.setPartitionerClass(WCPartitioner.class);
		job.setReducerClass(WCReducer.class);
		job.setNumReduceTasks(2);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new WCDriverWithPartitioner(), args);
		System.exit(run);

	}

}

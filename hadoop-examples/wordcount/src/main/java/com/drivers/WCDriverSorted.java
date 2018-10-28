package com.drivers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.chain.ChainReducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.MapperInverse;
import com.WCMapper;
import com.WCPartitioner;
import com.WCReducer;
import com.WCReducerInverse;
import com.WCReducerInverse2;

public class WCDriverSorted extends Configured implements Tool

{

	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		Job job = Job.getInstance(conf);
		job.setJarByClass(WCDriverSorted.class);
		FileInputFormat.addInputPath(job, new Path("input"));
		FileOutputFormat.setOutputPath(job, new Path("output/pre-sorted"));

		ChainMapper.addMapper(job, WCMapper.class, LongWritable.class, Text.class, Text.class, IntWritable.class,
				new Configuration(false));
		ChainReducer.setReducer(job, WCReducerInverse.class, Text.class, IntWritable.class, IntWritable.class,
				Text.class, new Configuration(false));
		// ChainReducer.addMapper(job, MapperInverse.class, IntWritable.class,
		// Text.class, Text.class, IntWritable.class,
		// new Configuration(false));
		if (job.waitForCompletion(true)) {
			Job job2 = Job.getInstance(getConf());
			FileInputFormat.addInputPath(job2, new Path("output/pre-sorted"));
			FileOutputFormat.setOutputPath(job2, new Path("output/sorted"));
			ChainMapper.addMapper(job2, MapperInverse.class, LongWritable.class, Text.class, IntWritable.class,
					Text.class, new Configuration(false));
			ChainReducer.setReducer(job2, WCReducerInverse2.class, IntWritable.class, Text.class, Text.class,
					IntWritable.class, new Configuration(false));
			return job2.waitForCompletion(true) ? 0 : 1;
		}
		return 1;
	}

	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new WCDriverSorted(), args);
		System.exit(run);

	}

}

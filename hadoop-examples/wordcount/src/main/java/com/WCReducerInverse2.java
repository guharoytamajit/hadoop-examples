package com;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class WCReducerInverse2 extends Reducer<IntWritable,Text,Text, IntWritable>{
	


	@Override
	protected void reduce(IntWritable key, Iterable<Text> values,
			Reducer<IntWritable,Text,Text, IntWritable>.Context context) throws IOException, InterruptedException {
		
		context.write(values.iterator().next(),key);
		
	}


}

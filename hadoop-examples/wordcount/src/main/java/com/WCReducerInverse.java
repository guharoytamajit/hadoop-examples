package com;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class WCReducerInverse extends Reducer<Text, IntWritable, IntWritable,Text>{
	


	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, IntWritable,Text>.Context context) throws IOException, InterruptedException {
		int count=0;
		for (IntWritable intWritable : values) {
			count+=intWritable.get();
		}
		context.write(new IntWritable(count),key);
		
	}


}

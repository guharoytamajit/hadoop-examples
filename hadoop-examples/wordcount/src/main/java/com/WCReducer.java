package com;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class WCReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
	
	MultipleOutputs<Text, IntWritable> multipleOutputs;

	@Override
	protected void cleanup(Reducer<Text, IntWritable, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		multipleOutputs.close();
		
		super.cleanup(context);
	}

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		int count=0;
		for (IntWritable intWritable : values) {
			count+=intWritable.get();
		}
//		context.write(key, new IntWritable(count));
		if((key.toString().compareTo("i"))<0) {
			multipleOutputs.write(key, new IntWritable(count), "abc.txt");
		}else {
			multipleOutputs.write(key, new IntWritable(count), "abc.txt");
		}
		
	}

	@Override
	protected void setup(Reducer<Text, IntWritable, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		multipleOutputs=new MultipleOutputs<>(context);
		super.setup(context);
	}

}

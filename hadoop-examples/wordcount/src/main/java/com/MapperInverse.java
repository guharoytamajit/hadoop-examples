package com;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapperInverse extends Mapper<LongWritable, Text,IntWritable, Text> {


	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text,IntWritable, Text>.Context context)
			throws IOException, InterruptedException {
		String[] split = value.toString().split("\t");
		if(split.length==2)
			context.write(new IntWritable(Integer.parseInt(split[0])),new Text(split[1]));
	}


}

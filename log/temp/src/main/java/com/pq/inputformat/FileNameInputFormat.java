package com.pq.inputformat;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
 
import org.apache.hadoop.mapreduce.RecordReader;
 
import org.apache.hadoop.mapreduce.TaskAttemptContext;
 
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
public class FileNameInputFormat extends FileInputFormat<Object, Text> {
	
	 
	@Override
	 
	public RecordReader<Object, Text> createRecordReader(InputSplit arg0,
	 
	TaskAttemptContext arg1) throws IOException, InterruptedException {
	 
	return new FileNameRecordReader();
	 
	}
	 
	}
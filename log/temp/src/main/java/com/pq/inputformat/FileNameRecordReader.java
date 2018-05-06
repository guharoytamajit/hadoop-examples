package com.pq.inputformat;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class FileNameRecordReader extends RecordReader<Object, Text> {

	private Object key;
	private Text value;
	private InputSplit inputSplit;
	private int count = 0;

	@Override
	public void close() throws IOException {

	}

	@Override
	public Object getCurrentKey() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return value;

	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return 1.0f;
	}

	@Override
	public void initialize(InputSplit inputSplit, TaskAttemptContext arg1) throws IOException, InterruptedException {
		this.inputSplit = inputSplit;

	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (count == 0) {
			key=new Object();
			value=new Text(((FileSplit)inputSplit).getPath().toString());
			count++;
			return true;
		} else {
			return false;
		}
	}

}

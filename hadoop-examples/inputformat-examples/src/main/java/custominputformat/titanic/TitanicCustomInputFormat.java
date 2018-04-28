package custominputformat.titanic;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class TitanicCustomInputFormat extends FileInputFormat<AliveGenderTuple,IntWritable>{

	@Override
	public RecordReader<AliveGenderTuple, IntWritable> createRecordReader(InputSplit inputSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return new TitanicRecordReader();
	}

}

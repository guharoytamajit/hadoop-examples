package custominputformat.titanic;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

public class TitanicRecordReader extends RecordReader<AliveGenderTuple, IntWritable> {
	LineRecordReader reader = new LineRecordReader();
	AliveGenderTuple key;
	IntWritable value;

	@Override
	public void close() throws IOException {
		reader.close();

	}

	@Override
	public AliveGenderTuple getCurrentKey() throws IOException, InterruptedException {

		return key;
	}

	@Override
	public IntWritable getCurrentValue() throws IOException, InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return reader.getProgress();
	}

	@Override
	public void initialize(InputSplit arg0, TaskAttemptContext arg1) throws IOException, InterruptedException {
		reader.initialize(arg0, arg1);

	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		boolean gotNext = reader.nextKeyValue();

		if (gotNext) {
			Text line = reader.getCurrentValue();
			if (key == null) {
				key = new AliveGenderTuple();
			}
			if (value == null) {
				value = new IntWritable();
			}

			String[] tokens = line.toString().split(",");

			key.setAlive(new String(tokens[1]));

			key.setGender(new String(tokens[4]));

			value.set(new Integer(1));

		} else {
			key = null;
			value = null;

		}
		return gotNext;
	}

}

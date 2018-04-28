package custominputformat.titanic;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class TitanicReducer extends Reducer<AliveGenderTuple, IntWritable, AliveGenderTuple, IntWritable> {

	@Override
	protected void reduce(AliveGenderTuple key, Iterable<IntWritable> values,
			Reducer<AliveGenderTuple, IntWritable, AliveGenderTuple, IntWritable>.Context context)
			throws IOException, InterruptedException {
		int sum = 0;
		for (IntWritable val : values) {
			sum += val.get();
		}
		context.write(key, new IntWritable(sum));
	}

}

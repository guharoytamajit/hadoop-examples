package multipleinputs;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MyMapper extends Mapper<Text, Text, Text, IntWritable> {
	static int counter=0;

	@Override
	protected void setup(Mapper<Text, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		super.setup(context);
		System.out.println("setup of mapper"+(++counter));
	}

	@Override
	protected void map(Text key, Text value, Mapper<Text, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		context.write(key,new IntWritable(Integer.parseInt(value.toString())) );
	}

}

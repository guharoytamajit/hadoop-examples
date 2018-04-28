package dbinputformat;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MyMapper extends Mapper<LongWritable, DBInputWritable, DBOutputWritable, NullWritable> {
	static int counter=0;

	@Override
	protected void setup(Mapper<LongWritable, DBInputWritable, DBOutputWritable, NullWritable>.Context context) throws IOException, InterruptedException {
		super.setup(context);
		System.out.println("setup of mapper"+(++counter));
	}

	@Override
	protected void map(LongWritable key, DBInputWritable value, Mapper<LongWritable, DBInputWritable, DBOutputWritable, NullWritable>.Context context)
			throws IOException, InterruptedException {
		String valueString = value.getUSER_ID()+"\t"+value.getUSERNAME()+"\t"+value.getCREATED_BY();
		System.out.println("key="+key+" value="+valueString);
		context.write(new DBOutputWritable(value.getUSERNAME(),value.getUSER_ID()),NullWritable.get());
	}

}

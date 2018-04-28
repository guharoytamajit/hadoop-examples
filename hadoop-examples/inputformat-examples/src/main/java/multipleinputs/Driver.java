package multipleinputs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Driver extends Configured implements Tool {
	private static final String INPUT_DIR = "input/multipleinputs/club";
	private static final String INPUT_DIR2 = "input/multipleinputs/national";
	private static final String OUTPUT_DIR = "output/multipleinputs";

	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new Driver(), args);
		System.exit(run);
	}

	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		FileSystem fs = FileSystem.get(conf);
		fs.deleteOnExit(new Path(OUTPUT_DIR));
		fs.close();

		Job job = Job.getInstance(getConf());
		
		MultipleInputs.addInputPath(job, new Path(INPUT_DIR), KeyValueTextInputFormat.class, MyMapper.class);
		MultipleInputs.addInputPath(job, new Path(INPUT_DIR2), KeyValueTextInputFormat.class, MyMapper.class);
		
		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_DIR));
		//job.setInputFormatClass(KeyValueTextInputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
//		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);
		
		job.setNumReduceTasks(1);
		return job.waitForCompletion(true) ? 0 : 1;
	}
}

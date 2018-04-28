package textinputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import textinputformat.MyMapper;

public class Driver extends Configured implements Tool {
	private static final String INPUT_DIR = "input/textInputFormatInput";
	private static final String OUTPUT_DIR = "output/textInputFormat";

	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new Driver(), args);
		System.exit(run);
	}

	public int run(String[] args) throws Exception {

		Configuration conf = getConf();
		FileSystem fs = FileSystem.get(conf);
			fs.deleteOnExit(new Path(OUTPUT_DIR));
			fs.close();
		Job job = Job.getInstance(conf);
		FileInputFormat.addInputPath(job, new Path(INPUT_DIR));
		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_DIR));
		job.setMapperClass(MyMapper.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}
}

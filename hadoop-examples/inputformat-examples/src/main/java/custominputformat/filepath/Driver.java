package custominputformat.filepath;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Driver extends Configured implements Tool{

	String INPUT_DIR="input/customInputFormat/filepath";
	String OUTPUT_DIR="output/customInputFormat/filepath";
	public int run(String[] arg0) throws Exception {
		FileSystem fs = FileSystem.get(getConf());
		fs.deleteOnExit(new Path(OUTPUT_DIR));
		fs.close();
		Job job = Job.getInstance(getConf());
		FileInputFormat.addInputPath(job, new Path(INPUT_DIR));
		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_DIR));	
		
		job.setInputFormatClass(FilePathInputFormat.class);
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		
		
		return job.waitForCompletion(true)?0:1;
	}
	public static void main(String[] args) throws Exception {
		int status = ToolRunner.run(new Driver(), args);
		System.exit(status);
	}

}

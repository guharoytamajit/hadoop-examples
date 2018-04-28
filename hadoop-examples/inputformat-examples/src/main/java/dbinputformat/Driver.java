package dbinputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Driver extends Configured implements Tool {
//	private static final String INPUT_DIR = "input/dbInputFormatInput";
//	private static final String OUTPUT_DIR = "output/dbInputFormat";

	public static void main(String[] args) throws Exception {
		DBUtil.populateRecord();
		int run = ToolRunner.run(new Driver(), args);
		DBUtil.checkRecord();
		System.exit(run);
	}

	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		DBConfiguration.configureDB(conf,
				 "org.h2.Driver", // driver class
				 "jdbc:h2:~/test", // db url
				 "sa", // user name
				 ""); //password
		FileSystem fs = FileSystem.get(conf);
//		fs.deleteOnExit(new Path(OUTPUT_DIR));
		fs.close();

		Job job = Job.getInstance(getConf());
//		FileInputFormat.addInputPath(job, new Path(INPUT_DIR));
//		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_DIR));
		job.setInputFormatClass(DBInputFormat.class);
		job.setOutputFormatClass(DBOutputFormat.class);
		
		DBInputFormat.setInput(
				 job,
				 DBInputWritable.class,
				 "DBUSER", //input table name
				 null,
				 null,
				 new String[] { "USER_ID", "USERNAME" ,"CREATED_BY"} // table columns
				 );
		
		DBOutputFormat.setOutput(
				 job,
				 "employee", // output table name
				 new String[] { "name", "id" } //table columns
				 );
		
		job.setMapperClass(MyMapper.class);
		job.setOutputKeyClass(DBOutputWritable.class);
		job.setOutputValueClass(NullWritable.class);
		job.setNumReduceTasks(0);
		return job.waitForCompletion(true) ? 0 : 1;
	}
}

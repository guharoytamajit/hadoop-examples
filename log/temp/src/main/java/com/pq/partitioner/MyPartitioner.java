package com.pq.partitioner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Properties;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;

import com.pq.util.AccessS3Util;

public class MyPartitioner extends Partitioner<Text, Text> implements Configurable {

	Configuration conf;

	static Properties properties = null;
	@Override
	public int getPartition(Text key, Text value, int num) {
		String[] split = key.toString().split("\\|");
		String sequenceNumber = split[split.length-1].trim();
		if (properties == null) {
			properties = new Properties();
			try {
				URI[] cacheFiles = Job.getInstance(getConf()).getCacheFiles();
				for (URI uri : cacheFiles) {
					System.out.println("urifound: "+uri);
					if (uri.toString().endsWith("execution_plan")) {
						if(conf.get("mode").equals("LOCAL")) {
						properties.load(new java.io.FileReader(uri.getPath().toString()));
						}else {
						BufferedReader readFromS3 = new AccessS3Util().readFromS3(conf.get("executionplan.location")+conf.get("job.name")+"_execution_plan");
						properties.load(readFromS3);
						readFromS3.close();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		System.out.println(sequenceNumber+"=====>"+properties.getProperty(sequenceNumber));
		return Integer.parseInt(properties.getProperty(sequenceNumber));
	}

	@Override
	public Configuration getConf() {
		return conf;
	}

	@Override
	public void setConf(Configuration arg0) {
		conf = arg0;
	}

}

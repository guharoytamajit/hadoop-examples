package com.pq.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AccessS3Util {

	private AWSCredentials credentials;
	private AmazonS3 s3;
	private String bucketName;
	private PutObjectResult status1;

	@SuppressWarnings("deprecation")
	public AccessS3Util() {
		this.bucketName = "content-bulk-analysis";
//		s3 = new AmazonS3Client();
//		s3=new ProfileCredentialsProvider("").getCredentials();
		credentials = new BasicAWSCredentials("AKIAID7TYXLGCYLG6NHQ", "tPmosyxgF2rF4Pp9zY0nltfKY8MFHVksxkMSW1jK");
		s3 = new AmazonS3Client(credentials);
	}

	public BufferedReader readFromS3(String key) throws IOException {
		if(key.startsWith("s3:")) {
		key = key.substring(key.indexOf(this.bucketName) + this.bucketName.length() + 1,
				key.length());
		}
		S3Object s3object = s3.getObject(new GetObjectRequest(this.bucketName, key));
		/*
		 * System.out.println(s3object.getObjectMetadata().getContentType());
		 * System.out.println(s3object.getObjectMetadata().getContentLength());
		 */

		BufferedReader reader = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));

		return reader;
	}

	public boolean moveFileFromS3toS3(String s3Source, String s3Destination) {
		boolean status = false;
		String sourceKey = s3Source.substring(s3Source.indexOf(this.bucketName) + this.bucketName.length() + 1,
				s3Source.length());
		String destKey = s3Destination.substring(s3Destination.indexOf(this.bucketName) + this.bucketName.length() + 1,
				s3Destination.length());
		
		System.out.println("s3Source: "+s3Source+" & sourceKey: "+sourceKey);
		System.out.println("s3Destination: "+s3Destination+" & destKey: "+destKey);

		CopyObjectResult cpResult = s3.copyObject(this.bucketName, sourceKey, this.bucketName, destKey);
		status = cpResult != null ? true : false;
		
		if (status){
			if (s3.doesObjectExist(this.bucketName, sourceKey)) {
				s3.deleteObject(this.bucketName, sourceKey);
			}
		}
		
		return status;
	}

	public void listAllRawFilesInS3AndMove(String s3FullLoc, Configuration conf, String backupTime) {
		System.out.println("Inside listAllRawFilesInS3AndMove()...");
		
		String prefix = s3FullLoc.substring(s3FullLoc.indexOf(this.bucketName) + this.bucketName.length() + 1, s3FullLoc.length());
		System.out.println("prefix: "+prefix);
		
		ObjectListing objectListing = s3.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix));
		
		for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
			System.out.println("objectSummary.getKey(): "+objectSummary.getKey());
			String f = objectSummary.getKey().substring(objectSummary.getKey().lastIndexOf("/")+1, objectSummary.getKey().length());
			System.out.println("file: "+f);
			// backup the raw files
			boolean status = moveFileFromS3toS3(conf.get("input.loc") + f, conf.get("backup.loc") + f+"_"+backupTime);
			System.out.println("Status of moving of '"+objectSummary.getKey()+"' is: "+status);
		}
		//return null;
	}
	
	public boolean doesFileExists(String s3FullPath){
		boolean status = false;
		String sourceKey = s3FullPath.substring(s3FullPath.indexOf(this.bucketName) + this.bucketName.length() + 1,
				s3FullPath.length());
		status = s3.doesObjectExist(this.bucketName, sourceKey);
		return status;
	}
	public  boolean uploadToS3(String path,String content){
		//boolean status = false;
		if(path.contains(this.bucketName)) {
			path=minus(path,this.bucketName);
		}
		System.out.println("path*************************"+path);
		PutObjectResult putObject = s3.putObject(this.bucketName, path, content);
		return true;
	}
	public  boolean uploadToS3(String s3Path,InputStream is){
		//boolean status = false;
		if(s3Path.contains(this.bucketName)) {
			s3Path=minus(s3Path,this.bucketName);
		}
		System.out.println("s3Path*************+***********"+s3Path);
		PutObjectResult putObject = s3.putObject(this.bucketName, s3Path, is,new ObjectMetadata());
		return true;
	}
	static String minus(String s,String sub) {
		int start = s.indexOf(sub)+sub.length()+1;
	return	s.substring(start);
	}
}

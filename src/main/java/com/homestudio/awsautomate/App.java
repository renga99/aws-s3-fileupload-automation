package com.homestudio.awsautomate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

/**
 * AWS S3 file upload utility.
 *
 * @author Rengaraj Selvaraj
 * @since 1.0
 *
 */
@SuppressWarnings({ "unused", "deprecation" })
public final class App {

	private static final Logger LOG = Logger.getLogger(App.class);

	private static final String LINE = "\n=========================================================";

	private static AWSCredentials credentials;

	private static String bucketName = "testbucketforrengraj.s3-website-ap-southeast-1.amazonaws.com";

	private static TransferManager tx;

	private static MultipleFileUpload upload;

	private App() { }


	@SuppressWarnings("deprecation")
	public static void main(final String... args) throws AmazonServiceException, AmazonClientException, InterruptedException, IOException {

		final Scanner scanner = new Scanner(System.in);

		if (LOG.isInfoEnabled()) {
			LOG.info(LINE
					+ "\n                                                         "
					+ "\n   **  AWS S3 file upload utility.	**					"
					+ "\n                                                         "
					+ LINE );
		}

		@SuppressWarnings("resource")
		final GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		final ConfigurableEnvironment environment = context.getEnvironment();

		System.out.println("Choose an option...");
		System.out.println("\t1. Upload file folder to aws S3");
		System.out.println("\tq. Quit");
		System.out.print(" > ");

		String filePath;

		while (true) {
			final String input = scanner.nextLine();

			if("1".equals(input.trim())) {

				System.out.println("Uploading to Amazon S3...");

				environment.setActiveProfiles("upload-to-s3");
				setupCredentials(environment, scanner);
				setupS3info(environment, scanner);

				context.load("classpath:META-INF/spring/integration/*-context.xml");
				context.registerShutdownHook();
				context.refresh();

				LOG.info("Enter folder path :");
				filePath = scanner.nextLine();

				credentials = context.getBean("credentials", AWSCredentials.class);
				tx = new TransferManager(credentials);
				bucketName = bucketName + credentials.getAWSAccessKeyId().toLowerCase();
				createAmazonS3Bucket();

				upload = tx.uploadDirectory(bucketName, "homestudio", new File(filePath), true);

				upload.addProgressListener(new ProgressListener() {
					public void progressChanged(ProgressEvent progressEvent) {
						System.out.println(upload.getProgress().getPercentTransferred() + "%");

						if (progressEvent.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {
							System.out.println("Upload completed !");
						}
					}
				});

				upload.waitForCompletion();

				//uploadFile();
				break;

			}
			else if("q".equals(input.trim())) {
				System.out.println("Exiting application...");
				System.exit(0);
			}
			else {
				System.out.println("wrong command\n\n");
				System.out.print(">");
			}
		}


		if (LOG.isInfoEnabled()) {
			LOG.info("Shutdown complete");
		}

		System.exit(0);

	}
	
//	public static void main(String args[]) throws IOException{
//		uploadFile();
//	}



//	private static void uploadFile() throws IOException {
//		String existingBucketName = "testbucketforrengraj";
//		  String keyName = "HS1_Cleo_1.jpg";
//		  
//		  String filePath = "C:\\work\\temp\\import\\HS1_Cleo\\HS1_Cleo_1.jpg";
//		  String amazonFileUploadLocationOriginal=existingBucketName+"/";
//		  
//
//		  AmazonS3 s3Client = new AmazonS3Client(new PropertiesCredentials(new File("app.properties")));
//		  
//		  
//		  FileInputStream stream = new FileInputStream(filePath);
//		  ObjectMetadata objectMetadata = new ObjectMetadata();
//		  PutObjectRequest putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal, keyName, stream, objectMetadata);
//		  PutObjectResult result = s3Client.putObject(putObjectRequest);
//		  System.out.println("Etag:" + result.getETag() + "-->" + result);
//
//		
//	}


	private static void setupCredentials(ConfigurableEnvironment environment, Scanner scanner) {
		if (!environment.containsProperty("accessKey")) {
			System.out.print("\nPlease enter your Access Key ID: ");
			final String accessKey = scanner.nextLine();
			environment.getSystemProperties().put("accessKey", accessKey);
		}

		if (!environment.containsProperty("secretKey")) {
			System.out.print("\nPlease enter your Secret Access Key: ");
			final String secretKey = scanner.nextLine();
			environment.getSystemProperties().put("secretKey", secretKey);
		}
	}

	private static void setupS3info(ConfigurableEnvironment environment, Scanner scanner) {
		if (!environment.containsProperty("bucket")) {
			System.out.print("\nWhich bucket do you want to use? ");
			final String bucket = scanner.nextLine();
			environment.getSystemProperties().put("bucket", bucket);
		}

		if (!environment.containsProperty("remoteDirectory")) {
			System.out.print("\nPlease enter the S3 remote directory to use: ");
			final String remoteDirectory = scanner.nextLine();
			environment.getSystemProperties().put("remoteDirectory", remoteDirectory);
		}
	}

	private static void createAmazonS3Bucket() {
		try {
			if (tx.getAmazonS3Client().doesBucketExist(bucketName) == false) {
				tx.getAmazonS3Client().createBucket(bucketName);
			}
		} catch (AmazonClientException ace) {
			LOG.error("Unable to create a new Amazon S3 bucket: " + ace.getMessage()+ 
					" Error Creating Bucket");
		}
	}
	
	

}
Step 1 : mvn clean package exec:java
 		 OR
 		 mvn clean package exec:java -DaccessKey=<accesskey> -DsecretKey=<secretkey>

Step 2 : Choose an option...

		1. Upload file folder to aws S3
		q. Quit
		>

Step 3 : Enter folder path : /path/to/folder


update bucket name in below variable in App.java

private static String bucketName = "XXXXXXX.s3-website-ap-southeast-1.amazonaws.com";

Access Key ID:
XXXXXXX

Secret Access Key:
XXXXXXX
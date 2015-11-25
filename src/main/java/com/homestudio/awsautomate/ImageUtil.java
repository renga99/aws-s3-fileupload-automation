package com.homestudio.awsautomate;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtil {


	public static void process() throws FileNotFoundException, IOException{

		File imageFile = new File("C:\\work\\temp\\import\\HS1_Cleo\\HS1_Cleo_1.jpg");
		File compressedImageFile = new File("C:\\work\\temp\\import\\HS1_Cleo\\HS1_Cleo_1_compressed.jpg");

		InputStream inputStream = new FileInputStream(imageFile);
		OutputStream outputStream = new FileOutputStream(compressedImageFile);

		float imageQuality = 0.9f;

		//Create the buffered image
		BufferedImage bufferedImage = ImageIO.read(inputStream);

		//Get image writers
		Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("jpg");

		if (!imageWriters.hasNext())
			throw new IllegalStateException("Writers Not Found!!");

		ImageWriter imageWriter = (ImageWriter) imageWriters.next();
		ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
		imageWriter.setOutput(imageOutputStream);

		ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

		//Set the compress quality metrics
		imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		imageWriteParam.setCompressionQuality(imageQuality);

		//Created image
		imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);

		// close all streams
		inputStream.close();
		outputStream.close();
		imageOutputStream.close();
		imageWriter.dispose();
	}


}

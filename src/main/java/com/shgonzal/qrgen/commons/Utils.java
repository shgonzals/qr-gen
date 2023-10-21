package com.shgonzal.qrgen.commons;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class Utils {

	public static byte[] imageToByteArray(String image, String path){
		byte[] img = null;

		String absolutePath = Paths.get(image, path).toAbsolutePath().toString();

		File imageFile = new File(absolutePath);

		if(imageFile.exists()){
			try {
				img = convertFileToByteArray(imageFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return img;
	}

	public static byte[] convertFileToByteArray(File file) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(file);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int bytesRead;

		while ((bytesRead = fileInputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}

		return outputStream.toByteArray();
	}

	public static byte[] bufferedImageToByteArray(BufferedImage image) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, Constants.FORMAT_PNG, outputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return outputStream.toByteArray();
	}
}

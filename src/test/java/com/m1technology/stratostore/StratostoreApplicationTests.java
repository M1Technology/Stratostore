package com.m1technology.stratostore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.m1technology.stratostore.model.Encrypted;
import com.m1technology.stratostore.service.EndecService;
import com.m1technology.stratostore.service.KeyService;
import com.m1technology.stratostore.service.SecretSharingService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StratostoreApplicationTests {

	@Autowired
    private KeyService keyService;
	
	@Autowired
	private EndecService endecService;
	
	@Autowired
	private SecretSharingService secretSharingService;
	
	@Test 
	public void testDieharder() {
		//https://webhome.phy.duke.edu/~rgb/General/dieharder.php
	}
	
	
	@Test 
	public void testKeyServiceReturnsProperLengthKey() {
		
		final byte[] key1 = keyService.getKey(100);
		assertEquals(key1.length, 100);
		
		final byte[] key2 = keyService.getKey(2000);
		assertEquals(key2.length, 2000);
		
		final byte[] key3 = keyService.getKey(30000);
		assertEquals(key3.length, 30000);
		
		final byte[] key4 = keyService.getKey(400000);
		assertEquals(key4.length, 400000);
		
		final byte[] key5 = keyService.getKey(50000000);
		assertEquals(key5.length, 50000000);
		
		final byte[] key6 = keyService.getKey(600000000);
		assertEquals(key6.length, 600000000);
		
		
	}
	
	
	
	@Test 
	public void testEncryptAndDeryptDataReturnsExpectedData() {
	
		String input = "This is my input string with a bunch of special characters, like !@#$%^&*()_+|}{|:<>?'";
		byte[] data = input.getBytes();
		
		for (int desiredShares = 2; desiredShares < 10; desiredShares++) {
			List<byte[]> shares = secretSharingService.share(data, desiredShares, desiredShares);
			byte[] returnedData = secretSharingService.reconstruct(shares);
			
			assertEquals(input, new String(returnedData, StandardCharsets.UTF_8));	
		}
		
		
	}
	
	
	
	@Test
	public void testLargePngImage() {
		//Convert image to byte[]
		byte[] originalImageBytes = null;
		BufferedImage image = null;
		try{  
			File imageLocation = new File("./src/test/java/resources/input-image.png");
			image = ImageIO.read(imageLocation);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			ImageIO.write(image, "png", baos); 
			originalImageBytes = baos.toByteArray(); 
			
			
		} catch(Exception e) { 
			e.printStackTrace(); 
			 
		}

		//Convert the byte[] into framents
		Encrypted encrypted = endecService.encrypt(originalImageBytes);
		
		byte[] share1bytes = new byte[originalImageBytes.length];
		byte[] share2bytes = new byte[originalImageBytes.length];
		
		byte[] decryptedBytes = endecService.decrypt(encrypted);
		
		System.out.print("length is: " + originalImageBytes.length + " !");
		
		for (int i = 0; i < originalImageBytes.length; i++) {
			if (i < 200) { //some of the bytes are header info that we need to keep
				share1bytes[i] = originalImageBytes[i];
				share2bytes[i] = originalImageBytes[i];
			} else {
				share1bytes[i] = encrypted.getCiphertext()[i];
				share2bytes[i] = encrypted.getKey()[i];
			}
		}
		
		    
		//write original back to file to test that it's the same as the input
		InputStream orig = new ByteArrayInputStream(decryptedBytes);
		BufferedImage original2;
		try {
			original2 = ImageIO.read(orig);
			ImageIO.write(original2, "png", new File("./src/test/java/resources/output-image.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			File fileCombined = new File("./src/test/java/resources/output-image.png");
			
			BufferedImage imageCombined = ImageIO.read(fileCombined);
			
			for (int x = 0; x < image.getWidth(); x++) {
	            for (int y = 0; y < image.getHeight(); y++) {
	            	assertEquals(image.getRGB(x, y), imageCombined.getRGB(x, y));
	            }
	        }
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	@Test
	public void test_visuallyInspectDecryptedShares() {
		
		
		//Read in an image as a byte array
		byte[] imageBytes = null; 
		BufferedImage image = null;
		try {
			File imageLocation = new File("./src/test/java/resources/m1-splash.bmp");
			image = ImageIO.read(imageLocation);
			//image = ImageIO.read(getClass().getResourceAsStream("m1-splash.bmp"));
			
			imageBytes = new byte[image.getHeight() * image.getWidth() * 4]; //4 bytes per pixel
			int byteCounter = 0;
			
			for (int x = 0; x < image.getWidth(); x++) {
	            for (int y = 0; y < image.getHeight(); y++) {
	                Color color = new Color(image.getRGB(x, y));
	                imageBytes[byteCounter++] = (byte) color.getAlpha();
	                imageBytes[byteCounter++] = (byte) color.getRed();
	                imageBytes[byteCounter++] = (byte) color.getGreen();
	                imageBytes[byteCounter++] = (byte) color.getBlue();
	            }
	        }
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		Encrypted encrypted = endecService.encrypt(imageBytes);
		
		byte[] shareA = encrypted.getCiphertext();
		outputBytesToImage(image.getHeight(), image.getWidth(), shareA, "m1-splash-share-a");
		
		byte[] shareB = encrypted.getKey();
		outputBytesToImage(image.getHeight(), image.getWidth(), shareB, "m1-splash-share-b");
		
		byte[] decrypted = endecService.decrypt(encrypted);
		outputBytesToImage(image.getHeight(), image.getWidth(), decrypted, "m1-splash-shares-combined");
		
		
		
		//Ensure that the shares have different RGB values than the original, and that the combined shares are equal
		try {
			File fileA = new File("./src/test/java/resources/m1-splash-share-a.png");
			File fileB = new File("./src/test/java/resources/m1-splash-share-b.png");
			File fileCombined = new File("./src/test/java/resources/m1-splash-shares-combined.png");
			 
			BufferedImage imageA = ImageIO.read(fileA);
			BufferedImage imageB = ImageIO.read(fileB);
			BufferedImage imageCombined = ImageIO.read(fileCombined);
			
			for (int x = 0; x < image.getWidth(); x++) {
	            for (int y = 0; y < image.getHeight(); y++) {

	            	assertTrue(image.getRGB(x, y) != imageA.getRGB(x, y));
	            	assertTrue(image.getRGB(x, y) != imageB.getRGB(x, y));
	            	
	            	assertEquals(image.getRGB(x, y), imageCombined.getRGB(x, y));
	            }
	        }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Converts a byte array into an image.  Each pixel is represented by 4 bytes in the ARGB array.
	public void outputBytesToImage(int imageHeight, int imageWidth, byte[] bytes, String imageName) {
		int newCounter = 0;
		BufferedImage newImage = new BufferedImage(imageHeight, imageWidth, BufferedImage.TYPE_INT_ARGB_PRE);
		for (int x = 0; x < newImage.getWidth(); x++) {
			for (int y = 0; y < newImage.getHeight(); y++) {
				byte[] color = {bytes[newCounter++], bytes[newCounter++], bytes[newCounter++], bytes[newCounter++]}; 
				int argb = byteArrayToInt(color);
				newImage.setRGB(x, y, argb);
	        }
	    }
	    
		//write the image to a file
	    File outputfile = new File("./src/test/java/resources/" + imageName + ".png");
	    try {
			ImageIO.write(newImage, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Converts a 4 byte array into an integer
	public static int byteArrayToInt(byte[] b) {
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
	
	
}
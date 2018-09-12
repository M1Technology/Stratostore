package com.m1technology.stratostore;

import com.m1technology.stratostore.model.Encrypted;
import com.m1technology.stratostore.service.EndecService;
import com.m1technology.stratostore.service.KeyService;
import com.m1technology.stratostore.service.SecretSharingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void test_keyService_returnsProperLengthKey() {

        final byte[] key1 = keyService.getKey(100);
        assertEquals(100, key1.length);

        final byte[] key2 = keyService.getKey(2000);
        assertEquals(2000, key2.length);

        final byte[] key3 = keyService.getKey(30000);
        assertEquals(30000, key3.length);

        final byte[] key4 = keyService.getKey(400000);
        assertEquals(400000, key4.length);

        final byte[] key5 = keyService.getKey(50000000);
        assertEquals(50000000, key5.length);

        final byte[] key6 = keyService.getKey(600000000);
        assertEquals(600000000, key6.length);
    }

    @Test
    public void test_secretSharingService_shareAndReconstructReturnsExpectedDataForMultipleLengthShares() throws IOException {

        String input = "This is my input string with a bunch of special characters, like !@#$%^&*()_+|}{|:<>?'";
        byte[] data = input.getBytes();

        for (int desiredShares = 2; desiredShares < 10; desiredShares++) {
            List<byte[]> shares = secretSharingService.share(data, desiredShares, desiredShares);
            byte[] returnedData = secretSharingService.reconstruct(shares);

            assertEquals(input, new String(returnedData, StandardCharsets.UTF_8));
        }
    }


    @Test
    public void test_secretSharingService_readAndWriteLargePngImageAndConfirmEachPixelIsCorrect() throws IOException {
        //Convert image to byte[]
        byte[] originalImageBytes;
        BufferedImage originalImage;
        File imageLocation = new File("./src/test/java/resources/input-image.png");
        originalImage = ImageIO.read(imageLocation);

        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(originalImage, "png", baos);
            originalImageBytes = baos.toByteArray();
        }

        //Convert the byte[] into shares
        List<byte[]> encrypted = secretSharingService.share(originalImageBytes, 2, 2);
        byte[] decryptedBytes = secretSharingService.reconstruct(encrypted);

        //write original back to file to test that it's the same as the input
        BufferedImage reconstructedOriginal;
        try(ByteArrayInputStream b = new ByteArrayInputStream(decryptedBytes)) {
            reconstructedOriginal = ImageIO.read(b);
        }
        ImageIO.write(reconstructedOriginal, "png", new File("./src/test/java/resources/output-image.png"));

        File fileCombined = new File("./src/test/java/resources/output-image.png");
        BufferedImage originalImageReconstructed = ImageIO.read(fileCombined);

        //Ensure that each pixel in the reconstructed image matches the corresponding pixel in the original image.
        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                assertEquals(originalImage.getRGB(x, y), originalImageReconstructed.getRGB(x, y));
            }
        }
    }

    @Test
    public void test_endecService_visuallyInspectDecryptedSharesAndConfirmEachPixelIsCorrect() throws IOException {

        //Read in an image as a byte array
        byte[] imageBytes;
        BufferedImage image;
        File imageLocation = new File("./src/test/java/resources/m1-splash.bmp");
        image = ImageIO.read(imageLocation);

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

        Encrypted encrypted = endecService.encrypt(imageBytes);

        byte[] shareA = encrypted.getCiphertext();
        outputBytesToImage(image.getHeight(), image.getWidth(), shareA, "m1-splash-share-a");

        byte[] shareB = encrypted.getKey();
        outputBytesToImage(image.getHeight(), image.getWidth(), shareB, "m1-splash-share-b");

        byte[] decrypted = endecService.decrypt(encrypted);
        outputBytesToImage(image.getHeight(), image.getWidth(), decrypted, "m1-splash-shares-combined");


        //Ensure that the shares have different RGB values than the original, and that the combined shares are equal
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
    }

    //Converts a byte array into an image.  Each pixel is represented by 4 bytes in the ARGB array.
    public void outputBytesToImage(int imageHeight, int imageWidth, byte[] bytes, String imageName) throws IOException {
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
        ImageIO.write(newImage, "png", outputfile);
    }

    //Converts a 4 byte array into an integer.  Used in image testing.
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
}
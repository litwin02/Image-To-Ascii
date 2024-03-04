package org.litwin02;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageToAscii {
    BufferedImage loadedImage;
    BufferedImage grayImage;
    char[] asciiChars = {' ', '.', ':', '-', '=', '+', '*', '#', '%', '@'};
    public void loadImage(String path) {
        System.out.println("Loading image from " + path);
        try {
            loadedImage = (ImageIO.read(new File(path)));
            System.out.println("Image loading is done");
        }
        catch (IOException e)
        {
            System.out.println("Cannot read file. Invalid path or system failure.");
        }
    }

    public void modifyImage(){
        //resizing image while maintaining aspect ratio
        int targetWidth = 200;
        int targetHeight = (int)Math.round((double) loadedImage.getHeight()*targetWidth / loadedImage.getWidth());
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        //creating resized copy of image based on loaded image
        resizedImage.createGraphics().drawImage(loadedImage, 0,0, targetWidth, targetHeight, null);

        //convert image to grayscale
        grayImage = new BufferedImage(resizedImage.getWidth(), resizedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        grayImage.createGraphics().drawImage(resizedImage, 0, 0, null);
    }

    public void createAsciiArt(String path) throws IOException {
        loadImage(path);
        modifyImage();
        StringBuilder asciiArt = new StringBuilder();

        for(int y=0; y < grayImage.getHeight(); y++) {
            for (int x = 0; x < grayImage.getWidth(); x++) {
                // this line gets RGB value from (x, y) coordinates - the image was converted to grayscale, so it should have values only from 0-255
                // but getRGB returns RGB value like this: bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue
                // we are interested only in bits 24-31, so we do bitwise AND with 0xFF to get our grayscale
                int pixelValue = grayImage.getRGB(x, y) & 0xFF;
                // by dividing pixelValue / 255, we normalize it, so we get values between 0-1, then by multiplying it by asciiChars.length - 1 we get index
                int index = (int) Math.round((asciiChars.length - 1) * (pixelValue / 255.0));
                asciiArt.append(asciiChars[index]);
            }
            asciiArt.append("\n");
        }
        System.out.println("Created ascii art. Writing to file.");
        Files.write(Paths.get("art.txt"), asciiArt.toString().getBytes());
    }

    public static void main(String[] args){
        ImageToAscii ascii = new ImageToAscii();
        try {
            ascii.createAsciiArt("1000yardstare.jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
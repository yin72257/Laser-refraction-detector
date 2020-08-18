/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laserdetector;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Resu
 */
public class LaserDetector {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("/Users/Resu/Desktop/Middle.png"));//image file name from directory
        } catch (IOException e) {

        }
        int[] frequency = getAllXPositions(img);
        int sum = 0;
        long totalLuminosity = 0;
        for (int i = 0; i < frequency.length; i++) {//calculate weighted average of luminosities
            totalLuminosity += (i + 1) * frequency[i];
            sum += frequency[i];
        }
        int highest = (int) (totalLuminosity / sum);
        try {
            BufferedImage newImage = drawLine(img, highest);
            File outputfile = new File("red.png");
            ImageIO.write(newImage, "png", outputfile);
        } catch (IOException e) {

        }
    }

    private static int[] getAllXPositions(BufferedImage img) {
        int[] pixelFrequency = new int[img.getWidth()];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color c = new Color(img.getRGB(x, y));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                pixelFrequency[x] += gray(red, green, blue);//luminosity computed

            }
        }
        return pixelFrequency;
    }

    private static BufferedImage drawLine(BufferedImage image, int highest) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                bufferedImage.setRGB(x, y, image.getRGB(x, y));
                if (x == highest) {
                    int rgb = new Color(255, 255, 0).getRGB();
                    bufferedImage.setRGB(x, y, rgb);
                }
            }
        }
        return bufferedImage;
    } // sRGB luminance(Y) values
    static double rY = 0.212655;
    static double gY = 0.715158;
    static double bY = 0.072187;

// Inverse of sRGB "gamma" function. (approx 2.2)
    static double inv_gam_sRGB(int ic) {
        double c = ic / 255.0;
        if (c <= 0.04045) {
            return c / 12.92;
        } else {
            return Math.pow(((c + 0.055) / (1.055)), 2.4);
        }
    }

// sRGB "gamma" function (approx 2.2)
    static int gam_sRGB(double v) {
        if (v <= 0.0031308) {
            v *= 12.92;
        } else {
            v = 1.055 * Math.pow(v, 1.0 / 2.4) - 0.055;
        }
        return (int) (v * 255 + 0.5); // This is correct in C++. Other languages may not
        // require +0.5
    }

// GRAY VALUE ("brightness")
    static int gray(int r, int g, int b) {
        return gam_sRGB(
                rY * inv_gam_sRGB(r)
                + gY * inv_gam_sRGB(g)
                + bY * inv_gam_sRGB(b)
        );
    }
}

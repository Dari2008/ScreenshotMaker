package main;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageResizer {

    /**
     * Resize an image to a given width and height.
     * @param original The original BufferedImage
     * @param targetWidth Desired width
     * @param targetHeight Desired height
     * @param keepAspectRatio Whether to preserve the original aspect ratio
     * @return Resized BufferedImage
     */
    public static BufferedImage resize(BufferedImage original, int targetWidth, int targetHeight, boolean keepAspectRatio) {
        if (keepAspectRatio) {
            double aspectRatio = (double) original.getWidth() / original.getHeight();
            if (targetWidth / aspectRatio <= targetHeight) {
                targetHeight = (int) (targetWidth / aspectRatio);
            } else {
                targetWidth = (int) (targetHeight * aspectRatio);
            }
        }

        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return resized;
    }

    /**
     * Resize an image file and save the result to another file.
     * @param inputFile Input image file
     * @param outputFile Output image file
     * @param targetWidth Desired width
     * @param targetHeight Desired height
     * @param keepAspectRatio Whether to preserve aspect ratio
     * @throws IOException if reading or writing fails
     */
    public static void resize(File inputFile, File outputFile, int targetWidth, int targetHeight, boolean keepAspectRatio) throws IOException {
        BufferedImage original = ImageIO.read(inputFile);
        BufferedImage resized = resize(original, targetWidth, targetHeight, keepAspectRatio);
        String format = getFileExtension(outputFile.getName());
        ImageIO.write(resized, format, outputFile);
    }

    private static String getFileExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1) : "png";
    }
}

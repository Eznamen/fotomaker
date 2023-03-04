package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {

    private double maxRatio;
    private int width;
    private int height;
    private double ratio;

    TextColorSchema schema = new Schema();

    public Converter() {
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));
        int w = img.getWidth();
        int h = img.getHeight();

        if (getMaxRatio() != 0) {
            if (w != 0) {
                if (h != 0) {
                    ratio = (double) Math.max(w, h) / Math.min(w, h);
                    if (ratio > maxRatio) {
                        throw new BadImageSizeException(ratio, maxRatio);
                    }
                }
            }
        }

        if (getWidth() != 0) {
            if (w > getWidth()) {
                ratio = (double) (w / getWidth());
                w = width;
                h = (int) (h / ratio);
            }

        }
        if (getHeight() != 0) {
            if (h > getHeight()) {
                ratio = (double) (h / getHeight());
                h = height;
                w = (int) (w / ratio);
            }
        }

        int newWidth = w;
        int newHeight = h;
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        WritableRaster bwRaster = bwImg.getRaster();
        int[] pixel = new int[3];

        StringBuilder stringBuilder = new StringBuilder();
        for (int x = 0; x < newHeight; x++) {
            for (int y = 0; y < newWidth; y++) {
                int color = bwRaster.getPixel(y, x, pixel)[0];
                stringBuilder.append(schema.convert(color));
                stringBuilder.append(schema.convert(color));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    public double getMaxRatio() {
        return maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}
package org.yaman;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@AllArgsConstructor
@Data
public class ImgResizer implements Runnable{

    private File photo;
    private int newWidth;
    private String dstFolder;
    private static int readyPhotos;

    @Override
    public void run() {
        try {
                BufferedImage img = ImageIO.read(photo);
                if (img == null) {
                    readyPhotos++;
                    return;
                }
                int newHeight = (int) Math.round(img.getHeight() / (img.getWidth() / (double) newWidth));
                BufferedImage newImg = Scalr.resize(img,newWidth,newHeight);
                File newPhoto = new File(dstFolder + "/" + photo.getName());
                ImageIO.write(newImg, "jpg", newPhoto);
                readyPhotos++;
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public static int getReadyPhotosCount() {
        return readyPhotos;
    }

    public static void resetReadyPhotosCount() {
        ImgResizer.readyPhotos = 0;
    }
}

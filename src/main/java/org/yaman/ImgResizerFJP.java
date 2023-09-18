package org.yaman;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

@AllArgsConstructor
@Data
public class ImgResizerFJP extends RecursiveAction {

    private File[] photos;
    private int newWidth;
    private String dstFolder;
    private static int readyPhotos;

    public static int getReadyPhotosCount() {
        return readyPhotos;
    }

    public static void resetReadyPhotosCount() {
        ImgResizerFJP.readyPhotos = 0;
    }

    @Override
    protected void compute() {
        if (photos.length == 0) {
            System.out.println("Array is empty.");
            return;
        }
        if (photos.length == 1) {
            File photo = photos[0];
            resize(photo);
            readyPhotos++;
        }
        if (photos.length > 1) {
            int numberOfTasks = Math.max(Runtime.getRuntime().availableProcessors() - 2, 1);
            int filesPerTask = (int) Math.ceil((double) photos.length / numberOfTasks);

            ForkJoinTask<?>[] tasks = new ForkJoinTask<?>[numberOfTasks];

            for (int i = 0; i < numberOfTasks ; i++) {
                int start = i * filesPerTask;
                int end = Math.min((i + 1) * filesPerTask, photos.length);
                File[] taskFiles = Arrays.copyOfRange(photos, start, end);
                ImgResizerFJP task = new ImgResizerFJP(taskFiles,newWidth,dstFolder);
                tasks[i] = task.fork();
            }

            for (int i = 0; i < numberOfTasks; i++) {
                tasks[i].join();
            }
        }
    }

    private void resize(File photo) {
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
}

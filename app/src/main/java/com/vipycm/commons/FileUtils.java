package com.vipycm.commons;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by mao on 17-4-21.
 */

public class FileUtils {

    public static void saveImage(String path, final Bitmap image) {
        File file = new File(path);
        file.getParentFile().mkdirs();
        try {
            image.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

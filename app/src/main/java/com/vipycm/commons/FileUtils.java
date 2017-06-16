package com.vipycm.commons;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.vipycm.mao.MaoApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public static String readRaw(final int rawId) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(MaoApp.getContext().getResources().openRawResource(rawId)));

        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return sb.toString();
    }

    public static Bitmap readBitmapFromAssets(String fileName) {
        Bitmap bitmap = null;
        AssetManager am = MaoApp.getContext().getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

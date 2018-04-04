package com.vipycm.commons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.vipycm.mao.MaoApp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by mao on 17-4-21.
 */

public class FileUtils {

    private static Context getContext() {
        return MaoApp.getContext();
    }

    public static void saveBitmapToFile(Bitmap bitmap, String path) {
        File file = new File(path);
        file.getParentFile().mkdirs();
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fileOutputStream);
        }
    }

    public static String readFileToString(String filepath) {
        try {
            return readStreamToString(new FileInputStream(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] readFileToByteArray(String filepath) {
        try {
            return readStreamToByteArray(new FileInputStream(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static String readAssetToString(String assetPath) {
        try {
            return readStreamToString(getContext().getAssets().open(assetPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] readAssetToByteArray(String assetPath) {
        try {
            return readStreamToByteArray(getContext().getAssets().open(assetPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static Bitmap readAssetToBitmap(String assetPath) {
        InputStream inputStream = null;
        try {
            inputStream = getContext().getResources().getAssets().open(assetPath);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
        }
        return null;
    }

    public static String readRawToString(int rawId) {
        try {
            return readStreamToString(getContext().getResources().openRawResource(rawId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readStreamToString(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int read;
            while ((read = bufferedReader.read(buffer, 0, 1024)) > 0) {
                sb.append(buffer, 0, read);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
        }
        return "";
    }

    public static byte[] readStreamToByteArray(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int count;
        try {
            while ((count = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, count);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(byteArrayOutputStream);
        }
        return new byte[0];
    }

    public static void close(Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

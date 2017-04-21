package com.vipycm.mao.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.vipycm.mao.camera.filter.CameraFilter;

import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

/**
 * 显示相机预览界面
 * Created by mao on 17-4-21.
 */

public class CameraView extends GLSurfaceView {

    private Camera mCamera;
    private CameraRenderer mRenderer;

    private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;

    public CameraView(Context context) {
        super(context);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        mRenderer = new CameraRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void setFilter(CameraFilter filter) {
        mRenderer.setFilter(filter);
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        setUpCamera();
    }

    @Override
    public synchronized void onPause() {
        releaseCamera();
        super.onPause();
    }

    private void setUpCamera() {
        if (mCamera == null) {
            mCamera = Camera.open(mCameraId);
            mRenderer.setRotation(90, mCameraId == CameraInfo.CAMERA_FACING_FRONT, false);
            mRenderer.setUpSurfaceTexture(mCamera);
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void switchCamera() {
        releaseCamera();
        mCameraId = (mCameraId + 1) % Camera.getNumberOfCameras();
        setUpCamera();
    }

    public void capture(final ICaptureCallback callback) {
        if (callback == null) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                callback.onCapture(capture());
            }
        }.start();
    }

    /**
     * Capture the current image with the size as it is displayed and retrieve it as Bitmap.
     *
     * @return current output as Bitmap
     * @throws InterruptedException
     */
    public Bitmap capture() {
        final Semaphore waiter = new Semaphore(0);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        // Take picture on OpenGL thread
        final int[] pixelMirroredArray = new int[width * height];
        mRenderer.runOnDrawEnd(new Runnable() {
            @Override
            public void run() {
                final IntBuffer pixelBuffer = IntBuffer.allocate(width * height);
                GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
                int[] pixelArray = pixelBuffer.array();

                // Convert upside down mirror-reversed image to right-side up normal image.
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        pixelMirroredArray[(height - i - 1) * width + j] = pixelArray[i * width + j];
                    }
                }
                waiter.release();
            }
        });
        requestRender();
        try {
            waiter.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(pixelMirroredArray));
        return bitmap;
    }

    public interface ICaptureCallback {
        void onCapture(Bitmap bitmap);
    }
}

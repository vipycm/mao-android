package com.vipycm.mao.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.camera.filter.CameraFilter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by mao on 17-4-20.
 */

public class CameraRenderer implements GLSurfaceView.Renderer, Camera.PreviewCallback {

    private MaoLog log = MaoLog.getLogger(getClass().getSimpleName());
    public static final int NO_IMAGE = -1;

    private int mGLTextureId = NO_IMAGE;
    private FloatBuffer mGLCubeBuffer;
    private FloatBuffer mGLTextureBuffer;
    private IntBuffer mGLRgbBuffer;

    //界面大小
    private int mOutputWidth;
    private int mOutputHeight;
    //相机预览大小
    private int mImageWidth;
    private int mImageHeight;
    private final Queue<Runnable> mRunOnDraw = new LinkedList<>();
    private final Queue<Runnable> mRunOnDrawEnd = new LinkedList<>();

    private SurfaceTexture mSurfaceTexture;
    private CameraFilter mFilter;

    public static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    public CameraRenderer() {
        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);
        float[] textureCords = TextureRotationUtil.getRotation(90, true, false);
        mGLTextureBuffer = ByteBuffer.allocateDirect(textureCords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.put(textureCords).position(0);
    }

    public void setFilter(CameraFilter filter) {
        mFilter = filter;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        log.i("onSurfaceCreated");
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        mFilter.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        log.i("onSurfaceChanged w:"+width+" h:"+height);
        mOutputWidth = width;
        mOutputHeight = height;
        GLES20.glViewport(0, 0, width, height);
        GLES20.glUseProgram(mFilter.getProgram());
        mFilter.onOutputSizeChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        log.d("onDrawFrame");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        runAll(mRunOnDraw);
        mFilter.onDraw(mGLTextureId, mGLCubeBuffer, mGLTextureBuffer);
        runAll(mRunOnDrawEnd);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
        }
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
        log.d("onPreviewFrame");
        final Size previewSize = camera.getParameters().getPreviewSize();
        if (mGLRgbBuffer == null) {
            mGLRgbBuffer = IntBuffer.allocate(previewSize.width * previewSize.height);
        }
        if (mRunOnDraw.isEmpty()) {
            runOnDraw(new Runnable() {
                @Override
                public void run() {
                    YuvDecoder.YUVtoRBGA(data, previewSize.width, previewSize.height, mGLRgbBuffer.array());
                    mGLTextureId = OpenGlUtils.loadTexture(mGLRgbBuffer, previewSize, mGLTextureId);
                    camera.addCallbackBuffer(data);

                    if (mImageWidth != previewSize.width) {
                        mImageWidth = previewSize.width;
                        mImageHeight = previewSize.height;
                        adjustImageScaling();
                    }
                }
            });
        }
    }

    public synchronized void setRotation(final int rotation, final boolean flipHorizontal, final boolean flipVertical) {
        float[] textureCords = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);
        mGLTextureBuffer = ByteBuffer.allocateDirect(textureCords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.put(textureCords).position(0);
    }

    private void adjustImageScaling() {
        //TODO
    }

    public void setUpSurfaceTexture(final Camera camera) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                try {
                    Camera.Parameters param = camera.getParameters();
                    param.setPreviewSize(1280, 720);//TODO
                    if (param.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        param.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    }
                    camera.setParameters(param);
                    int[] textures = new int[1];
                    GLES20.glGenTextures(1, textures, 0);
                    mSurfaceTexture = new SurfaceTexture(textures[0]);
                    camera.setPreviewTexture(mSurfaceTexture);
                    camera.setPreviewCallback(CameraRenderer.this);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
    }

    protected void runOnDrawEnd(final Runnable runnable) {
        synchronized (mRunOnDrawEnd) {
            mRunOnDrawEnd.add(runnable);
        }
    }
}

package com.vipycm.mao.cameranew;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera.CameraInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.camera.TextureRotationUtil;
import com.vipycm.mao.cameranew.filter.CameraFilter;
import com.vipycm.mao.cameranew.filter.CameraFilterGroup;
import com.vipycm.mao.cameranew.filter.CameraInputFilter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 相机预览界面基类
 * Created by mao on 17-6-13.
 */

public class BaseCameraView extends GLSurfaceView implements Renderer, OnFrameAvailableListener {

    private MaoLog mLog = MaoLog.getLogger(getClass().getSimpleName());

    protected int mCameraId = CameraInfo.CAMERA_FACING_FRONT;

    protected FloatBuffer mCubeBuffer;
    protected FloatBuffer mTextureBuffer;

    protected int mTextureId = 0;
    private boolean mNeedUpdateTexImage = false;
    protected SurfaceTexture mSurfaceTexture;
    private CameraInputFilter mCameraInputFilter = new CameraInputFilter();

    protected CameraFilterGroup mCameraFilterGroup;

    protected int mSurfaceWidth;
    protected int mSurfaceHeight;

    private final Queue<Runnable> mRunOnDraw = new LinkedList<>();
    private final Queue<Runnable> mRunOnDrawEnd = new LinkedList<>();

    public BaseCameraView(Context context) {
        super(context);
        init();
    }

    public BaseCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        float[] cube = TextureRotationUtil.CUBE;
        mCubeBuffer = ByteBuffer.allocateDirect(cube.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeBuffer.put(cube);

        float[] textureCords = TextureRotationUtil.getRotation(90, mCameraId == CameraInfo.CAMERA_FACING_FRONT, true);
        mTextureBuffer = ByteBuffer.allocateDirect(textureCords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuffer.put(textureCords);

        List<CameraFilter> filters = new ArrayList<>();
        filters.add(mCameraInputFilter);
        filters.addAll(initFilters());
        mCameraFilterGroup = new CameraFilterGroup(filters);
    }

    public List<CameraFilter> initFilters() {
        return new ArrayList<>();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mLog.i("onSurfaceCreated");
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        mCameraFilterGroup.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mLog.i("onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        mCameraFilterGroup.initFrameBuffer(width, height);
    }

    @Override
    public final void onDrawFrame(GL10 gl) {
        mLog.i("onDrawFrame");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        synchronized (this) {
            if (mNeedUpdateTexImage && mSurfaceTexture != null) {
                mSurfaceTexture.updateTexImage();
            }
            mNeedUpdateTexImage = false;
        }
        runAll(mRunOnDraw);
        if (mTextureId != 0) {
//            mSurfaceTexture.getTransformMatrix(mCameraInputFilter.getMVPMatrix());
            mCameraFilterGroup.draw(mTextureId, mCubeBuffer, mTextureBuffer);
        }
        runAll(mRunOnDrawEnd);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        mCameraFilterGroup.destroy();
    }

    @Override
    public synchronized void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mLog.i("onFrameAvailable");
        mNeedUpdateTexImage = true;
        requestRender();
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

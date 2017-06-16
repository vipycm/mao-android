package com.vipycm.mao.cameranew.filter;

import android.opengl.GLES20;

import com.vipycm.mao.cameranew.OpenGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mao on 17-6-14.
 */

public class CameraFilterGroup extends CameraFilter {

    private List<CameraFilter> mFilters = new ArrayList<>();

    private FloatBuffer mCubeBuffer;
    private FloatBuffer mTextureBuffer;

    public CameraFilterGroup(List<CameraFilter> filters) {
        mFilters = filters;

        float[] cube = OpenGLUtils.CUBE;
        mCubeBuffer = ByteBuffer.allocateDirect(cube.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeBuffer.put(cube);

        float[] textureCords = OpenGLUtils.TEXTURE_NO_ROTATION;
        mTextureBuffer = ByteBuffer.allocateDirect(textureCords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuffer.put(textureCords);
    }

    @Override
    public void init() {
        for (CameraFilter filter : mFilters) {
            filter.init();
        }
    }

    @Override
    public void initFrameBuffer(int width, int height) {
        for (CameraFilter filter : mFilters) {
            filter.initFrameBuffer(width, height);
        }
    }

    @Override
    public void draw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {

        int texture = textureId;
        int size = mFilters.size();
        for (int i = 0; i < size - 1; i++) {
            CameraFilter filter = mFilters.get(i);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, filter.mFrameBuffer);
            GLES20.glClearColor(0, 0, 0, 0);
            filter.draw(texture, mCubeBuffer, mTextureBuffer);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            texture = filter.mFrameBufferTexture;
        }
        CameraFilter filter = mFilters.get(size - 1);
        filter.draw(texture, cubeBuffer, textureBuffer);
    }

    @Override
    public void destroy() {
        for (CameraFilter filter : mFilters) {
            filter.destroy();
        }
    }
}

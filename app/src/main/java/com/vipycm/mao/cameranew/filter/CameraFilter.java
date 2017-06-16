package com.vipycm.mao.cameranew.filter;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.vipycm.mao.cameranew.OpenGLUtils;

import java.nio.FloatBuffer;

/**
 * Created by mao on 17-6-13.
 */

public class CameraFilter {
    public static final String NO_FILTER_VERTEX_SHADER_CODE = "" +
            "attribute vec4 aPosition;\n" +
            "attribute vec2 aTextureCoordinate;\n" +
            "varying vec2 vTextureCoordinate;\n" +
            "uniform mat4 uMVPMatrix;\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = uMVPMatrix * aPosition;\n" +
            "    vTextureCoordinate = aTextureCoordinate;\n" +
            "}\n";

    public static final String NO_FILTER_FRAGMENT_SHADER_CODE = "" +
            "precision mediump float;\n" +
            "varying vec2 vTextureCoordinate;\n" +
            "uniform sampler2D uTexture;\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = texture2D(uTexture,vTextureCoordinate);\n" +
            "}\n";

    private final String mVertexShaderCode;
    private final String mFragmentShaderCode;

    protected int mWidth;
    protected int mHeight;

    protected int mProgram = 0;
    protected int mAPositionHandle;
    protected int mATextureCoordinateHandle;
    protected int mUTextureHandle;
    protected int mUMVPMatrixHandle;

    protected int mFrameBuffer = 0;
    protected int mFrameBufferTexture = 0;

    protected float[] mMVPMatrix = new float[16];

    public CameraFilter() {
        this(NO_FILTER_VERTEX_SHADER_CODE, NO_FILTER_FRAGMENT_SHADER_CODE);
    }

    public CameraFilter(String vertexShaderCode, String fragmentShaderCode) {
        mVertexShaderCode = vertexShaderCode;
        mFragmentShaderCode = fragmentShaderCode;

        Matrix.setIdentityM(mMVPMatrix, 0);
    }

    public void init() {
        mProgram = OpenGLUtils.loadProgram(mVertexShaderCode, mFragmentShaderCode);
        mAPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mATextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoordinate");
        mUTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
        mUMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void initFrameBuffer(int width, int height) {
        mWidth = width;
        mHeight = height;

        deleteFrameBuffer();

        int[] frameBuffers = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffers, 0);
        mFrameBuffer = frameBuffers[0];

        int[] frameBufferTextures = new int[1];
        GLES20.glGenTextures(1, frameBufferTextures, 0);
        mFrameBufferTexture = frameBufferTextures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTexture);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mFrameBufferTexture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void draw(int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mProgram);

        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mAPositionHandle, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mAPositionHandle);

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mATextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mATextureCoordinateHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mUTextureHandle, 0);

        GLES20.glUniformMatrix4fv(mUMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mAPositionHandle);
        GLES20.glDisableVertexAttribArray(mATextureCoordinateHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        OpenGLUtils.checkError("draw");
    }

    public void destroy() {
        if (mProgram != 0) {
            GLES20.glDeleteProgram(mProgram);
        }
        deleteFrameBuffer();
    }

    public void deleteFrameBuffer() {
        if (mFrameBuffer != 0) {
            int[] frameBuffers = {mFrameBuffer};
            GLES20.glDeleteFramebuffers(1, frameBuffers, 0);
        }
        if (mFrameBuffer != 0) {
            int[] frameBufferTextures = {mFrameBufferTexture};
            GLES20.glDeleteTextures(1, frameBufferTextures, 0);
        }
    }
}

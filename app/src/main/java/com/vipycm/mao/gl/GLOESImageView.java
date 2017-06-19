package com.vipycm.mao.gl;

import android.graphics.PointF;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.vipycm.mao.cameranew.OpenGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by mao on 17-5-5.
 */

public class GLOESImageView extends GLView {

    protected FloatBuffer mCubeBuffer;
    protected FloatBuffer mTextureBuffer;
    protected FloatBuffer mMVPBuffer;

    protected float[] mMVPMatrix = new float[16];

    protected int mResource = 0;
    protected int mTexture = 0;

    protected ImageProgram mProgram;

    private static final float[] CUBE = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
    };
    private static final float[] TEXTURE = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };

    public GLOESImageView() {
        mCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//        mCubeBuffer.put(CUBE);

        mTextureBuffer = ByteBuffer.allocateDirect(TEXTURE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuffer.put(TEXTURE);

        float[] mvp = new float[16];
        Matrix.setIdentityM(mvp, 0);
        mMVPBuffer = ByteBuffer.allocateDirect(mvp.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mMVPBuffer.put(mvp);
    }

    public void setImageResource(int resId) {
        mTexture = 0;
        mResource = resId;
    }

    public void setImageTexture(int textureId) {
        mTexture = textureId;
    }

    @Override
    public void measure(PointF viewportSize, PointF viewSize, PointF position) {
        super.measure(viewportSize, viewSize, position);
    }

    public void draw() {

        if (mTexture == 0) {
            mTexture = OpenGLUtils.loadTexture(mResource);
        }

        if (mProgram == null) {
            mProgram = new ImageProgram();
        }

        setCube();

        float translateX = (mPosition.x + mTranslationX) * 2.0f / mViewportSize.x;
        float translateY = (-mPosition.y - mTranslationY) * 2.0f / mViewportSize.y;
        float[] mvp = new float[16];
        Matrix.setIdentityM(mvp, 0);
        Matrix.translateM(mMVPMatrix, 0, mvp, 0, translateX, translateY, 0);
        mMVPBuffer.position(0);
        mMVPBuffer.put(mMVPMatrix).position(0);

        GLES20.glUseProgram(mProgram.mProgram);

        mCubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mProgram.mAPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mCubeBuffer);
        GLES20.glEnableVertexAttribArray(mProgram.mAPositionHandle);

        mTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(mProgram.mATextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(mProgram.mATextureCoordinateHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTexture);
        GLES20.glUniform1i(mProgram.mUTextureHandle, 0);

        mMVPBuffer.position(0);
        GLES20.glUniformMatrix4fv(mProgram.mUMVPMatrixHandle, 1, false, mMVPBuffer);

        GLES20.glUniform1f(mProgram.mUOpacityHandle, mOpacity);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mProgram.mAPositionHandle);
        GLES20.glDisableVertexAttribArray(mProgram.mATextureCoordinateHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    private void setCube() {
        float cubeWidth = mViewSize.x * 2.0f / mViewportSize.x;
        float cubeHeight = mViewSize.y * 2.0f / mViewportSize.y;
        float left = -1.0f;
        float top = 1.0f;
        float right = cubeWidth - 1.0f;
        float bottom = 1.0f - cubeHeight;

        if (mRotationX > 0 && mRotationX <= 180) {
            top -= cubeHeight * mRotationX / 180;
            bottom += cubeHeight * mRotationX / 180;
        } else if (mRotationX > 180 && mRotationX <= 360) {
            float tempTop = bottom + cubeHeight * (mRotationX - 180) / 180;
            float tempBottom = top - cubeHeight * (mRotationX - 180) / 180;
            top = tempTop;
            bottom = tempBottom;
        }

        if (mRotationY > 0 && mRotationY <= 180) {
            left += cubeWidth * mRotationY / 180;
            right -= cubeWidth * mRotationY / 180;
        } else if (mRotationY > 180 && mRotationY <= 360) {
            float tempLeft = right - cubeWidth * (mRotationY - 180) / 180;
            float tempRight = left + cubeWidth * (mRotationY - 180) / 180;
            left = tempLeft;
            right = tempRight;
        }

        float[] cube = {
                left, bottom,
                right, bottom,
                left, top,
                right, top
        };
        mCubeBuffer.position(0);
        mCubeBuffer.put(cube);
    }

    public static class ImageProgram {
        private int mProgram;
        private int mAPositionHandle;
        private int mATextureCoordinateHandle;
        private int mUTextureHandle;
        private int mUMVPMatrixHandle;
        private int mUOpacityHandle;

        private static final String VERTEX_SHADER_CODE = "" +
                "attribute vec4 aPosition;\n" +
                "attribute vec2 aTextureCoordinate;\n" +
                "varying vec2 vTextureCoordinate;\n" +
                "uniform mat4 uMVPMatrix;\n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = uMVPMatrix * aPosition;\n" +
                "    vTextureCoordinate = aTextureCoordinate;\n" +
                "}\n";

        private static final String FRAGMENT_SHADER_CODE = "" +
                "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTextureCoordinate;\n" +
                "uniform samplerExternalOES uTexture;\n" +
                "uniform float uOpacity;\n" +
                "void main()\n" +
                "{\n" +
                "    gl_FragColor = texture2D(uTexture,vTextureCoordinate);\n" +
                "    gl_FragColor.a *= uOpacity;\n" +
                "}\n";

        public ImageProgram() {
            mProgram = OpenGLUtils.loadProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);

            mAPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            mATextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoordinate");
            mUTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
            mUMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            mUOpacityHandle = GLES20.glGetUniformLocation(mProgram, "uOpacity");
        }

    }
}

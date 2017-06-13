package com.vipycm.mao.cameranew.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.vipycm.mao.camera.OpenGlUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by mao on 17-6-13.
 */

public class CameraFilter {

    private FloatBuffer mMVPBuffer;
    private ImageProgram program;
    protected float mOpacity = 1.0f;

    public CameraFilter() {
        mMVPBuffer = ByteBuffer.allocateDirect(OpenGlUtils.IDENTITY_MATRIX.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mMVPBuffer.put(OpenGlUtils.IDENTITY_MATRIX);
    }

    public void loadProgram() {
        program = new ImageProgram();
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {

        GLES20.glUseProgram(program.mProgram);

        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(program.mAPositionHandle, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(program.mAPositionHandle);

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(program.mATextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(program.mATextureCoordinateHandle);

        GLES20.glActiveTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(program.mUTextureHandle, 0);

        mMVPBuffer.position(0);
        GLES20.glUniformMatrix4fv(program.mUMVPMatrixHandle, 1, false, mMVPBuffer);

        GLES20.glUniform1f(program.mUOpacityHandle, mOpacity);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(program.mAPositionHandle);
        GLES20.glDisableVertexAttribArray(program.mATextureCoordinateHandle);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
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
            mProgram = OpenGlUtils.loadProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);

            mAPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            mATextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoordinate");
            mUTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
            mUMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            mUOpacityHandle = GLES20.glGetUniformLocation(mProgram, "uOpacity");
        }

    }
}

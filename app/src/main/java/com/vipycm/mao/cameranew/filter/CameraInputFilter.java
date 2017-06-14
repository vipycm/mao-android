package com.vipycm.mao.cameranew.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * 绘制将相机预览的SurfaceTexture
 * Created by mao on 17-6-14.
 */

public class CameraInputFilter extends CameraFilter {

    private static final String FRAGMENT_SHADER_CODE = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 vTextureCoordinate;\n" +
            "uniform samplerExternalOES uTexture;\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = texture2D(uTexture,vTextureCoordinate);\n" +
            "}\n";

    public CameraInputFilter() {
        super(NO_FILTER_VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
    }

    public float[] getMVPMatrix() {
        return mMVPMatrix;
    }

    @Override
    public void draw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mProgram);

        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mAPositionHandle, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mAPositionHandle);

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mATextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mATextureCoordinateHandle);

        GLES20.glActiveTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(mUTextureHandle, 0);

        GLES20.glUniformMatrix4fv(mUMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mAPositionHandle);
        GLES20.glDisableVertexAttribArray(mATextureCoordinateHandle);
    }
}

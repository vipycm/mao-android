package com.vipycm.mao.camera;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * Created by yangchangmao on 17-4-20.
 */

public class CameraFilter {
    private final String fss =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "uniform samplerExternalOES inputImageTexture;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(inputImageTexture,textureCoordinate);\n" +
                    "}";

    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    protected int mGLProgId;
    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;
    private boolean mIsInitialized;

    private final String mVertexShader;
    private final String mFragmentShader;

    public CameraFilter() {
        mVertexShader = NO_FILTER_VERTEX_SHADER;
//        mFragmentShader = NO_FILTER_FRAGMENT_SHADER;
        mFragmentShader = fss;
    }

    public void init() {
        mGLProgId = OpenGlUtils.loadProgram(mVertexShader, mFragmentShader);
        mGLAttribPosition = GLES20.glGetAttribLocation(mGLProgId, "position");
        mGLUniformTexture = GLES20.glGetUniformLocation(mGLProgId, "inputImageTexture");
        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mGLProgId, "inputTextureCoordinate");
        mIsInitialized = true;
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {
        if (!mIsInitialized) {
            return;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mGLProgId);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(mGLUniformTexture, 0);

        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glFlush();
    }
}

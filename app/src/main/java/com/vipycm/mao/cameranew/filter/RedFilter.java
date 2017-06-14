package com.vipycm.mao.cameranew.filter;

/**
 * Created by mao on 17-6-14.
 */

public class RedFilter extends CameraFilter {

    private static final String FRAGMENT_SHADER_CODE = "" +
            "precision mediump float;\n" +
            "varying vec2 vTextureCoordinate;\n" +
            "uniform sampler2D uTexture;\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = texture2D(uTexture,vTextureCoordinate);\n" +
            "    gl_FragColor.r = 1.0;\n" +
            "}\n";

    public RedFilter() {
        super(NO_FILTER_VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
    }
}

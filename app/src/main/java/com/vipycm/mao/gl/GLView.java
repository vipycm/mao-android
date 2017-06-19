package com.vipycm.mao.gl;

import android.graphics.PointF;

/**
 * Created by mao on 17-5-9.
 */

public class GLView {
    protected PointF mViewportSize = new PointF(0.0f, 0.0f);
    protected PointF mViewSize = new PointF(0.0f, 0.0f);
    protected PointF mPosition = new PointF(0.0f, 0.0f);

    protected float mTranslationX = 0.0f;
    protected float mTranslationY = 0.0f;
    protected float mTranslationZ = 0.0f;

    protected float mRotationX = 0.0f;
    protected float mRotationY = 0.0f;
    protected float mRotationZ = 1.0f;

    protected float mOpacity = 1.0f; //不透明度 0.0(全透明) - 1.0(不透明)

    protected float mPadding = 0.0f;

    public void draw() {

    }

    public void measure(PointF viewportSize, PointF viewSize, PointF position) {
        setViewportSize(viewportSize);
        setViewSize(viewSize);
        setPosition(position);
    }

    public PointF getViewportSize() {
        return mViewportSize;
    }

    public void setViewportSize(PointF viewportSize) {
        mViewportSize.x = viewportSize.x;
        mViewportSize.y = viewportSize.y;
    }

    public PointF getViewSize() {
        return mViewSize;
    }

    public void setViewSize(PointF viewSize) {
        mViewSize.x = viewSize.x;
        mViewSize.y = viewSize.y;
    }

    public PointF getPosition() {
        return mPosition;
    }

    public void setPosition(PointF position) {
        mPosition.x = position.x;
        mPosition.y = position.y;
    }

    public void setPosition(float left, float top) {
        mPosition.x = left;
        mPosition.y = top;
    }

    public float getTranslationX() {
        return mTranslationX;
    }

    public void setTranslationX(float translationX) {
        mTranslationX = translationX;
    }

    public float getTranslationY() {
        return mTranslationY;
    }

    public void setTranslationY(float translationY) {
        mTranslationY = translationY;
    }

    public float getTranslationZ() {
        return mTranslationZ;
    }

    public void setTranslationZ(float translationZ) {
        mTranslationZ = translationZ;
    }

    public void setRotationX(float rotationX) {
        mRotationX = rotationX % 360;
    }

    public void setRotationY(float rotationY) {
        mRotationY = rotationY % 360;
    }

    public void setRotationZ(float rotationZ) {
        mRotationZ = rotationZ % 360;
    }

    public void setOpacity(float opacity) {
        mOpacity = opacity;
    }

    public final float getLeft() {
        return mPosition.x;
    }

    public final float getTop() {
        return mPosition.y;
    }

    public void setPadding(float padding) {
        mPadding = padding;
    }

    public float getPadding() {
        return mPadding;
    }
}

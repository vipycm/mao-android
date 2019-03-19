package com.vipycm.mao.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.util.Locale;

import static com.vipycm.mao.BuildConfig.DEBUG;

public class WebActivity extends AppCompatActivity {

    private static final String TAG = "WebActivity";
    private WebViewEx mWebView;

    public static void open(Context context) {
        open(context, "https://www.bituniverse.org");
    }

    public static void open(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = new WebViewEx(this);
        setContentView(mWebView);
        initWebView();

        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
    }

    private void initWebView() {

        WebSettings webSettings = mWebView.getSettings();

        // 设置自适应屏幕，两者合用
        // 将图片调整到适合webview的大小
        webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);

        // 缩放操作
        // 支持缩放，默认为true。是下面那个的前提。
        webSettings.setSupportZoom(true);
        // 设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setBuiltInZoomControls(true);
        // 隐藏原生的缩放控件
        webSettings.setDisplayZoomControls(false);

        // 支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        // 设置编码格式
        webSettings.setDefaultTextEncodingName("utf-8");

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(false);
        saveData(webSettings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        // 解决跨域问题
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (DEBUG) Log.d(TAG, String.format(Locale.ENGLISH, "Title(%s)", title));
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (DEBUG)
                    Log.d(TAG, String.format(Locale.ENGLISH, "Progress(%s)", String.valueOf(newProgress)));
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading: " + url);
                if (TextUtils.isEmpty(url) || url.startsWith("http") || url.startsWith("https")) {
                    return false;
                } else {
                    openUrlWithDefaultBrowser(WebActivity.this, url);
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (DEBUG) Log.d(TAG, String.format(Locale.ENGLISH, "onPageStarted(%s)", url));

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (DEBUG) Log.d(TAG, String.format(Locale.ENGLISH, "onPageFinished(%s)", url));
            }
        });

    }

    /**
     * 数据存储
     */
    private void saveData(WebSettings webSettings) {
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        File cacheDir = getCacheDir();
        if (cacheDir != null) {
            String appCachePath = cacheDir.getAbsolutePath();
            webSettings.setDomStorageEnabled(true);
            webSettings.setDatabaseEnabled(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setAppCachePath(appCachePath);
        }
    }

    private static void openUrlWithDefaultBrowser(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

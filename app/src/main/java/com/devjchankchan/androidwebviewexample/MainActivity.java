package com.devjchankchan.androidwebviewexample;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private String myRequestUrl = "http://api.ma.la/androidwebview/";
    private final String[] incidentsFuncList = {
            "setAccessible()",
            "ClassLoader", "getClass()", "getClassLoader()", "loadClass()",
            "Context", "getContext()", "getApplicationContext()", "getBaseContext()"
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_secure:
                    setupWebView(SelectedType.SECURE_INTERFACE);
                    break;
                case R.id.navigation_insecure:
                    setupWebView(SelectedType.INSECURE_INTERFACE);
                    break;
                case R.id.navigation_no_javascript:
                    setupWebView(SelectedType.NO_JAVASCRIPT);
                    break;
            }

            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new InSecureWebAppInterface(), "TEST");
        mWebView.loadUrl("http://api.ma.la/androidwebview/");
    }

    private class InSecureWebAppInterface {
        @JavascriptInterface
        public void onClick() {
    private enum SelectedType {
        SECURE_INTERFACE,
        INSECURE_INTERFACE,
        NO_JAVASCRIPT
    }

    /*
     http://yuki312.blogspot.jp/2011/11/blog-post.html
     */
    private static void dbg(String msg) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        String className = stack[1].getClassName();
        String method = stack[1].getMethodName();
        int line = stack[1].getLineNumber();
        StringBuilder buf = new StringBuilder(60);
        buf.append(msg)
                .append("[")
                // sample.package.ClassName.methodName:1234
                .append(className).append(".").append(method).append(":").append(line)
                .append("]");
        android.util.Log.d("AndroidWebViewExample", buf.toString());
    }


        }
    }
}

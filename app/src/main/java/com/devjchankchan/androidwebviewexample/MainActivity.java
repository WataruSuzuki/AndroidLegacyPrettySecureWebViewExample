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
    private final String myRequestUrl = "http://api.ma.la/androidwebview/";
    private final String checkUrl = "https://www.google.co.jp";
    private final String interfaceName = "MyWebAppInterface";
    private final String[] incidentsFuncList = {
            "searchBoxJavaBridge_",
            "setAccessible()",
            "ClassLoader", "getClass()", "getClassLoader()", "loadClass()",
            "Context", "getContext()", "getApplicationContext()", "getBaseContext()"
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            mWebView.loadUrl("about:blank");
            switch (item.getItemId()) {
                case R.id.navigation_secure:
                    setupWebView(SelectedType.STATUS_SECURE);
                    break;
                case R.id.navigation_insecure:
                    setupWebView(SelectedType.STATUS_INSECURE);
                    break;
                case R.id.navigation_no_javascript:
                    setupWebView(SelectedType.STATUS_NO_JAVASCRIPT);
                    break;
            }

            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupWebView(SelectedType.STATUS_SECURE);
    }

    private void setupWebView(SelectedType type) {
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.clearCache(true);

        mWebView.addJavascriptInterface(new MyWebAppInterface(), interfaceName);
        mWebView.getSettings().setJavaScriptEnabled(true);
        switch (type) {
            case STATUS_SECURE:
                setupSecureWebView();
                break;

            case STATUS_INSECURE:
                mWebView.setWebViewClient(null);
                break;

            default:
                mWebView.getSettings().setJavaScriptEnabled(false);
        }
        mWebView.loadUrl(myRequestUrl);
    }

    private void setupSecureWebView() {
        removeInsidents(mWebView);
        mWebView.setWebViewClient(new MyWebViewClient());
    }

    private void removeInsidents(WebView webView) {
        for (String insidentsFuncStr:
                incidentsFuncList) {
            webView.removeJavascriptInterface(insidentsFuncStr);
        }
    }

    private class MyWebAppInterface {
        @JavascriptInterface
        public void onClick() {}
    }

    private enum SelectedType {
        STATUS_SECURE,
        STATUS_INSECURE,
        STATUS_NO_JAVASCRIPT
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            dbg("(・A・)!!");
            view.removeJavascriptInterface(interfaceName);
            handler.cancel();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (detectUnknownUrl(url, view)) {
                //do nothing
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (detectUnknownUrl(url, view)) {
                //do nothing
            } else {
                super.onPageFinished(view, url);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (detectUnknownUrl(request.getUrl().toString(), view)) {
                return true;
            } else {
                return super.shouldOverrideUrlLoading(view, request);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (detectUnknownUrl(url.toString(), view)) {
                return true;
            } else {
                super.shouldOverrideUrlLoading(view, url);
            }
        }
    }

    /*
     Guard 001: Remove Web App Interface
     */
    private boolean detectUnknownUrl(String urlStr, WebView webView) {
        if (checkUrl.startsWith(urlStr)) {
            dbg("(・A・)!!");
            webView.removeJavascriptInterface(interfaceName);
            return true;
        }
        return false;
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

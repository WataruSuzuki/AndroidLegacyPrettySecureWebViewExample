package com.devjchankchan.androidwebviewexample;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PrettySecureWebActivity extends AppCompatActivity {

    private final String myRequestUrl = "http://api.ma.la/androidwebview/";
    private final String checkUrl = myRequestUrl;//"https://www.google.co.jp";
    private final String interfaceName = "MyWebAppInterface";
    private final String callViewSource = "javascript:window."+ interfaceName + ".viewSource(document.documentElement.outerHTML);";
    private final String[] incidentsFuncList = {
            "accessible", "Accessible",
            "classloader", "ClassLoader",
            "getClass", "getclass", "loadclass", "loadClass",
            "context", "Context"
    };
    private WebView mWebView;
    private Handler mHandler = new Handler();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pretty_secure_web);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.clearCache(true);

        mWebView.addJavascriptInterface(new MyWebAppInterface(), interfaceName);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new PrettySecureWebViewClient());
        mWebView.loadUrl(myRequestUrl);
    }

    /*
     Guard 001: Check request host is mine.
     */
    private boolean detectUnknownUrl(String urlStr, WebView webView) {
        if (!checkUrl.equals(callViewSource) && !checkUrl.startsWith(urlStr)) {
            dbg("(・A・)!!");
            webView.removeJavascriptInterface(interfaceName);
            webView.getSettings().setJavaScriptEnabled(false);
            return true;
        }
        return false;
    }

    /*
     Guard 002: Check dangerous javascript function on html contents.
     */
    private void checkDangerousJavaScriptFunc(String html) {
        for (String insidentsFuncStr :
                incidentsFuncList) {
            if (html.indexOf(insidentsFuncStr) >= 0) {
                dbg("(・A・)!!");
                mWebView.removeJavascriptInterface(interfaceName);
                mWebView.getSettings().setJavaScriptEnabled(false);
                break;
            }
        }
    }

    private class MyWebAppInterface {
        @JavascriptInterface
        public void viewSource(final String html) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    checkDangerousJavaScriptFunc(html);
                }
            });
        }
    }

    private class PrettySecureWebViewClient extends WebViewClient {
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
                view.loadUrl(callViewSource);
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
                return super.shouldOverrideUrlLoading(view, url);
            }
        }
    }
}

package com.taihuoniao.fineix.tv.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by Stephen on 2017/11/9 18:34
 * Email: 895745843@qq.com
 */

public class WebViewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        setContentView(webView);

        webView.loadUrl("http://www.baidu.com");
    }
}

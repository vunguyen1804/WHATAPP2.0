//package com.edm_messenger.app;
//import androidx.appcompat.app.AppCompatActivity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.webkit.WebChromeClient;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.ProgressBar;
//import com.edm_messenger.R;
//
//public class WebviewActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.webview_activity);
//
//    }
//
//    @Override
//    protected void onStart() {
//
//        super.onStart();
//
//        setContentView(R.layout.webview_activity);
//
//        WebView myWebView = (WebView) findViewById(R.id.webview);
//
//        ProgressBar progessBar = (ProgressBar) findViewById(R.id.webview_progress_bar);
//
//        progessBar.setVisibility(View.VISIBLE);
//
//        myWebView.getSettings().setJavaScriptEnabled(true);
//
//        Intent intent = getIntent();
//
//        String url = intent.getStringExtra("URL");
//
//        WebViewClient webViewClient = new WebViewClient();
//
//        myWebView.setWebViewClient(webViewClient);
//
//        myWebView.loadUrl(url);
//
//
//        myWebView.setWebChromeClient(new WebChromeClient(){
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//
//                if(newProgress == 100) {
//
//                    progessBar.setVisibility(View.GONE);
//
//                } else {
//
//                    progessBar.setVisibility(View.VISIBLE);
//
//                }
//
//            }
//        });
//
//    }
//}

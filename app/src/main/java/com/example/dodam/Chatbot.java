package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class Chatbot extends AppCompatActivity {

    private WebView webView;
    private WebSettings webSettings;
    private Button back, to_dodam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

            back = findViewById(R.id.back);
            to_dodam = findViewById(R.id.to_dodam);

            WebView webView = findViewById(R.id.webvw);

            webView.setWebViewClient(new WebViewClient());  // URL 클릭 시, 새 창 열기 없이 웹뷰 내에서 다시 로드
            WebSettings webSettings = webView.getSettings();
            webSettings.setAppCacheEnabled(true);
            webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setAllowFileAccess(true);
            //if SDK version is greater of 19 then activate hardware acceleration otherwise activate software acceleration
            if (Build.VERSION.SDK_INT >= 19) {
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }

            webView.loadUrl("http://172.20.10.4:8080/");

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Chatbot.this, MainScreen.class);
                    startActivity(intent);
                    finish();
                }
            });

            to_dodam.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Chatbot.this, Dodam_Cat.class);
                    startActivity(intent);
                    finish();
                }
            }));
    }
}
package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class Rules extends AppCompatActivity {

    WebView webView;
    public String fileName = "Rulebook.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        // Initialize webview
        webView = (WebView) findViewById(R.id.rulesWebView);
        // Displaying content in WebView from html file that is stored in the assets folder
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/" + fileName);
    }
}
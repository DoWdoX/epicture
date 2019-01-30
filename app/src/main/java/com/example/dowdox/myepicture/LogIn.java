package com.example.dowdox.myepicture;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;


public class LogIn extends AppCompatActivity {
    private WebView mWebView;

    public static String access_token;
    public static String pseudo;
    public static String account_id;
    public static String refresh_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        mWebView = new WebView(this);
        root.addView(mWebView);
        setContentView(root);

        setupWebView();

        mWebView.loadUrl("https://api.imgur.com/oauth2/authorize?client_id=e8eeb2c02e493ed&response_type=token");
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    private void setupWebView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                boolean tokensURL = false;
                if (url.startsWith("https://android")) {
                    tokensURL = true;
                    access_token = url.substring(30, 70);
                    String endUrl = url.substring(182);
                    int index = endUrl.indexOf('&');
                    pseudo = endUrl.substring(0, index);
                    index = endUrl.indexOf('=') + 1;
                    account_id = endUrl.substring(index);
                    //refresh_token = url.substring(237, 276);
                    refresh_token = "";
                    /*AlertDialog.Builder builder= new AlertDialog.Builder(LogIn.this);
                    builder.setMessage(account_id);
                    AlertDialog alert=builder.create();
                    alert.show();*/
                }
                Intent i = new Intent(LogIn.this, nav_bar.class);
                startActivity(i);
                finish();
                return tokensURL;
            }
        });
    }
    public static String getPseudo() {
        return pseudo;
    }
    public static String getAccess_token() {
        return access_token;
    }
    public static String getRefresh_token() {
        return refresh_token;
    }
}
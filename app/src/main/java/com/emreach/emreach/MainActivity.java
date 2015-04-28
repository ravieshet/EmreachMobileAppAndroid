package com.emreach.emreach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends Activity {

    public boolean show_splash;
    ProgressBar progressBar;
    //TextView text;
    ImageView logo;
    WebView myWebView;
    String URL="http://app3.emreach.com/";
    Toast toast;
    //private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        show_splash = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //context = this;
        //text = (TextView) findViewById(R.id.textView1);
        //Typeface face = Typeface.createFromAsset(getAssets(),"fonts/Comfortaa-Bold.ttf");
        //text.setTypeface(face);

        logo = (ImageView) findViewById(R.id.imageView1);
        //logo.setBackgroundColor(Color.rgb(0, 127, 255));
        logo.setBackgroundColor(Color.rgb(56, 165, 219));

        myWebView = (WebView) findViewById(R.id.webview);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setSupportZoom(false);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);


        myWebView.setWebViewClient(new MyWebViewClient());
        //myWebView.setWebChromeClient(new MyWebChromeClient());
        myWebView.setWebChromeClient(new WebChromeClient());

        if(haveNetworkConnection()){
            myWebView.loadUrl(URL);
        } else {
           toast = Toast.makeText(getApplicationContext(), "Unable to connect to Server. " +
                            "Please check your Internet connection and try again",
                    Toast.LENGTH_LONG);
           toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
           toast.show();
           thread.start();
        }
    }

    Thread thread = new Thread(){
        @Override
        public void run() {
            try {
                Thread.sleep(2500); // As I am using LENGTH_LONG in Toast
                MainActivity.this.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void Visible() {
        if (show_splash) {
            myWebView.setVisibility(View.INVISIBLE);
            //text.setVisibility(View.VISIBLE);
            logo.setVisibility(View.VISIBLE);
            show_splash = false;
            progressBar.setVisibility(View.VISIBLE);
        } else {
            myWebView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void Invisible() {
        myWebView.setVisibility(View.VISIBLE);
        //text.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
/*
    //For JsAlert Testing
    public class MyWebChromeClient extends WebChromeClient {
        //Handle javascript alerts:
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
            Log.d("alert", message);
            //Toast.makeText(context, message, 3000).show();
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            result.confirm();
            return true;
        };
    }
    //For JsAlert Testing
*/
    public class MyWebViewClient extends WebViewClient {
    //public class MyWebViewClient extends WebChromeClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("app3.emreach.com")) {
            //if (Uri.parse(url).getHost().equals("app.emreach.com")) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            if (view.canGoBack()) {
                view.goBack();
            }
            toast = Toast.makeText(getApplicationContext(), "Lost connection to server. " +
                            "Please check your Internet connection and try again",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
            toast.show();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap facIcon) {
            Visible();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String link= view.getUrl();
            String stat="http://app3.emreach.com/Account/Login?ReturnUrl=%2f";
            if (link.equals(stat)){
                myWebView.clearHistory();
            }
            Invisible();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
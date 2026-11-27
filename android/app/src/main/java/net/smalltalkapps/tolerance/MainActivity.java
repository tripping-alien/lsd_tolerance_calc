package net.smalltalkapps.tolerance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends AppCompatActivity {

    public static final String LOCAL_URL = "https://appassets.androidplatform.net/assets/www/index.html";
    public static final String LOCAL_DOMAIN = "appassets.androidplatform.net";
    public static final String TARGET_URL = "https://lsd-tolerance-calc.onrender.com";
    public static final String HOST_DOMAIN = "lsd-tolerance-calc.onrender.com";

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private LinearLayout mOfflineLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new RateHelper(this).notifyAppLaunch();
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webview);
        mProgressBar = findViewById(R.id.progressBar);
        mOfflineLayout = findViewById(R.id.offlineLayout);
        Button retryButton = findViewById(R.id.retryButton);

        // Retry Button
        retryButton.setOnClickListener(v -> {
            mOfflineLayout.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadUrl(LOCAL_URL);
        });

        // Setup WebSettings for performance, storage and instant loading
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        // Enable smooth native touch scrolling & nested scrolling with AppBarLayout
        mWebView.setNestedScrollingEnabled(true);
        mWebView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setHapticFeedbackEnabled(true);

        // Instant Loading via Jetpack WebViewAssetLoader
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        // WebChromeClient for Progress
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        // WebViewClient with asset interception and external link redirection
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri != null && LOCAL_DOMAIN.equals(uri.getHost())) {
                    return assetLoader.shouldInterceptRequest(uri);
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri != null ? uri.getScheme() : null;
                String host = uri != null ? uri.getHost() : null;
                if ("https".equalsIgnoreCase(scheme) && (LOCAL_DOMAIN.equalsIgnoreCase(host) || HOST_DOMAIN.equalsIgnoreCase(host))) {
                    return false; // Stay in WebView
                }
                // Open safe external links (https, http, mailto, tel) in default browser
                if ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme)
                        || "mailto".equalsIgnoreCase(scheme) || "tel".equalsIgnoreCase(scheme)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception ignored) {}
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    // If local loading fails for any reason, try remote URL if connected
                    if (isNetworkAvailable()) {
                        mWebView.loadUrl(TARGET_URL);
                    } else {
                        showOfflineScreen();
                    }
                }
            }
        });

        // Modern OnBackPressed handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    finish();
                }
            }
        });

        // Instant launch from bundled assets or HTTPS deep link!
        mWebView.loadUrl(resolveDeepLinkUrl(getIntent()));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (mWebView != null) {
            mWebView.loadUrl(resolveDeepLinkUrl(intent));
        }
    }

    private String resolveDeepLinkUrl(Intent intent) {
        if (intent == null || !Intent.ACTION_VIEW.equals(intent.getAction()) || intent.getData() == null) {
            return LOCAL_URL;
        }
        Uri data = intent.getData();
        String scheme = data.getScheme();
        String host = data.getHost();
        if ("https".equalsIgnoreCase(scheme) && HOST_DOMAIN.equalsIgnoreCase(host)) {
            return data.toString();
        }
        if ("tolerance".equalsIgnoreCase(scheme) || ("smalltalk".equalsIgnoreCase(scheme) && "tolerance".equalsIgnoreCase(host))) {
            String query = data.getEncodedQuery();
            if (query != null && !query.isEmpty()) {
                return LOCAL_URL + "?" + query;
            }
        }
        return LOCAL_URL;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void showOfflineScreen() {
        mWebView.setVisibility(View.GONE);
        mOfflineLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        if (mWebView != null) mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) mWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}

package ricky.easybrowser.page.webpage;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import ricky.easybrowser.utils.EasyLog;

public class EasyWebView extends WebView {

    public static final String TAG = "EasyWebView";

    public EasyWebView(Context context) {
        this(context, null);
    }

    public EasyWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initSettings();
    }

    private void initSettings() {
        EasyLog.d(TAG, "EasyWebView init");
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
    }
}

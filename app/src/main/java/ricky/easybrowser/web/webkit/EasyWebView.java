package ricky.easybrowser.web.webkit;

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
        super(context, attrs);

        initDefaultSettings();
    }

    public EasyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initDefaultSettings();
    }

    private void initDefaultSettings() {
        EasyLog.d(TAG, "EasyWebView init");
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(false);
        }*/

    }
}

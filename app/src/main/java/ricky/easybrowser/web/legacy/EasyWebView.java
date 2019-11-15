package ricky.easybrowser.web.legacy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.webkit.WebSettings;
import android.webkit.WebView;

import ricky.easybrowser.utils.EasyLog;

public class EasyWebView extends WebView {

    public static final String TAG = "EasyWebView";

    public static final int SCROLL_UP = 1;
    public static final int SCROLL_DOWN = 2;

    private int lastScrollType = 0;

    private boolean animating = false;

    private float initTouchY;
    private float touchUpY;

    private ViewConfiguration vc;
    private int mTouchSlop;

    private WebViewScrollListener webViewScrollListener;

    public EasyWebView(Context context) {
        this(context, null);
    }

    public EasyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            return;
        }
        initDefaultSettings();
        vc = ViewConfiguration.get(getContext());
        mTouchSlop = vc.getScaledTouchSlop();
    }

    public EasyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            return;
        }
        initDefaultSettings();
        vc = ViewConfiguration.get(getContext());
        mTouchSlop = vc.getScaledTouchSlop();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animating) {
            return false;
        }

        final MotionEvent trackEvent = MotionEvent.obtain(event);
        final int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            initTouchY = trackEvent.getY();
        }

        if (action == MotionEvent.ACTION_UP) {
            touchUpY = trackEvent.getY();
            float deltaY = touchUpY - initTouchY;
            EasyLog.i(TAG, "initTouchY: " + initTouchY + ", touchUpY: " + touchUpY + ", deltaY: " + deltaY);
            if (touchUpY > initTouchY && Math.abs(deltaY) > (mTouchSlop * 15)) {  // 增加滑动距离
                if (webViewScrollListener != null && lastScrollType != SCROLL_DOWN) {
                    webViewScrollListener.onScrollDown();
                    lastScrollType = SCROLL_DOWN;
                }
            } else if (touchUpY < initTouchY && Math.abs(deltaY) > mTouchSlop) {
                if (webViewScrollListener != null && lastScrollType != SCROLL_UP) {
                    webViewScrollListener.onScrollUp();
                    lastScrollType = SCROLL_UP;
                }
            }
        }
        trackEvent.recycle();
        return super.onTouchEvent(event);
    }

    public void setWebViewScrollListener(WebViewScrollListener webViewScrollListener) {
        this.webViewScrollListener = webViewScrollListener;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void setAnimating(boolean animating) {
        this.animating = animating;
    }

    interface WebViewScrollListener {
        void onScrollUp();

        void onScrollDown();
    }
}
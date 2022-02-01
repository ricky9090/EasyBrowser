package ricky.easybrowser.web.gecko;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import ricky.easybrowser.contract.IWebView;

public class PageGeckoView extends LinearLayout implements IWebView {

    // TODO implement this class
    public PageGeckoView(Context context) {
        super(context);
    }

    public PageGeckoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageGeckoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnWebInteractListener(OnWebInteractListener listener) {

    }

    @Override
    public OnWebInteractListener getOnWebInteractListener() {
        return null;
    }

    @Override
    public void loadUrl(String url) {

    }

    @Override
    public void goBack() {

    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goForward() {

    }

    @Override
    public boolean canGoForward() {
        return false;
    }

    @Override
    public void releaseSession() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public Bitmap capturePreview() {
        return null;
    }
}

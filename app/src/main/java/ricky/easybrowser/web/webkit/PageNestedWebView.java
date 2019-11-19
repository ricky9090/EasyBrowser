package ricky.easybrowser.web.webkit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;

import java.io.InputStream;
import java.util.Map;

import ricky.easybrowser.R;
import ricky.easybrowser.common.WebConst;
import ricky.easybrowser.entity.bo.TabInfo;
import ricky.easybrowser.entity.dao.History;
import ricky.easybrowser.page.browser.IBrowser;
import ricky.easybrowser.utils.EasyLog;
import ricky.easybrowser.utils.SharedPreferencesUtils;
import ricky.easybrowser.utils.StringUtils;
import ricky.easybrowser.web.IWebView;
import ricky.easybrowser.widget.BrowserNavBar;

public class PageNestedWebView extends LinearLayout implements IWebView {

    private Context mContext;

    private EasyNestedWebView webView;
    private AddressBar addressBar;

    private ImageView goButton;
    private EditText webAddress;
    private ContentLoadingProgressBar progressBar;
    private BrowserNavBar browserNavBar;

    private OnWebInteractListener onWebInteractListener;
    private WebViewClickHandler handler;

    SharedPreferences sp;
    private boolean noPicMode;

    private String hitResultExtra = null;

    public PageNestedWebView(Context context) {
        this(context, null);
    }

    public PageNestedWebView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageNestedWebView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.fragment_web_page_v2, this);
        initViews();
        handler = new WebViewClickHandler(this);
    }

    private void initViews() {
        configureWebView();

        addressBar = findViewById(R.id.web_address_bar);


        goButton = findViewById(R.id.goto_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInputUrl();
            }
        });

        webAddress = findViewById(R.id.page_url_edittext);
        webAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    webAddress.clearFocus();
                    if (getContext() instanceof Activity) {
                        Activity activity = (Activity) getContext();
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
                    }

                    loadInputUrl();
                }
                return false;
            }
        });
        progressBar = findViewById(R.id.web_loading_progress_bar);

        browserNavBar = findViewById(R.id.web_nav_bar);
        browserNavBar.setNavListener(new WebNavListener(getContext()));
    }

    private void configureWebView() {
        webView = findViewById(R.id.page_webview);
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        webView.setOnLongClickListener(new MyWebLongClickListener());
    }

    private void loadInputUrl() {
        if (webAddress.getText() != null) {
            String url = webAddress.getText().toString();
            this.loadUrl(url);
        }
    }

    @Override
    public void loadUrl(String url) {
        updateWebSettings();
        webView.loadUrl(url);
    }

    @Override
    public boolean canGoBack() {
        return webView.canGoBack();
    }

    @Override
    public void goBack() {
        webView.goBack();
    }

    @Override
    public void goForward() {
        webView.goForward();
    }

    @Override
    public boolean canGoForward() {
        return webView.canGoForward();
    }

    @Override
    public void setOnWebInteractListener(OnWebInteractListener listener) {
        this.onWebInteractListener = listener;
    }

    @Override
    public OnWebInteractListener getOnWebInteractListener() {
        return onWebInteractListener;
    }

    @Override
    public void releaseSession() {
        // donothing, for geckoView
    }

    @Override
    public void onResume() {
        webView.onResume();
        webView.resumeTimers();
    }

    @Override
    public void onPause() {
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    public void onDestroy() {
        webView.stopLoading();
        webView.getSettings().setJavaScriptEnabled(false);
        webView.clearHistory();
        webView.clearCache(true);
        webView.loadUrl("about:blank");
        webView.pauseTimers();
        webView.removeAllViews();
        webView.destroy();
        webView = null;
        onWebInteractListener = null;
    }

    private void updateWebSettings() {
        if (sp == null) {
            sp = SharedPreferencesUtils.getSettingSP(getContext());
        }
        WebSettings webSettings = webView.getSettings();
        if (sp != null && webSettings != null) {
            noPicMode = sp.getBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, false);
            webView.getSettings().setBlockNetworkImage(noPicMode);
        }

    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setProgress(0);
                progressBar.hide();
                return;
            }

            if ((newProgress > 0) && (progressBar.getVisibility() == View.INVISIBLE
                    || progressBar.getVisibility() == View.GONE)) {
                progressBar.show();
            }
            progressBar.setProgress(newProgress);

        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webAddress.setText(url);
            if (onWebInteractListener != null) {
                onWebInteractListener.onPageTitleChange(TabInfo.create("", view.getTitle()));
            }

            boolean isBrowserController = mContext instanceof IBrowser;
            if (!isBrowserController) {
                return;
            }
            // FIXME 通过进度 == 100 判断，避免网页重定向生成多条无效历史记录
            // https://stackoverflow.com/questions/3149216/how-to-listen-for-a-webview-finishing-loading-a-url
            if (webView.getProgress() == 100) {
                IBrowser browser = (IBrowser) mContext;
                History history = new History();
                history.setTitle(view.getTitle());
                history.setUrl(url);
                browser.provideHistoryController().addHistory(history);
            }
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            /*try {
                String targetPath = request.getUrl().getPath();
                if (StringUtils.isEmpty(targetPath)) {
                    return super.shouldInterceptRequest(view, request);
                }
                if (noPicMode && isPicResources(targetPath)) {
                    InputStream placeHolderIS = mContext.getAssets().open("emptyplaceholder.png");
                    return new WebResourceResponse("image/png", "UTF-8", placeHolderIS);
                }
            } catch (Exception e) {

            }*/
            return super.shouldInterceptRequest(view, request);
        }


        /*private boolean isPicResources(String path) {
            if (path.endsWith(".jpg")
                    || path.endsWith(".jpeg")
                    || path.endsWith(".png")
                    || path.endsWith(".gif")) {
                return true;
            }
            return false;
        }*/
    }

    class MyWebLongClickListener implements OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            final WebView.HitTestResult result = ((WebView) v).getHitTestResult();
            if (result == null) {
                return false;
            }
            final int type = result.getType();
            final String extra = result.getExtra();
            hitResultExtra = result.getExtra();
            switch (type) {
                case WebView.HitTestResult.IMAGE_TYPE:
                    EasyLog.i("test", "press image: " + extra);
                    Message imageMsg = handler.obtainMessage(type);
                    Bundle imageBundle = new Bundle();
                    imageBundle.putString(WebViewClickHandler.KEY_URL, hitResultExtra);
                    imageMsg.setData(imageBundle);
                    handler.sendMessage(imageMsg);
                    break;
                case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                    EasyLog.i("test", "press url: " + extra);
                    Message urlMsg = handler.obtainMessage(type);
                    Bundle urlBundle = new Bundle();
                    urlBundle.putString(WebViewClickHandler.KEY_URL, hitResultExtra);
                    urlMsg.setData(urlBundle);
                    handler.sendMessage(urlMsg);
                    break;
                case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                    EasyLog.i("test", "press image anchor: " + extra);
                    // image anchor类型弹窗，需要获取图片url及父节点<a>标签的url
                    Message msg = handler.obtainMessage(type);
                    msg.setTarget(handler);
                    webView.requestFocusNodeHref(msg);
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}

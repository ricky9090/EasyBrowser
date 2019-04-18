package ricky.easybrowser.page.webpage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import ricky.easybrowser.R;

public class WebPageView extends LinearLayout {

    private EasyWebView webView;
    private ImageView goButton;
    private EditText webAddress;

    private OnWebPageChangeListener onWebPageChangeListener;

    public static WebPageView newInstance(Context context) {
        WebPageView view = new WebPageView(context);
        return view;
    }

    public WebPageView(Context context) {
        this(context, null);
    }

    public WebPageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebPageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.fragment_web_page, this);

        webView = findViewById(R.id.page_webview);

        webView.setWebViewClient(new WebViewClient() {
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
                if (onWebPageChangeListener != null) {
                    onWebPageChangeListener.onPageTitleChange(view.getTitle());
                }
            }
        });

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
    }

    private void loadInputUrl() {
        if (webAddress.getText() != null) {
            String url = webAddress.getText().toString();
            webView.loadUrl(url);
        }
    }

    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public void goBack() {
        webView.goBack();
    }

    public EasyWebView getWebView() {
        return webView;
    }

    public OnWebPageChangeListener getOnWebPageChangeListener() {
        return onWebPageChangeListener;
    }

    public void setOnWebPageChangeListener(OnWebPageChangeListener onWebPageChangeListener) {
        this.onWebPageChangeListener = onWebPageChangeListener;
    }

    public interface OnWebPageChangeListener {
        void onPageTitleChange(String newTitle);
    }
}

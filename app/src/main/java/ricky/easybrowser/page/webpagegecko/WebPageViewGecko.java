package ricky.easybrowser.page.webpagegecko;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;

import ricky.easybrowser.R;

public class WebPageViewGecko extends LinearLayout {

    private EasyGeckoView webView;
    GeckoSession session;
    GeckoRuntime runtime;
    private ImageView goButton;
    private EditText webAddress;

    private OnWebPageChangeListener onWebPageChangeListener;

    public static WebPageViewGecko newInstance(Context context) {
        WebPageViewGecko view = new WebPageViewGecko(context);
        return view;
    }

    public WebPageViewGecko(Context context) {
        this(context, null);
    }

    public WebPageViewGecko(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebPageViewGecko(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.fragment_web_page_gecko, this);

        webView = findViewById(R.id.page_webview);
        session = new GeckoSession();
        runtime = GeckoRuntime.create(context);

        session.open(runtime);
        webView.setSession(session);


        goButton = findViewById(R.id.goto_button);
        goButton.setOnClickListener(new OnClickListener() {
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
            session.loadUri(url);
        }
    }

    public void loadUrl(String url) {
        session.loadUri(url);
    }

    public boolean canGoBack() {
        return true;
    }

    public void goBack() {
        session.goBack();
    }

    public EasyGeckoView getWebView() {
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

package ricky.easybrowser.web.gecko;

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

import ricky.easybrowser.EasyApplication;
import ricky.easybrowser.R;
import ricky.easybrowser.web.IWebView;

public class PageGeckoView extends LinearLayout implements IWebView {

    private EasyGeckoView geckoView;
    GeckoSession session;
    GeckoRuntime runtime;
    private ImageView goButton;
    private EditText webAddress;
    OnWebInteractListener listener;

    public static PageGeckoView newInstance(Context context) {
        PageGeckoView view = new PageGeckoView(context);
        return view;
    }

    public PageGeckoView(Context context) {
        this(context, null);
    }

    public PageGeckoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageGeckoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.fragment_web_page_gecko, this);

        geckoView = findViewById(R.id.page_webview);
        session = new GeckoSession();

        runtime = ((EasyApplication) context.getApplicationContext()).getGeckoRuntime();

        session.open(runtime);
        geckoView.setSession(session);

        goButton = findViewById(R.id.goto_button);
        goButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadInputUrl();
                geckoView.releaseSession();
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

    @Override
    public void setOnWebInteractListener(OnWebInteractListener listener) {
        this.listener = listener;
    }

    @Override
    public void loadUrl(String url) {
        session.loadUri(url);
    }

    @Override
    public void goBack() {
        session.goBack();
    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    public void restoreSession() {
        if (session == null) {
            session = new GeckoSession();
        }
        if (!session.isOpen()) {
            session.open(runtime);
        }
        geckoView.setSession(session);
    }

    @Override
    public void releaseSession() {
        geckoView.releaseSession();
    }

    @Override
    public void onDestroy() {
        // donothing, for webView
    }
}

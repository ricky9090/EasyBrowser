package ricky.easybrowser.web.webkit;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.ContentLoadingProgressBar;

import java.io.InputStream;

import ricky.easybrowser.R;
import ricky.easybrowser.page.browser.IBrowser;
import ricky.easybrowser.page.browser.TabInfo;
import ricky.easybrowser.utils.EasyLog;
import ricky.easybrowser.utils.SharedPreferencesUtils;
import ricky.easybrowser.utils.StringUtils;
import ricky.easybrowser.web.IWebView;

public class PageNestedWebView extends LinearLayout implements IWebView {

    private EasyNestedWebView webView;
    private AddressBar addressBar;

    private ImageView goButton;
    private EditText webAddress;
    private ContentLoadingProgressBar progressBar;

    private OnWebInteractListener onWebInteractListener;

    private Context mContext;

    private boolean noPicMode;

    private AlertDialog imageActionsDialog = null;
    private AlertDialog urlActionsDialog = null;
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
    }

    private void configureWebView() {
        webView = findViewById(R.id.page_webview);
        webView.setWebChromeClient(new WebChromeClient() {
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
        });
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
                if (onWebInteractListener != null) {
                    onWebInteractListener.onPageTitleChange(view.getTitle());
                }
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                try {
                    String targetPath = request.getUrl().getPath();
                    if (StringUtils.isEmpty(targetPath)) {
                        return super.shouldInterceptRequest(view, request);
                    }
                    if (noPicMode && isPicResources(targetPath)) {
                        InputStream placeHolderIS = mContext.getAssets().open("emptyplaceholder.png");
                        return new WebResourceResponse("image/png", "UTF-8", placeHolderIS);
                    }
                } catch (Exception e) {

                }

                return super.shouldInterceptRequest(view, request);
            }

            private boolean isPicResources(String path) {
                if (path.endsWith(".jpg")
                        || path.endsWith(".jpeg")
                        || path.endsWith(".png")
                        || path.endsWith(".gif")) {
                    return true;
                }
                return false;
            }
        });
        webView.setOnLongClickListener(new OnLongClickListener() {
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
                        showImageActionsDialog();
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                        EasyLog.i("test", "press image anchor: " + extra);
                        // TODO 实现image anchor类型弹窗，需要获取图片url及父节点<a>标签的url
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                        EasyLog.i("test", "press url: " + extra);
                        showUrlActionsDialog();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void loadInputUrl() {
        if (webAddress.getText() != null) {
            String url = webAddress.getText().toString();
            this.loadUrl(url);
        }
    }

    @Override
    public void loadUrl(String url) {
        SharedPreferences sp = SharedPreferencesUtils.getSettingSP(getContext());
        if (sp != null) {
            noPicMode = sp.getBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, false);
        }
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
    public void setOnWebInteractListener(OnWebInteractListener listener) {
        this.onWebInteractListener = listener;
    }

    @Override
    public void releaseSession() {
        // donothing, for geckoView
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
    }

    /**
     * 点击图片弹窗
     */
    private void showImageActionsDialog() {
        if (imageActionsDialog != null) {
            imageActionsDialog.show();
            return;
        }
        AlertDialog.Builder imageDialogbuilder = new AlertDialog.Builder(mContext);
        imageDialogbuilder.setItems(R.array.image_actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {  // backstage
                    notifyAddNewTab(true);
                } else if (which == 1) {
                    notifyAddNewTab(false);
                }
            }
        });
        imageActionsDialog = imageDialogbuilder.create();
        imageActionsDialog.show();
    }

    /**
     * 点击网页链接弹窗
     */
    private void showUrlActionsDialog() {
        if (urlActionsDialog != null) {
            urlActionsDialog.show();
            return;
        }
        AlertDialog.Builder urlDialogbuilder = new AlertDialog.Builder(mContext);
        urlDialogbuilder.setItems(R.array.url_actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {  // backstage
                    notifyAddNewTab(true);
                } else if (which == 1) {
                    notifyAddNewTab(false);
                }
            }
        });
        urlActionsDialog = urlDialogbuilder.create();
        urlActionsDialog.show();
    }

    private void notifyAddNewTab(boolean backStage) {
        IBrowser browser = null;
        if (mContext instanceof IBrowser) {
            browser = (IBrowser) mContext;

        }
        if (browser == null) {
            return;
        }
        if (StringUtils.isEmpty(hitResultExtra)) {
            return;
        }
        Uri uri = null;
        try {
            uri = Uri.parse(hitResultExtra);
        } catch (Exception e) {
            uri = null;
        }
        if (uri == null) {
            return;
        }
        TabInfo tabInfo = new TabInfo();
        tabInfo.setTag(System.currentTimeMillis() + "");
        tabInfo.setTitle(mContext.getResources().getString(R.string.new_tab_welcome));
        tabInfo.setUri(uri);
        browser.provideTabController().onAddNewTab(tabInfo, backStage);
    }

}

package ricky.easybrowser.web;

public interface IWebView {

    void setOnWebInteractListener(OnWebInteractListener listener);
    void loadUrl(String url);
    void goBack();
    boolean canGoBack();
    void releaseSession();
    void onDestroy();


    interface OnWebInteractListener {
        void onPageTitleChange(String newTitle);
    }
}

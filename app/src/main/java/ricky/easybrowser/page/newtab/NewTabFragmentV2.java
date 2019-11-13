package ricky.easybrowser.page.newtab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ricky.easybrowser.R;
import ricky.easybrowser.common.TabConst;
import ricky.easybrowser.entity.bo.TabInfo;
import ricky.easybrowser.entity.dao.WebSite;
import ricky.easybrowser.utils.EasyLog;
import ricky.easybrowser.web.IWebView;
import ricky.easybrowser.web.webkit.PageNestedWebView;

/**
 * 新标签页Fragment。显示收藏站点快捷按钮
 */
public class NewTabFragmentV2 extends Fragment implements ITab {

    private String mTitle;
    private String mTag;
    private Uri loadUri;

    private FrameLayout frameLayout;
    private NewTabView newTabView;
    private IWebView pageWebView;


    private IWebView.OnWebInteractListener mListener;

    public NewTabFragmentV2() {
        // Required empty public constructor
    }

    public static NewTabFragmentV2 newInstance() {
        NewTabFragmentV2 fragment = new NewTabFragmentV2();
        return fragment;
    }

    public static NewTabFragmentV2 newInstance(String title) {
        NewTabFragmentV2 fragment = new NewTabFragmentV2();
        Bundle args = new Bundle();
        args.putString(TabConst.ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 创建新标签页，并指定标题与Tag
     *
     * @param title 页面标题，在快捷列表中显示
     * @param tag   页面tag，用于缓存
     * @return
     */
    public static NewTabFragmentV2 newInstance(String title, String tag) {
        NewTabFragmentV2 fragment = new NewTabFragmentV2();
        Bundle args = new Bundle();
        args.putString(TabConst.ARG_TITLE, title);
        args.putString(TabConst.ARG_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewTabFragmentV2 newInstance(String title, String tag, Uri uri) {
        NewTabFragmentV2 fragment = new NewTabFragmentV2();
        Bundle args = new Bundle();
        args.putString(TabConst.ARG_TITLE, title);
        args.putString(TabConst.ARG_TAG, tag);
        args.putParcelable(TabConst.ARG_URI, uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(TabConst.ARG_TITLE);
            mTag = savedInstanceState.getString(TabConst.ARG_TAG);
            Bundle resArg = new Bundle();
            resArg.putString(TabConst.ARG_TITLE, mTitle);
            resArg.putString(TabConst.ARG_TAG, mTag);
            setArguments(resArg);

            loadUri = savedInstanceState.getParcelable(TabConst.ARG_URI);
        } else if (getArguments() != null) {
            mTitle = getArguments().getString(TabConst.ARG_TITLE);
            mTag = getArguments().getString(TabConst.ARG_TAG);
            loadUri = getArguments().getParcelable(TabConst.ARG_URI);
        } else {
            mTag = "" + System.currentTimeMillis();
        }

        EasyLog.i("test", "title: " + mTitle);
        EasyLog.i("test", "tag: " + mTag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_tab_v2, container, false);
        frameLayout = rootView.findViewById(R.id.new_tab_v2_frame);

        newTabView = new NewTabView(getContext());
        newTabView.setTabTitle(mTitle);

        newTabView.setSiteListener(new SiteAdapterV2.OnSiteItemClickListener() {
            @Override
            public void onSiteItemClick(WebSite webSite) {
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority(webSite.getSiteUrl())
                        .build();
                loadUri = uri;

                addWebView(loadUri);
            }
        });
        if (loadUri == null) {
            frameLayout.addView(newTabView);
        } else {
            addWebView(loadUri);
        }

        return rootView;
    }

    private void addWebView(Uri uri) {
        frameLayout.removeAllViews();
        pageWebView = new PageNestedWebView(getContext());
        pageWebView.setOnWebInteractListener(new IWebView.OnWebInteractListener() {
            @Override
            public void onPageTitleChange(TabInfo tabInfo) {
                tabInfo.setTag(mTag);
                updateTitle(tabInfo);
            }
        });
        frameLayout.addView((View) pageWebView);
        pageWebView.loadUrl(uri.toString());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IWebView.OnWebInteractListener) {
            mListener = (IWebView.OnWebInteractListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IWebView.OnWebInteractListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onBackPressed() {
        if (frameLayout.getChildCount() <= 0) {
            return false;
        }

        View view = frameLayout.getChildAt(0);
        // 已经在网站快捷方式 不能返回
        if (view instanceof NewTabView) {
            return false;
        }

        // 不是IWebView 不能返回
        if (!(view instanceof IWebView)) {
            return false;
        }

        if (pageWebView.canGoBack()) {
            // 网页可返回，执行网页的返回逻辑
            pageWebView.goBack();
            return true;
        } else {
            // 网页不能返回，将WebView移除，替换成网站快捷方式
            frameLayout.removeAllViews();
            destroyWebView();
            frameLayout.addView(newTabView);
            try {
                mTitle = getContext().getString(R.string.new_tab_welcome);
                loadUri = null;
                updateTitle(provideTagInfo());
            } catch (Exception e) {

            }
            return true;
        }
    }

    @Override
    public TabInfo provideTagInfo() {
        TabInfo.create(this.mTag, this.mTitle, this.loadUri);
        return null;
    }

    @Override
    public void goForward() {
        if (frameLayout.getChildCount() <= 0) {
            return;
        }

        View view = frameLayout.getChildAt(0);
        if (view instanceof NewTabView) {
            return;
        }

        if (view instanceof IWebView) {
            IWebView iWebView = (IWebView) view;
            if (iWebView.canGoForward()) {
                iWebView.goForward();
            }
        }
    }

    @Override
    public void gotoHomePage() {
        if (frameLayout.getChildCount() <= 0) {
            return;
        }

        View view = frameLayout.getChildAt(0);
        if (view instanceof NewTabView) {
            return;
        }

        if (view instanceof IWebView) {
            frameLayout.removeAllViews();
            destroyWebView();
            frameLayout.addView(newTabView);
            try {
                mTitle = getContext().getString(R.string.new_tab_welcome);
                loadUri = null;
                updateTitle(provideTagInfo());
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyWebView();
        frameLayout.removeAllViews();
    }

    private void destroyWebView() {
        if (pageWebView != null) {
            pageWebView.onDestroy();
            pageWebView = null;
        }
    }

    private void updateTitle(TabInfo tabInfo) {
        mTitle = tabInfo.getTitle();
        if (getArguments() != null) {
            getArguments().putString(TabConst.ARG_TITLE, mTitle);
        }
        if (mListener != null) {
            mListener.onPageTitleChange(tabInfo);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TabConst.ARG_TITLE, mTitle);
        outState.putString(TabConst.ARG_TAG, mTag);
        outState.putParcelable(TabConst.ARG_URI, loadUri);
        EasyLog.i("test", "newtabfragment onsaveinstancestate: " + this.hashCode());
    }
}

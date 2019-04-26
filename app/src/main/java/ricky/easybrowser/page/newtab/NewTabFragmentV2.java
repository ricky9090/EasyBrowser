package ricky.easybrowser.page.newtab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import ricky.easybrowser.R;
import ricky.easybrowser.utils.EasyLog;
import ricky.easybrowser.utils.OnBackInteractionListener;
import ricky.easybrowser.web.IWebView;
import ricky.easybrowser.web.webkit.PageWebView;

/**
 * 新标签页Fragment。显示收藏站点快捷按钮
 */
public class NewTabFragmentV2 extends Fragment implements OnBackInteractionListener {

    public static final String ARG_TITLE = "param_title";
    public static final String ARG_TAG = "param_tag";

    private String mTitle;
    private String mTag;

    private FrameLayout frameLayout;
    private NewTabView newTabView;
    private IWebView pageWebView;

    private OnFragmentInteractionListener mListener;

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
        args.putString(ARG_TITLE, title);
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
        args.putString(ARG_TITLE, title);
        args.putString(ARG_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mTag = getArguments().getString(ARG_TAG);
            EasyLog.i("test", "title: " + mTitle);
            EasyLog.i("test", "tag: " + mTag);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_tab_fragment_v2, container, false);
        frameLayout = rootView.findViewById(R.id.new_tab_v2_frame);

        newTabView = new NewTabView(getContext());
        newTabView.setTabTitle(mTitle);

        newTabView.setSiteListener(new SiteAdapter.OnSiteItemClickListener() {
            @Override
            public void onSiteItemClick(SiteEntity siteEntity) {
                Uri uri = new Uri.Builder()
                        .scheme("http://")
                        .authority(siteEntity.getSiteUrl())
                        .build();

                frameLayout.removeAllViews();
                pageWebView = new PageWebView(getContext());
                pageWebView.setOnWebInteractListener(new IWebView.OnWebInteractListener() {
                    @Override
                    public void onPageTitleChange(String newTitle) {
                        updateTitle(newTitle);
                    }
                });
                frameLayout.addView((View) pageWebView);
                pageWebView.loadUrl(uri.getScheme() + uri.getHost());
            }
        });
        frameLayout.addView(newTabView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
        if (view instanceof NewTabView) {
            return false;
        }

        if (!(view instanceof PageWebView)) {
            return false;
        }
        if (pageWebView.canGoBack()) {
            pageWebView.goBack();
            return true;
        } else {
            frameLayout.removeAllViews();
            destroyWebView();
            frameLayout.addView(newTabView);
            try {
                String newTitle = getContext().getString(R.string.new_tab_welcome);
                updateTitle(newTitle);
            } catch (Exception e) {

            }
            return true;
        }
    }

    public void gotoHomePage() {
        if (frameLayout.getChildCount() <= 0) {
            return;
        }

        View view = frameLayout.getChildAt(0);
        if (view instanceof NewTabView) {
            return;
        }

        if (view instanceof PageWebView) {
            frameLayout.removeAllViews();
            destroyWebView();
            frameLayout.addView(newTabView);
            try {
                String newTitle = getContext().getString(R.string.new_tab_welcome);
                updateTitle(newTitle);
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

    private void updateTitle(String title) {
        mTitle = title;
        getArguments().putString(ARG_TITLE, mTitle);
        if (mListener != null) {
            mListener.onTabTitleChanged(mTitle);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onTabTitleChanged(String title);
    }
}
